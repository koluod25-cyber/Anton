FILE: Home.tsx

1) Replace the Bluetooth search result handling:

OLD:
const result = await BluetoothPrinterBridge.listPairedPrinters();
const printers = result.printers || [];
setPairedPrinters(printers);

NEW:
const result = await BluetoothPrinterBridge.listPairedPrinters();
const printers = Array.isArray(result?.printers) ? result.printers : [];
setPairedPrinters(printers);

2) Replace the catch block so the native error is visible:

OLD:
} catch (error) {
  console.error(error);
  toast.error("Gagal membaca printer Bluetooth. Pastikan izin Bluetooth diberikan.");
}

NEW:
} catch (error) {
  console.error("BLUETOOTH SEARCH ERROR:", error);
  const message = error instanceof Error ? error.message : String(error);
  toast.error(message || "Gagal membaca printer Bluetooth. Pastikan izin Bluetooth diberikan.");
}
