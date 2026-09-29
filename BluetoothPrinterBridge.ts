import { registerPlugin } from "@capacitor/core";

export type PairedPrinter = { name: string; address: string };
export type UsbPrinter = {
  deviceId: number;
  vendorId: number;
  productId: number;
  name: string;
  productName?: string;
  manufacturerName?: string;
};

export type BluetoothPrinterBridgePlugin = {
  listPairedPrinters(): Promise<{ printers: PairedPrinter[] }>;
  testConnection(options: { address: string }): Promise<{ success: boolean }>;
  testWifi(options: { host: string; port?: number }): Promise<{ success: boolean }>;
  listUsbPrinters(): Promise<{ printers: UsbPrinter[] }>;
  testUsb(options: { deviceId: number; vendorId?: number; productId?: number }): Promise<{ success: boolean }>;
  print(options: {
    connection: "Bluetooth" | "WiFi" | "USB" | "System";
    address?: string;
    host?: string;
    port?: number;
    deviceId?: number;
    vendorId?: number;
    productId?: number;
    copies?: number;
    paperWidth?: "58mm" | "80mm" | "A4";
    content: string;
  }): Promise<{ success: boolean }>;
};

export const BluetoothPrinterBridge = registerPlugin<BluetoothPrinterBridgePlugin>(
  "BluetoothPrinterBridge"
);
