package com.dishant.jewelcore.billing.service;

import com.dishant.jewelcore.billing.dto.GstConfig;
import com.dishant.jewelcore.billing.dto.SaleCreateRequest;
import com.dishant.jewelcore.billing.dto.SaleResponse;
import com.dishant.jewelcore.billing.entity.Customer;
import com.dishant.jewelcore.billing.entity.Invoice;
import com.dishant.jewelcore.billing.entity.Payment;
import com.dishant.jewelcore.billing.entity.Sale;
import com.dishant.jewelcore.billing.entity.SaleItem;
import com.dishant.jewelcore.billing.repository.CustomerRepository;
import com.dishant.jewelcore.billing.repository.InvoiceRepository;
import com.dishant.jewelcore.billing.repository.PaymentRepository;
import com.dishant.jewelcore.billing.repository.SaleRepository;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import com.dishant.jewelcore.inventory.stock.repository.InventoryItemRepository;
import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import com.dishant.jewelcore.inventory.jewellery.repository.JewelleryRepository;
import com.dishant.jewelcore.masterdata.metalprice.entity.DailyMetalPrice;
import com.dishant.jewelcore.masterdata.metalprice.repository.DailyMetalPriceRepository;
import com.dishant.jewelcore.pricing.service.PricingService;
import com.dishant.jewelcore.pricing.dto.PriceCalculationRequest;
import com.dishant.jewelcore.pricing.dto.PriceCalculationResponse;
import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final JewelleryRepository jewelleryRepository;
    private final DailyMetalPriceRepository dailyMetalPriceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final PricingService pricingService;
    private final GstCalculationService gstCalculationService;
    private final GstConfig gstConfig;

    private final AtomicLong saleNumberSequence = new AtomicLong(1000);
    private final AtomicLong invoiceNumberSequence = new AtomicLong(1000);

    @Transactional
    public SaleResponse createSale(SaleCreateRequest request, String username) {
        // Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getCustomerId()));

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Validate and process items
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;

        for (SaleCreateRequest.SaleItemRequest itemRequest : request.getItems()) {
            InventoryItem inventoryItem = inventoryItemRepository.findById(itemRequest.getInventoryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory item not found: " + itemRequest.getInventoryItemId()));

            // Check item availability
            if (inventoryItem.getStatus() != InventoryItemStatus.AVAILABLE) {
                throw new IllegalStateException("Item " + inventoryItem.getItemCode() + " is not available for sale");
            }

            // Get jewellery details
            Jewellery jewellery = inventoryItem.getJewellery();
            Long jewelleryId = jewellery.getId();

            // Get current metal price
            LocalDate today = LocalDate.now();
            DailyMetalPrice metalPrice = dailyMetalPriceRepository
                    .findByMetalIdAndPriceDate(jewellery.getMetal().getId(), today)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No metal price for today for metal: " + jewellery.getMetal().getName()));

            // Calculate price using pricing service
            PriceCalculationRequest priceRequest = new PriceCalculationRequest(itemRequest.getInventoryItemId(), today);
            PriceCalculationResponse priceResponse = pricingService.calculatePrice(priceRequest);

            // Calculate item-level details
            SaleItem saleItem = buildSaleItem(inventoryItem, jewellery, metalPrice, priceResponse, BigDecimal.ZERO);
            saleItems.add(saleItem);
            subtotal = subtotal.add(saleItem.getTotalAmount());
        }

        // Calculate totals
        BigDecimal taxableAmount = gstCalculationService.calculateTaxableAmount(subtotal, discountAmount);
        BigDecimal gstAmount = gstCalculationService.calculateGstAmount(taxableAmount);
        BigDecimal grandTotal = gstCalculationService.calculateGrandTotal(taxableAmount, gstAmount);

        // Create sale
        Sale sale = Sale.builder()
                .saleNumber(generateSaleNumber())
                .customer(customer)
                .saleDate(LocalDateTime.now())
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .taxableAmount(taxableAmount)
                .gstRate(gstConfig.getGstRate())
                .gstAmount(gstAmount)
                .grandTotal(grandTotal)
                .paymentStatus("PENDING")
                .notes(request.getNotes())
                .createdBy(userRepository.findByUsername(username).orElseThrow())
                .build();

        // Set items and associate with sale
        for (SaleItem item : saleItems) {
            item.setSale(sale);
        }
        sale.setItems(saleItems);

        // Process payments
        BigDecimal paidAmount = BigDecimal.ZERO;
        for (SaleCreateRequest.PaymentRequest paymentRequest : request.getPayments()) {
            Payment payment = Payment.builder()
                    .sale(sale)
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .amount(paymentRequest.getAmount())
                    .paymentDate(LocalDateTime.now())
                    .referenceNumber(paymentRequest.getReferenceNumber())
                    .notes(paymentRequest.getNotes())
                    .build();
            sale.getPayments().add(payment);
            paidAmount = paidAmount.add(paymentRequest.getAmount());
        }

        // Update payment status
        if (paidAmount.compareTo(grandTotal) >= 0) {
            sale.setPaymentStatus("PAID");
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            sale.setPaymentStatus("PARTIAL");
        }

        // Save sale (cascades to items and payments)
        Sale savedSale = saleRepository.save(sale);

        // Update inventory items to SOLD
        for (SaleItem item : savedSale.getItems()) {
            InventoryItem inventoryItem = inventoryItemRepository.findById(item.getInventoryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory item not found: " + item.getInventoryItemId()));
            inventoryItem.sell();
            inventoryItemRepository.save(inventoryItem);
        }

        // Generate invoice
        Invoice invoice = generateInvoice(savedSale);
        invoiceRepository.save(invoice);

        return mapToResponse(savedSale);
    }

    private SaleItem buildSaleItem(InventoryItem inventoryItem, Jewellery jewellery,
                                   DailyMetalPrice metalPrice, PriceCalculationResponse priceResponse,
                                   BigDecimal discountAmount) {
        BigDecimal metalRatePerGram = metalPrice.getPricePerGram();
        BigDecimal purityPercentage = jewellery.getPurity().getPercentage();
        BigDecimal pureMetalRate = metalRatePerGram.multiply(purityPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal metalValue = inventoryItem.getNetWeight().multiply(pureMetalRate);
        BigDecimal makingCharge = jewellery.getMakingCharge();
        BigDecimal itemPrice = priceResponse.finalPrice();

        BigDecimal gstAmount = gstCalculationService.calculateItemGst(itemPrice);
        BigDecimal totalAmount = itemPrice.add(gstAmount);

        return SaleItem.builder()
                .inventoryItemId(inventoryItem.getId())
                .jewelleryId(jewellery.getId())
                .metalName(jewellery.getMetal().getName())
                .purityName(jewellery.getPurity().getName())
                .purityPercentage(jewellery.getPurity().getPercentage())
                .grossWeight(inventoryItem.getGrossWeight())
                .stoneWeight(inventoryItem.getStoneWeight())
                .netWeight(inventoryItem.getNetWeight())
                .metalRatePerGram(metalRatePerGram)
                .pureMetalRatePerGram(pureMetalRate)
                .metalValue(metalValue)
                .makingCharge(makingCharge)
                .itemPrice(itemPrice)
                .gstRate(gstConfig.getGstRate())
                .gstAmount(gstAmount)
                .totalAmount(totalAmount)
                .build();
    }

    private String generateSaleNumber() {
        return "SAL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + saleNumberSequence.incrementAndGet();
    }

    private Invoice generateInvoice(Sale sale) {
        Customer customer = sale.getCustomer();
        String invoiceNumber = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + invoiceNumberSequence.incrementAndGet();

        String paymentMethod = sale.getPayments().isEmpty() ? null :
                sale.getPayments().get(0).getPaymentMethod();

        return Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .sale(sale)
                .businessName("JewelCore Jewellers")
                .businessAddress("123 Jewellery Street, Mumbai, Maharashtra 400001")
                .businessGstNumber("27AAAAA0000A1Z5")
                .customerName(sale.getCustomer().getName())
                .customerAddress(sale.getCustomer().getAddress())
                .customerGstNumber(sale.getCustomer().getGstNumber())
                .subtotal(sale.getSubtotal())
                .discountAmount(sale.getDiscountAmount())
                .taxableAmount(sale.getTaxableAmount())
                .gstRate(sale.getGstRate())
                .gstAmount(sale.getGstAmount())
                .grandTotal(sale.getGrandTotal())
                .paymentStatus(sale.getPaymentStatus())
                .paymentMethod(sale.getPayments().isEmpty() ? null : sale.getPayments().get(0).getPaymentMethod())
                .build();
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setSaleNumber(sale.getSaleNumber());
        response.setSaleDate(sale.getSaleDate());
        response.setSubtotal(sale.getSubtotal());
        response.setDiscountAmount(sale.getDiscountAmount());
        response.setTaxableAmount(sale.getTaxableAmount());
        response.setGstRate(sale.getGstRate());
        response.setGstAmount(sale.getGstAmount());
        response.setGrandTotal(sale.getGrandTotal());
        response.setPaymentStatus(sale.getPaymentStatus());

        // Customer
        SaleResponse.CustomerSummary customer = new SaleResponse.CustomerSummary();
        customer.setId(sale.getCustomer().getId());
        customer.setName(sale.getCustomer().getName());
        customer.setPhone(sale.getCustomer().getPhone());
        customer.setEmail(sale.getCustomer().getEmail());
        response.setCustomer(customer);

        // Items
        List<SaleResponse.SaleItemResponse> itemResponses = new ArrayList<>();
        for (SaleItem item : sale.getItems()) {
            SaleResponse.SaleItemResponse itemResp = new SaleResponse.SaleItemResponse();
            itemResp.setId(item.getId());
            itemResp.setInventoryItemId(item.getInventoryItemId());
            itemResp.setJewelleryName(item.getJewelleryName());
            itemResp.setMetalName(item.getMetalName());
            itemResp.setPurityName(item.getPurityName());
            itemResp.setPurityPercentage(item.getPurityPercentage());
            itemResp.setGrossWeight(item.getGrossWeight());
            itemResp.setStoneWeight(item.getStoneWeight());
            itemResp.setNetWeight(item.getNetWeight());
            itemResp.setMetalRatePerGram(item.getMetalRatePerGram());
            itemResp.setPureMetalRatePerGram(item.getPureMetalRatePerGram());
            itemResp.setMetalValue(item.getMetalValue());
            itemResp.setMakingCharge(item.getMakingCharge());
            itemResp.setItemPrice(item.getItemPrice());
            itemResp.setGstRate(item.getGstRate());
            itemResp.setGstAmount(item.getGstAmount());
            itemResp.setTotalAmount(item.getTotalAmount());
            itemResponses.add(itemResp);
        }
        response.setItems(itemResponses);

        // Payments
        List<SaleResponse.PaymentSummary> paymentResponses = new ArrayList<>();
        for (Payment payment : sale.getPayments()) {
            SaleResponse.PaymentSummary p = new SaleResponse.PaymentSummary();
            p.setId(payment.getId());
            p.setPaymentMethod(payment.getPaymentMethod());
            p.setAmount(payment.getAmount());
            p.setPaymentDate(payment.getPaymentDate());
            p.setReferenceNumber(payment.getReferenceNumber());
            paymentResponses.add(p);
        }
        response.setPayments(paymentResponses);

        return response;
    }
}