package com.dishant.jewelcore.billing.controller;

import com.dishant.jewelcore.billing.service.BarcodeService;
import com.dishant.jewelcore.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/barcode")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;

    @GetMapping(value = "/barcode/{itemCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable String itemCode) {
        byte[] image = barcodeService.generateBarcodeBytes(itemCode);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @GetMapping(value = "/qrcode/{itemCode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable String itemCode) {
        byte[] image = barcodeService.generateQRCodeBytes(itemCode);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @GetMapping("/barcode-base64/{itemCode}")
    public ResponseEntity<ApiResponse<String>> getBarcodeBase64(@PathVariable String itemCode) {
        String base64 = barcodeService.generateBarcodeBase64(itemCode);
        return ResponseEntity.ok(ApiResponse.success(base64));
    }

    @GetMapping("/qrcode-base64/{itemCode}")
    public ResponseEntity<ApiResponse<String>> getQRCodeBase64(@PathVariable String itemCode) {
        String base64 = barcodeService.generateQRCodeBase64(itemCode);
        return ResponseEntity.ok(ApiResponse.success(base64));
    }
}