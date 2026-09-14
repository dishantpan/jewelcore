with open('frontend/src/pages/Billing.jsx', 'r') as f:
    content = f.read()

old = 'const [search, setSearch] = useState("");\n    const [discount, setDiscount] = useState("");'
new = '''const [search, setSearch] = useState("");
    const [barcodeInput, setBarcodeInput] = useState("");
    const [scanningBarcode, setScanningBarcode] = useState(false);
    const [barcodeError, setBarcodeError] = useState("");
    const [discount, setDiscount] = useState("");'''

content = content.replace(old, new)

with open('frontend/src/pages/Billing.jsx', 'w') as f:
    f.write(content)
print('Done')