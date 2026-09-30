package com.antonservice.pos;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginMethod;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@CapacitorPlugin(name = "BluetoothPrinterBridge")
public class BluetoothPrinterBridge extends Plugin {

    private static final UUID SPP_UUID = UUID.fromString(
            "00001101-0000-1000-8000-00805F9B34FB");
    private static final String USB_PERMISSION_ACTION =
            "com.antonservice.pos.USB_PERMISSION";

    private UsbManager usbManager;

    private final BroadcastReceiver usbPermissionReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!USB_PERMISSION_ACTION.equals(intent.getAction())) return;
                }
            };

    @Override
    public void load() {
        super.load();
        usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(USB_PERMISSION_ACTION);
        if (Build.VERSION.SDK_INT >= 33) {
            getContext().registerReceiver(usbPermissionReceiver, filter,
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            getContext().registerReceiver(usbPermissionReceiver, filter);
        }
    }

    @Override
    protected void handleOnDestroy() {
        try { getContext().unregisterReceiver(usbPermissionReceiver); }
        catch (Exception ignored) { }
        super.handleOnDestroy();
    }

    @PluginMethod
    public void listPairedPrinters(PluginCall call) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        JSObject result = new JSObject();
        JSArray printers = new JSArray();
        if (adapter == null) { result.put("printers", printers); call.resolve(result); return; }
        if (Build.VERSION.SDK_INT >= 31 && getContext().checkSelfPermission(
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            call.reject("Izin BLUETOOTH_CONNECT belum diberikan"); return;
        }
        try {
            Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                JSObject item = new JSObject();
                String name = device.getName();
                if (name == null || name.trim().isEmpty()) name = "Bluetooth Printer";
                item.put("name", name);
                item.put("address", device.getAddress());
                printers.put(item);
            }
            result.put("printers", printers);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Gagal membaca printer Bluetooth: " + e.getMessage());
        }
    }

    @PluginMethod
    public void testConnection(PluginCall call) {
        String address = call.getString("address", "");
        if (address.trim().isEmpty()) { call.reject("Alamat Bluetooth printer belum diisi"); return; }
        if (Build.VERSION.SDK_INT >= 31 && getContext().checkSelfPermission(
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            call.reject("Izin BLUETOOTH_CONNECT belum diberikan"); return;
        }
        new Thread(() -> {
            BluetoothSocket socket = null;
            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter == null) throw new Exception("Bluetooth tidak tersedia");
                BluetoothDevice device = adapter.getRemoteDevice(address);
                socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();
                JSObject result = new JSObject();
                result.put("success", true);
                call.resolve(result);
            } catch (Exception e) {
                call.reject("Koneksi Bluetooth gagal: " + e.getMessage());
            } finally {
                try { if (socket != null) socket.close(); } catch (Exception ignored) { }
            }
        }).start();
    }

    @PluginMethod
    public void testWifi(PluginCall call) {
        String host = call.getString("host", "");
        int port = call.getInt("port", 9100);
        if (host.trim().isEmpty()) { call.reject("IP printer Wi-Fi belum diisi"); return; }
        new Thread(() -> {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
                JSObject result = new JSObject(); result.put("success", true); call.resolve(result);
            } catch (Exception e) { call.reject("Koneksi Wi-Fi gagal: " + e.getMessage()); }
        }).start();
    }

    @PluginMethod
    public void listUsbPrinters(PluginCall call) {
        ensureUsbManager();
        JSArray printers = new JSArray();
        JSObject result = new JSObject();
        if (usbManager == null) { result.put("printers", printers); call.resolve(result); return; }
        try {
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                JSObject item = new JSObject();
                item.put("deviceId", device.getDeviceId());
                item.put("vendorId", device.getVendorId());
                item.put("productId", device.getProductId());
                String name = device.getProductName();
                if (name == null || name.trim().isEmpty()) name = "USB Printer";
                item.put("name", name);
                if (device.getProductName() != null) item.put("productName", device.getProductName());
                if (device.getManufacturerName() != null) item.put("manufacturerName", device.getManufacturerName());
                printers.put(item);
            }
            result.put("printers", printers); call.resolve(result);
        } catch (Exception e) { call.reject("Gagal membaca perangkat USB: " + e.getMessage()); }
    }

    @PluginMethod
    public void testUsb(PluginCall call) {
        int deviceId = call.getInt("deviceId", -1);
        if (deviceId < 0) { call.reject("Perangkat USB belum dipilih"); return; }
        UsbDevice device = findUsbDevice(deviceId);
        if (device == null) { call.reject("Perangkat USB tidak ditemukan"); return; }
        new Thread(() -> {
            UsbDeviceConnection connection = null;
            try {
                connection = openUsbConnection(device);
                if (connection == null) throw new Exception("Tidak mendapat izin atau gagal membuka USB");
                connection.close();
                JSObject result = new JSObject(); result.put("success", true); call.resolve(result);
            } catch (Exception e) {
                if (connection != null) try { connection.close(); } catch (Exception ignored) { }
                call.reject("USB tidak dapat terhubung: " + e.getMessage());
            }
        }).start();
    }

    @PluginMethod
    public void print(PluginCall call) {
        String connection = call.getString("connection", "Bluetooth");
        String content = call.getString("content", "");
        int copies = Math.max(1, Math.min(call.getInt("copies", 1), 20));
        String paperWidth = call.getString("paperWidth", "58mm");
        if (content.trim().isEmpty()) { call.reject("Isi struk kosong"); return; }
        new Thread(() -> {
            try {
                byte[] data = buildEscPos(content, paperWidth);
                if ("WiFi".equalsIgnoreCase(connection)) {
                    printWifi(call.getString("host", ""), call.getInt("port", 9100), data, copies);
                } else if ("USB".equalsIgnoreCase(connection)) {
                    int deviceId = call.getInt("deviceId", -1);
                    if (deviceId < 0) throw new Exception("Perangkat USB belum dipilih");
                    UsbDevice device = findUsbDevice(deviceId);
                    if (device == null) throw new Exception("Printer USB tidak ditemukan");
                    printUsb(device, data, copies);
                } else {
                    printBluetooth(call.getString("address", ""), data, copies);
                }
                JSObject result = new JSObject(); result.put("success", true); call.resolve(result);
            } catch (Exception e) { call.reject("Cetak gagal: " + e.getMessage()); }
        }).start();
    }

    private void ensureUsbManager() {
        if (usbManager == null) usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
    }

    private UsbDevice findUsbDevice(int deviceId) {
        ensureUsbManager();
        if (usbManager == null) return null;
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if (device.getDeviceId() == deviceId) return device;
        }
        return null;
    }

    private UsbDeviceConnection openUsbConnection(UsbDevice device) throws Exception {
        ensureUsbManager();
        if (usbManager == null) throw new Exception("USB Manager tidak tersedia");
        if (!usbManager.hasPermission(device)) {
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= 31) flags |= PendingIntent.FLAG_MUTABLE;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getContext(), device.getDeviceId(), new Intent(USB_PERMISSION_ACTION), flags);
            usbManager.requestPermission(device, pendingIntent);
            throw new Exception("Izin USB belum diberikan. Izinkan akses USB kemudian tekan tes/cetak lagi.");
        }
        UsbInterface printerInterface = null; UsbEndpoint outEndpoint = null;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                        endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    printerInterface = usbInterface; outEndpoint = endpoint; break;
                }
            }
            if (outEndpoint != null) break;
        }
        if (printerInterface == null || outEndpoint == null) throw new Exception("Endpoint USB OUT printer tidak ditemukan");
        UsbDeviceConnection connection = usbManager.openDevice(device);
        if (connection == null) throw new Exception("Gagal membuka perangkat USB");
        if (!connection.claimInterface(printerInterface, true)) {
            connection.close(); throw new Exception("Tidak dapat mengakses interface printer USB");
        }
        return connection;
    }

    private void printUsb(UsbDevice device, byte[] data, int copies) throws Exception {
        ensureUsbManager();
        if (usbManager == null) throw new Exception("USB Manager tidak tersedia");
        if (!usbManager.hasPermission(device)) {
            openUsbConnection(device);
            throw new Exception("Izin USB belum diberikan. Setujui izin USB kemudian cetak kembali.");
        }
        UsbInterface printerInterface = null; UsbEndpoint outEndpoint = null;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                        endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    printerInterface = usbInterface; outEndpoint = endpoint; break;
                }
            }
            if (outEndpoint != null) break;
        }
        if (printerInterface == null || outEndpoint == null) throw new Exception("Printer USB tidak memiliki endpoint OUT ESC/POS");
        UsbDeviceConnection connection = usbManager.openDevice(device);
        if (connection == null) throw new Exception("Gagal membuka perangkat USB");
        try {
            if (!connection.claimInterface(printerInterface, true)) throw new Exception("Tidak dapat mengakses interface printer USB");
            for (int i = 0; i < copies; i++) {
                int sent = connection.bulkTransfer(outEndpoint, data, data.length, 5000);
                if (sent < 0) throw new Exception("Gagal mengirim data ke printer USB");
            }
            connection.releaseInterface(printerInterface);
        } finally { connection.close(); }
    }

    private void printBluetooth(String address, byte[] data, int copies) throws Exception {
        if (address == null || address.trim().isEmpty()) throw new Exception("Alamat Bluetooth printer belum diisi");
        if (Build.VERSION.SDK_INT >= 31 && getContext().checkSelfPermission(
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("Izin BLUETOOTH_CONNECT belum diberikan");
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) throw new Exception("Bluetooth tidak tersedia");
        BluetoothDevice device = adapter.getRemoteDevice(address);
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            socket.connect();
            OutputStream output = socket.getOutputStream();
            for (int i = 0; i < copies; i++) { output.write(data); output.flush(); }
        } finally { if (socket != null) try { socket.close(); } catch (Exception ignored) { } }
    }

    private void printWifi(String host, int port, byte[] data, int copies) throws Exception {
        if (host == null || host.trim().isEmpty()) throw new Exception("IP printer Wi-Fi belum diisi");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            OutputStream output = socket.getOutputStream();
            for (int i = 0; i < copies; i++) { output.write(data); output.flush(); }
        }
    }

    private byte[] buildEscPos(String content, String paperWidth) {
        String normalized = content.replace("\r\n", "\n").replace("\r", "\n");
        int maxChars = "80mm".equalsIgnoreCase(paperWidth) ? 48 : 32;
        if ("A4".equalsIgnoreCase(paperWidth)) maxChars = 80;
        StringBuilder wrapped = new StringBuilder();
        for (String line : normalized.split("\n", -1)) {
            if (line.length() <= maxChars) { wrapped.append(line).append('\n'); continue; }
            int start = 0;
            while (start < line.length()) {
                int end = Math.min(start + maxChars, line.length());
                wrapped.append(line, start, end).append('\n'); start = end;
            }
        }
        byte[] body = wrapped.toString().getBytes(StandardCharsets.UTF_8);
        byte[] init = new byte[]{0x1B, 0x40};
        byte[] feed = new byte[]{0x1B, 0x64, 0x03};
        byte[] cut = new byte[]{0x1D, 0x56, 0x00};
        byte[] result = new byte[init.length + body.length + feed.length + cut.length];
        int offset = 0;
        System.arraycopy(init, 0, result, offset, init.length); offset += init.length;
        System.arraycopy(body, 0, result, offset, body.length); offset += body.length;
        System.arraycopy(feed, 0, result, offset, feed.length); offset += feed.length;
        System.arraycopy(cut, 0, result, offset, cut.length);
        return result;
    }
}
