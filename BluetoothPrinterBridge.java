package com.antonservice.pos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CapacitorPlugin(name = "BluetoothPrinterBridge")
public class BluetoothPrinterBridge extends Plugin {

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean hasBluetoothPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @PluginMethod
    public void listPairedPrinters(PluginCall call) {
        try {
            if (!hasBluetoothPermission()) {
                call.reject("Izin BLUETOOTH_CONNECT belum diberikan.");
                return;
            }

            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                call.reject("Perangkat tidak mendukung Bluetooth.");
                return;
            }

            Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
            List<JSObject> printers = new ArrayList<>();

            for (BluetoothDevice device : bondedDevices) {
                JSObject item = new JSObject();
                item.put("name", device.getName() != null ? device.getName() : "Bluetooth Printer");
                item.put("address", device.getAddress());
                printers.add(item);
            }

            JSObject result = new JSObject();
            result.put("printers", printers);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Gagal membaca printer Bluetooth: " + e.getMessage(), e);
        }
    }

    /**
     * Connect to a classic Bluetooth SPP printer.
     * Some POS-58B/58mm thermal printers accept secure RFCOMM while others
     * only work with the insecure RFCOMM socket. Try both before failing.
     */
    private BluetoothSocket connectBluetoothSocket(BluetoothDevice device) throws Exception {
        BluetoothSocket socket = null;
        Exception secureError = null;

        try {
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            socket.connect();
            return socket;
        } catch (Exception e) {
            secureError = e;
            closeSocket(socket);
        }

        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            socket.connect();
            return socket;
        } catch (Exception insecureError) {
            closeSocket(socket);

            String secureMessage = secureError != null && secureError.getMessage() != null
                    ? secureError.getMessage()
                    : "secure RFCOMM gagal";
            String insecureMessage = insecureError.getMessage() != null
                    ? insecureError.getMessage()
                    : "insecure RFCOMM gagal";

            throw new Exception(
                    "Bluetooth SPP gagal. Secure: " + secureMessage
                            + " | Insecure: " + insecureMessage,
                    insecureError
            );
        }
    }

    private void closeSocket(BluetoothSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    @PluginMethod
    public void testConnection(PluginCall call) {
        BluetoothSocket socket = null;
        try {
            if (!hasBluetoothPermission()) {
                call.reject("Izin BLUETOOTH_CONNECT belum diberikan.");
                return;
            }

            String address = call.getString("address", "");
            if (address.trim().isEmpty()) {
                call.reject("Alamat Bluetooth printer belum dipilih.");
                return;
            }

            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                call.reject("Perangkat tidak mendukung Bluetooth.");
                return;
            }

            BluetoothDevice device = adapter.getRemoteDevice(address.trim());
            socket = connectBluetoothSocket(device);

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Bluetooth printer terhubung.");
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Printer Bluetooth tidak dapat terhubung: " + e.getMessage(), e);
        } finally {
            closeSocket(socket);
        }
    }

    @PluginMethod
    public void testWifi(PluginCall call) {
        Socket socket = null;
        try {
            String host = call.getString("host", "");
            int port = call.getInt("port", 9100);

            if (host.trim().isEmpty()) {
                call.reject("IP printer Wi-Fi belum diisi.");
                return;
            }

            socket = new Socket();
            socket.connect(new InetSocketAddress(host.trim(), port), 5000);

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Printer Wi-Fi terhubung.");
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Printer Wi-Fi tidak dapat terhubung: " + e.getMessage(), e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @PluginMethod
    public void listUsbPrinters(PluginCall call) {
        try {
            UsbManager usbManager = (UsbManager) getContext().getSystemService(android.content.Context.USB_SERVICE);
            if (usbManager == null) {
                call.reject("USB Manager tidak tersedia.");
                return;
            }

            List<JSObject> printers = new ArrayList<>();
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                JSObject item = new JSObject();
                item.put("deviceId", device.getDeviceId());
                item.put("vendorId", device.getVendorId());
                item.put("productId", device.getProductId());
                item.put("name", device.getProductName() != null ? device.getProductName() : "USB Printer");
                printers.add(item);
            }

            JSObject result = new JSObject();
            result.put("printers", printers);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Gagal membaca printer USB: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void testUsb(PluginCall call) {
        UsbDeviceConnection connection = null;
        try {
            UsbManager usbManager = (UsbManager) getContext().getSystemService(android.content.Context.USB_SERVICE);
            int deviceId = call.getInt("deviceId", -1);
            if (usbManager == null || deviceId < 0) {
                call.reject("Printer USB belum dipilih.");
                return;
            }

            UsbDevice device = null;
            for (UsbDevice item : usbManager.getDeviceList().values()) {
                if (item.getDeviceId() == deviceId) {
                    device = item;
                    break;
                }
            }

            if (device == null) {
                call.reject("Printer USB tidak ditemukan.");
                return;
            }
            if (!usbManager.hasPermission(device)) {
                call.reject("Izin USB belum diberikan.");
                return;
            }

            connection = usbManager.openDevice(device);
            if (connection == null) {
                call.reject("Gagal membuka printer USB.");
                return;
            }

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Printer USB terhubung.");
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Printer USB tidak dapat terhubung: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @PluginMethod
    public void print(PluginCall call) {
        String connectionType = call.getString("connection", "Bluetooth");
        int copies = Math.max(1, Math.min(20, call.getInt("copies", 1)));
        String paperWidth = call.getString("paperWidth", "58mm");
        String content = call.getString("content", "");

        try {
            if ("WiFi".equalsIgnoreCase(connectionType)) {
                printWifi(call, content, copies);
            } else if ("USB".equalsIgnoreCase(connectionType)) {
                printUsb(call, content, copies, paperWidth);
            } else {
                printBluetooth(call, content, copies, paperWidth);
            }
        } catch (Exception e) {
            call.reject("Cetak gagal: " + e.getMessage(), e);
        }
    }

    private void printBluetooth(PluginCall call, String content, int copies, String paperWidth) throws Exception {
        if (!hasBluetoothPermission()) {
            throw new Exception("Izin BLUETOOTH_CONNECT belum diberikan.");
        }

        String address = call.getString("address", "");
        if (address.trim().isEmpty()) {
            throw new Exception("Alamat Bluetooth printer belum dipilih.");
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            throw new Exception("Perangkat tidak mendukung Bluetooth.");
        }

        BluetoothDevice device = adapter.getRemoteDevice(address.trim());
        BluetoothSocket socket = null;

        try {
            socket = connectBluetoothSocket(device);
            OutputStream output = socket.getOutputStream();
            byte[] data = buildEscPos(content, paperWidth);

            for (int i = 0; i < copies; i++) {
                output.write(data);
                output.flush();
            }

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Data berhasil dikirim ke printer Bluetooth.");
            call.resolve(result);
        } finally {
            closeSocket(socket);
        }
    }

    private void printWifi(PluginCall call, String content, int copies) throws Exception {
        String host = call.getString("host", "");
        int port = call.getInt("port", 9100);
        if (host.trim().isEmpty()) {
            throw new Exception("IP printer Wi-Fi belum diisi.");
        }

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host.trim(), port), 5000);
            OutputStream output = socket.getOutputStream();
            byte[] data = buildEscPos(content, call.getString("paperWidth", "58mm"));
            for (int i = 0; i < copies; i++) {
                output.write(data);
                output.flush();
            }

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Data berhasil dikirim ke printer Wi-Fi.");
            call.resolve(result);
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void printUsb(PluginCall call, String content, int copies, String paperWidth) throws Exception {
        UsbManager usbManager = (UsbManager) getContext().getSystemService(android.content.Context.USB_SERVICE);
        int deviceId = call.getInt("deviceId", -1);
        if (usbManager == null || deviceId < 0) {
            throw new Exception("Printer USB belum dipilih.");
        }

        UsbDevice device = null;
        for (UsbDevice item : usbManager.getDeviceList().values()) {
            if (item.getDeviceId() == deviceId) {
                device = item;
                break;
            }
        }
        if (device == null) {
            throw new Exception("Printer USB tidak ditemukan.");
        }
        if (!usbManager.hasPermission(device)) {
            throw new Exception("Izin USB belum diberikan.");
        }

        UsbInterface usbInterface = null;
        UsbEndpoint outEndpoint = null;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface candidate = device.getInterface(i);
            for (int j = 0; j < candidate.getEndpointCount(); j++) {
                UsbEndpoint endpoint = candidate.getEndpoint(j);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                        && endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    usbInterface = candidate;
                    outEndpoint = endpoint;
                    break;
                }
            }
            if (outEndpoint != null) break;
        }

        if (usbInterface == null || outEndpoint == null) {
            throw new Exception("Endpoint OUT printer USB tidak ditemukan.");
        }

        UsbDeviceConnection connection = usbManager.openDevice(device);
        if (connection == null) {
            throw new Exception("Gagal membuka printer USB.");
        }

        try {
            if (!connection.claimInterface(usbInterface, true)) {
                throw new Exception("Gagal mengambil interface printer USB.");
            }

            byte[] data = buildEscPos(content, paperWidth);
            for (int i = 0; i < copies; i++) {
                int offset = 0;
                while (offset < data.length) {
                    int length = Math.min(16384, data.length - offset);
                    int written = connection.bulkTransfer(outEndpoint, data, offset, length, 10000);
                    if (written <= 0) {
                        throw new Exception("Data USB gagal dikirim.");
                    }
                    offset += written;
                }
            }

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Data berhasil dikirim ke printer USB.");
            call.resolve(result);
        } finally {
            try {
                connection.releaseInterface(usbInterface);
            } catch (Exception ignored) {
            }
            connection.close();
        }
    }

    private byte[] buildEscPos(String content, String paperWidth) {
        int maxChars;
        if ("80mm".equalsIgnoreCase(paperWidth)) {
            maxChars = 48;
        } else if ("A4".equalsIgnoreCase(paperWidth)) {
            maxChars = 80;
        } else {
            maxChars = 32;
        }

        String normalized = content == null ? "" : content.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);
        StringBuilder body = new StringBuilder();

        for (String line : lines) {
            if (line.length() <= maxChars) {
                body.append(line).append('\n');
            } else {
                int start = 0;
                while (start < line.length()) {
                    int end = Math.min(start + maxChars, line.length());
                    body.append(line, start, end).append('\n');
                    start = end;
                }
            }
        }

        byte[] init = new byte[]{0x1B, 0x40};
        byte[] text = body.toString().getBytes(StandardCharsets.UTF_8);
        byte[] feed = new byte[]{0x0A, 0x0A, 0x0A};
        byte[] cut = new byte[]{0x1D, 0x56, 0x00};

        byte[] output = new byte[init.length + text.length + feed.length + cut.length];
        System.arraycopy(init, 0, output, 0, init.length);
        System.arraycopy(text, 0, output, init.length, text.length);
        System.arraycopy(feed, 0, output, init.length + text.length, feed.length);
        System.arraycopy(cut, 0, output, init.length + text.length + feed.length, cut.length);
        return output;
    }
}
