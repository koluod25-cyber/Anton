import { registerPlugin } from "@capacitor/core";

export type PairedPrinter = {
  name: string;
  address: string;
};

export type BluetoothPrinterBridgePlugin = {
  listPairedPrinters(): Promise<{
    printers: PairedPrinter[];
  }>;

  testConnection(options: {
    address: string;
  }): Promise<{
    success: boolean;
  }>;

  testWifi(options: {
    host: string;
    port?: number;
  }): Promise<{
    success: boolean;
  }>;

  print(options: {
    connection: "Bluetooth" | "WiFi";
    address?: string;
    host?: string;
    port?: number;
    copies?: number;
    paperWidth?: "58mm" | "80mm" | "A4";
    content: string;
  }): Promise<{
    success: boolean;
  }>;
};

export const BluetoothPrinterBridge =
  registerPlugin<BluetoothPrinterBridgePlugin>(
    "BluetoothPrinterBridge"
  );
