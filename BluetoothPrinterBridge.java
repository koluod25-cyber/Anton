package com.antonservice.pos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Base64;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
        if (!hasBluetoothConnectPermission()) { call.reject("Izin BLUETOOTH_CONNECT belum diberikan. Buka Pengaturan > Aplikasi > AntonPOS > Izin, lalu izinkan Perangkat di sekitar."); return; }
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) { call.reject("Perangkat Android tidak mendukung Bluetooth."); return; }
            if (!adapter.isEnabled()) { call.reject("Bluetooth belum aktif. Aktifkan Bluetooth terlebih dahulu."); return; }
            JSArray printers = new JSArray();
            for (BluetoothDevice device : adapter.getBondedDevices()) {
                JSObject item = new JSObject();
                item.put("name", safeDeviceName(device));
                item.put("address", device.getAddress());
                item.put("type", device.getType());
                printers.put(item);
            }
            JSObject response = new JSObject(); response.put("printers", printers); response.put("count", printers.length()); call.resolve(response);
        } catch (SecurityException e) { call.reject("Android menolak akses perangkat Bluetooth. Pastikan izin Perangkat di sekitar/Bluetooth diizinkan.", e); }
        catch (Exception e) { call.reject("Gagal membaca printer Bluetooth: " + safeMessage(e), e); }
    }

    private String safeDeviceName(BluetoothDevice device) {
        try { String name = device.getName(); return name == null || name.trim().isEmpty() ? "Bluetooth Printer" : name; }
        catch (SecurityException e) { return "Bluetooth Printer"; }
    }

    @PluginMethod
    public void testConnection(PluginCall call) {
        String address = call.getString("address");
        if (address == null || address.trim().isEmpty()) { call.reject("Alamat Bluetooth printer belum dipilih."); return; }
        if (!hasBluetoothConnectPermission()) { call.reject("Izin BLUETOOTH_CONNECT belum diberikan."); return; }
        BluetoothSocket socket = null;
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) throw new Exception("Bluetooth tidak tersedia.");
            if (!adapter.isEnabled()) throw new Exception("Bluetooth belum aktif.");
            try { adapter.cancelDiscovery(); } catch (Exception ignored) {}
            BluetoothDevice device = adapter.getRemoteDevice(address.trim());
            socket = connectBluetoothSocket(device);
            JSObject result = new JSObject(); result.put("success", true); result.put("name", safeDeviceName(device)); result.put("address", device.getAddress()); call.resolve(result);
        } catch (Exception e) { call.reject("Gagal terhubung ke printer Bluetooth: " + safeMessage(e)); }
        finally { closeSocket(socket); }
    }

    @PluginMethod
    public void print(PluginCall call) {
        String connection = call.getString("connection", "Bluetooth");
        String content = call.getString("content", "");
        String logoBase64 = call.getString("logoBase64", null);
        String warrantyCode = call.getString("warrantyCode", "");
        int copies = Math.max(1, Math.min(20, call.getInt("copies", 1)));
        String paperWidth = call.getString("paperWidth", "58mm");
        try {
            if ("WiFi".equalsIgnoreCase(connection)) printWifi(call.getString("host"), call.getInt("port", 9100), content, copies, paperWidth, logoBase64);
            else if ("USB".equalsIgnoreCase(connection)) { call.reject("Pencetakan USB belum melalui bridge Bluetooth ini."); return; }
            else printBluetooth(
        call.getString("address"),
        content,
        copies,
        paperWidth,
        logoBase64,
        warrantyCode
);
            JSObject result = new JSObject(); result.put("success", true); call.resolve(result);
        } catch (Exception e) { call.reject("Cetak gagal: " + safeMessage(e)); }
    }

    private void printBluetooth(
        String address,
        String content,
        int copies,
        String paperWidth,
        String logoBase64,
        String warrantyCode
) throws Exception {
        if (!hasBluetoothConnectPermission()) throw new Exception("Izin BLUETOOTH_CONNECT belum diberikan.");
        if (address == null || address.trim().isEmpty()) throw new Exception("Alamat Bluetooth printer belum dipilih.");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) throw new Exception("Bluetooth tidak tersedia.");
        if (!adapter.isEnabled()) throw new Exception("Bluetooth belum aktif.");
        try { adapter.cancelDiscovery(); } catch (Exception ignored) {}
        BluetoothDevice device = adapter.getRemoteDevice(address.trim());
        BluetoothSocket socket = null;
        try {
            socket = connectBluetoothSocket(device);
            OutputStream output = socket.getOutputStream();
            byte[] data = buildEscPos(
        content,
        paperWidth,
        logoBase64,
        warrantyCode
);
            for (int i = 0; i < copies; i++) { output.write(data); output.flush(); Thread.sleep(700); }
            Thread.sleep(300);
        } finally { closeSocket(socket); }
    }

    private BluetoothSocket connectBluetoothSocket(BluetoothDevice device) throws Exception {
        BluetoothSocket socket = null; Exception secureError = null;
        try { socket = device.createRfcommSocketToServiceRecord(SPP_UUID); socket.connect(); return socket; }
        catch (Exception e) { secureError = e; closeSocket(socket); }
        try { socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID); socket.connect(); return socket; }
        catch (Exception insecureError) { closeSocket(socket); }
        try {
            java.lang.reflect.Method method = device.getClass().getMethod("createRfcommSocket", int.class);
            socket = (BluetoothSocket) method.invoke(device, 1); socket.connect(); return socket;
        } catch (Exception channelError) {
            closeSocket(socket);
            throw new Exception("Bluetooth SPP gagal. Secure: " + safeMessage(secureError) + " | Insecure: " + safeMessage(channelError));
        }
    }

    private void printWifi(String host, int port, String content, int copies, String paperWidth, String logoBase64) throws Exception {
        if (host == null || host.trim().isEmpty()) throw new Exception("IP printer Wi-Fi belum diisi.");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host.trim(), port), 5000);
            OutputStream output = socket.getOutputStream(); byte[] data = buildEscPos(content, paperWidth, logoBase64);
            for (int i = 0; i < copies; i++) { output.write(data); output.flush(); Thread.sleep(300); }
        }
    }

    @PluginMethod
    public void testWifi(PluginCall call) {
        String host = call.getString("host"); int port = call.getInt("port", 9100);
        if (host == null || host.trim().isEmpty()) { call.reject("IP printer Wi-Fi belum diisi."); return; }
        try (Socket socket = new Socket()) { socket.connect(new InetSocketAddress(host.trim(), port), 5000); JSObject result = new JSObject(); result.put("success", true); call.resolve(result); }
        catch (Exception e) { call.reject("Wi-Fi printer tidak dapat terhubung: " + safeMessage(e)); }
    }

    private byte[] buildEscPos(String content, String paperWidth, String logoBase64) {
        int maxChars = "80mm".equalsIgnoreCase(paperWidth) ? 48 : "A4".equalsIgnoreCase(paperWidth) ? 80 : 32;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(new byte[]{0x1B, 0x40});
            if (logoBase64 != null && !logoBase64.trim().isEmpty()) {
                byte[] logo = buildLogoRaster(logoBase64, paperWidth);
                if (logo.length > 0) { out.write(new byte[]{0x1B, 0x61, 0x01}); out.write(logo); out.write('\n'); }
            }
            out.write(new byte[]{0x1B, 0x61, 0x00});
            String[] lines = (content == null ? "" : content).replace("\r", "").split("\n", -1);
            StringBuilder normalized = new StringBuilder();
            for (String line : lines) {
                if (line.length() <= maxChars) normalized.append(line);
                else { int start = 0; while (start < line.length()) { int end = Math.min(start + maxChars, line.length()); normalized.append(line, start, end); if (end < line.length()) normalized.append('\n'); start = end; } }
                normalized.append('\n');
            }
            byte[] body; try { body = normalized.toString().getBytes(Charset.forName("GBK")); } catch (Exception ignored) { body = normalized.toString().getBytes(StandardCharsets.UTF_8); }
            out.write(body); out.write(new byte[]{0x1B, 0x64, 0x03}); out.write(new byte[]{0x1D, 0x56, 0x00});
            return out.toByteArray();
        } catch (Exception e) { return new byte[0]; }
    }

    private byte[] buildLogoRaster(String base64, String paperWidth) {
        try {
            String raw = base64.trim(); int comma = raw.indexOf(','); if (comma >= 0) raw = raw.substring(comma + 1);
            byte[] bytes = Base64.decode(raw, Base64.DEFAULT); Bitmap source = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); if (source == null) return new byte[0];
            int maxWidth = "80mm".equalsIgnoreCase(paperWidth)
        ? 220
        : "A4".equalsIgnoreCase(paperWidth)
            ? 300
            : 160;
            float scale = Math.min(1f, (float) maxWidth / Math.max(1, source.getWidth()));
            int width = Math.max(1, Math.round(source.getWidth() * scale)); int height = Math.max(1, Math.round(source.getHeight() * scale));
            Bitmap scaled = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); Canvas canvas = new Canvas(scaled); canvas.drawColor(Color.WHITE); Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG); canvas.drawBitmap(source, null, new android.graphics.Rect(0,0,width,height), paint);
            source.recycle();
            int bytesPerRow = (width + 7) / 8; ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(0x1D); out.write(0x76); out.write(0x30); out.write(0x00); out.write(bytesPerRow & 0xFF); out.write((bytesPerRow >> 8) & 0xFF); out.write(height & 0xFF); out.write((height >> 8) & 0xFF);
            for (int y=0; y<height; y++) for (int bx=0; bx<bytesPerRow; bx++) { int value=0; for(int bit=0; bit<8; bit++){ int x=bx*8+bit; if(x<width){ int p=scaled.getPixel(x,y); int gray=(Color.red(p)*299+Color.green(p)*587+Color.blue(p)*114)/1000; if(gray<180) value |= (1<<(7-bit)); }} out.write(value); }
            scaled.recycle(); return out.toByteArray();
        } catch (Exception e) { return new byte[0]; }
    }

    private void closeSocket(BluetoothSocket socket) { if (socket != null) { try { socket.close(); } catch (Exception ignored) {} } }
    private String safeMessage(Exception e) { if (e == null) return "unknown error"; String message=e.getMessage(); return message==null || message.trim().isEmpty()?e.getClass().getSimpleName():message; }
}
