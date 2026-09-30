package com.antonservice.pos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;

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
import java.util.UUID;

@CapacitorPlugin(name = "BluetoothPrinterBridge")
public class BluetoothPrinterBridge extends Plugin {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean hasBluetoothConnectPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                getContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    @PluginMethod
    public void listPairedPrinters(PluginCall call) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!hasBluetoothConnectPermission()) {
            call.reject("Izin BLUETOOTH_CONNECT belum diberikan. Buka Pengaturan > Aplikasi > AntonPOS > Izin, lalu izinkan Perangkat di sekitar.");
            return;
        }
        try {
            if (adapter == null) {
                call.reject("Perangkat Android tidak mendukung Bluetooth.");
                return;
            }
            if (!adapter.isEnabled()) {
                call.reject("Bluetooth belum aktif. Aktifkan Bluetooth terlebih dahulu.");
                return;
            }

            List<JSObject> result = new ArrayList<>();
            for (BluetoothDevice device : adapter.getBondedDevices()) {
                JSObject item = new JSObject();
                item.put("name", safeDeviceName(device));
                item.put("address", device.getAddress());
                item.put("type", device.getType());
                result.add(item);
            }

            JSObject response = new JSObject();
            response.put("printers", result);
            response.put("count", result.size());
            call.resolve(response);
        } catch (SecurityException e) {
            call.reject("Android menolak akses perangkat Bluetooth. Pastikan izin Perangkat di sekitar/Bluetooth diizinkan.", e);
        } catch (Exception e) {
            call.reject("Gagal membaca printer Bluetooth: " + safeMessage(e), e);
        }
    }

    private String safeDeviceName(BluetoothDevice device) {
        try {
            String name = device.getName();
            return name == null || name.trim().isEmpty() ? "Bluetooth Printer" : name;
        } catch (SecurityException e) {
            return "Bluetooth Printer";
        }
    }

    @PluginMethod
    public void testConnection(PluginCall call) {
        String address = call.getString("address");
        if (address == null || address.trim().isEmpty()) {
            call.reject("Alamat Bluetooth printer belum dipilih.");
            return;
        }
        if (!hasBluetoothConnectPermission()) {
            call.reject("Izin BLUETOOTH_CONNECT belum diberikan.");
            return;
        }

        BluetoothSocket socket = null;
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null || !adapter.isEnabled()) {
                call.reject("Bluetooth tidak tersedia atau belum aktif.");
                return;
            }
            BluetoothDevice device = adapter.getRemoteDevice(address.trim());
            socket = connectBluetoothSocket(device);
            JSObject result = new JSObject();
            result.put("success", true);
            result.put("name", safeDeviceName(device));
            result.put("address", device.getAddress());
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Gagal terhubung ke printer Bluetooth: " + safeMessage(e), e);
        } finally {
            closeSocket(socket);
        }
    }

    @PluginMethod
    public void print(PluginCall call) {
        String connection = call.getString("connection", "Bluetooth");
        String content = call.getString("content", "");
        int copies = Math.max(1, Math.min(20, call.getInt("copies", 1)));
        String paperWidth = call.getString("paperWidth", "58mm");

        try {
            if ("WiFi".equalsIgnoreCase(connection)) {
                printWifi(call.getString("host"), call.getInt("port", 9100), content, copies, paperWidth);
            } else if ("USB".equalsIgnoreCase(connection)) {
                call.reject("Pencetakan USB menggunakan jalur native USB yang ada pada versi proyek Anda. Gunakan file bridge USB yang sudah terpasang jika fitur USB diperlukan.");
                return;
            } else {
                printBluetooth(call.getString("address"), content, copies, paperWidth);
            }
            JSObject result = new JSObject();
            result.put("success", true);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Cetak gagal: " + safeMessage(e), e);
        }
    }

    private void printBluetooth(String address, String content, int copies, String paperWidth) throws Exception {
        if (!hasBluetoothConnectPermission()) throw new Exception("Izin BLUETOOTH_CONNECT belum diberikan.");
        if (address == null || address.trim().isEmpty()) throw new Exception("Alamat Bluetooth printer belum dipilih.");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) throw new Exception("Bluetooth belum aktif.");

        BluetoothDevice device = adapter.getRemoteDevice(address.trim());
        BluetoothSocket socket = null;
        try {
            socket = connectBluetoothSocket(device);
            OutputStream output = socket.getOutputStream();
            byte[] data = buildEscPos(content, paperWidth);
            for (int i = 0; i < copies; i++) {
                output.write(data);
                output.flush();
                if (i + 1 < copies) Thread.sleep(250);
            }
        } finally {
            closeSocket(socket);
        }
    }

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
            throw new Exception("Bluetooth SPP gagal. Secure: " + safeMessage(secureError) + " | Insecure: " + safeMessage(insecureError));
        }
    }

    private void printWifi(String host, int port, String content, int copies, String paperWidth) throws Exception {
        if (host == null || host.trim().isEmpty()) throw new Exception("IP printer Wi-Fi belum diisi.");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host.trim(), port), 5000);
            OutputStream output = socket.getOutputStream();
            byte[] data = buildEscPos(content, paperWidth);
            for (int i = 0; i < copies; i++) {
                output.write(data);
                output.flush();
                if (i + 1 < copies) Thread.sleep(250);
            }
        }
    }

    @PluginMethod
    public void testWifi(PluginCall call) {
        String host = call.getString("host");
        int port = call.getInt("port", 9100);
        if (host == null || host.trim().isEmpty()) { call.reject("IP printer Wi-Fi belum diisi."); return; }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host.trim(), port), 5000);
            JSObject result = new JSObject(); result.put("success", true); call.resolve(result);
        } catch (Exception e) { call.reject("Wi-Fi printer tidak dapat terhubung: " + safeMessage(e), e); }
    }

    private byte[] buildEscPos(String content, String paperWidth) {
        int maxChars = "80mm".equalsIgnoreCase(paperWidth) ? 48 : "A4".equalsIgnoreCase(paperWidth) ? 80 : 32;
        String[] lines = content.replace("\r", "").split("\n", -1);
        StringBuilder normalized = new StringBuilder();
        for (String line : lines) {
            if (line.length() <= maxChars) normalized.append(line);
            else {
                int start = 0;
                while (start < line.length()) {
                    int end = Math.min(start + maxChars, line.length());
                    normalized.append(line, start, end);
                    if (end < line.length()) normalized.append('\n');
                    start = end;
                }
            }
            normalized.append('\n');
        }
        byte[] body = normalized.toString().getBytes(StandardCharsets.UTF_8);
        byte[] init = new byte[]{0x1B, 0x40};
        byte[] feed = new byte[]{0x1B, 0x64, 0x03};
        byte[] cut = new byte[]{0x1D, 0x56, 0x00};
        byte[] all = new byte[init.length + body.length + feed.length + cut.length];
        int pos = 0;
        System.arraycopy(init, 0, all, pos, init.length); pos += init.length;
        System.arraycopy(body, 0, all, pos, body.length); pos += body.length;
        System.arraycopy(feed, 0, all, pos, feed.length); pos += feed.length;
        System.arraycopy(cut, 0, all, pos, cut.length);
        return all;
    }

    private void closeSocket(BluetoothSocket socket) { if (socket != null) try { socket.close(); } catch (Exception ignored) {} }
    private String safeMessage(Exception e) {
        if (e == null) return "unknown error";
        String message = e.getMessage();
        return message == null || message.trim().isEmpty() ? e.getClass().getSimpleName() : message;
    }
}
