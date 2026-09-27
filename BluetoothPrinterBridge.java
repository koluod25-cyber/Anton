package com.antonservice.pos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@CapacitorPlugin(name = "BluetoothPrinterBridge")
public class BluetoothPrinterBridge extends Plugin {

    private static final UUID SPP_UUID =
            UUID.fromString(
                    "00001101-0000-1000-8000-00805F9B34FB"
            );

    /**
     * Mencari printer Bluetooth yang sudah dipasangkan
     * dengan HP Android.
     */
    @PluginMethod
    public void listPairedPrinters(PluginCall call) {

        BluetoothAdapter adapter =
                BluetoothAdapter.getDefaultAdapter();

        JSObject result = new JSObject();
        com.getcapacitor.JSArray printers =
                new com.getcapacitor.JSArray();

        if (adapter == null) {
            result.put("printers", printers);
            call.resolve(result);
            return;
        }

        /*
         * Android 12+
         */
        if (android.os.Build.VERSION.SDK_INT >= 31) {

            if (getContext().checkSelfPermission(
                    Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) {

                call.reject(
                        "Izin BLUETOOTH_CONNECT belum diberikan"
                );

                return;
            }
        }

        try {

            Set<BluetoothDevice> bondedDevices =
                    adapter.getBondedDevices();

            for (BluetoothDevice device : bondedDevices) {

                JSObject item = new JSObject();

                String name = device.getName();

                if (name == null || name.trim().isEmpty()) {
                    name = "Bluetooth Printer";
                }

                item.put("name", name);
                item.put("address", device.getAddress());

                printers.put(item);
            }

            result.put("printers", printers);

            call.resolve(result);

        } catch (Exception e) {

            call.reject(
                    "Gagal membaca printer Bluetooth: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Menguji koneksi Bluetooth.
     */
    @PluginMethod
    public void testConnection(PluginCall call) {

        String address =
                call.getString("address", "");

        if (address.trim().isEmpty()) {

            call.reject(
                    "Alamat Bluetooth printer belum diisi"
            );

            return;
        }

        new Thread(() -> {

            BluetoothSocket socket = null;

            try {

                BluetoothAdapter adapter =
                        BluetoothAdapter.getDefaultAdapter();

                if (adapter == null) {
                    throw new Exception(
                            "Bluetooth tidak tersedia"
                    );
                }

                BluetoothDevice device =
                        adapter.getRemoteDevice(address);

                socket =
                        device.createRfcommSocketToServiceRecord(
                                SPP_UUID
                        );

                socket.connect();

                JSObject result =
                        new JSObject();

                result.put("success", true);

                call.resolve(result);

            } catch (Exception e) {

                call.reject(
                        "Koneksi Bluetooth gagal: "
                                + e.getMessage()
                );

            } finally {

                try {

                    if (socket != null) {
                        socket.close();
                    }

                } catch (Exception ignored) {
                }
            }

        }).start();
    }

    /**
     * Menguji printer Wi-Fi melalui TCP.
     * Port default ESC/POS biasanya 9100.
     */
    @PluginMethod
    public void testWifi(PluginCall call) {

        String host =
                call.getString("host", "");

        int port =
                call.getInt("port", 9100);

        if (host.trim().isEmpty()) {

            call.reject(
                    "IP printer Wi-Fi belum diisi"
            );

            return;
        }

        new Thread(() -> {

            try (Socket socket = new Socket()) {

                socket.connect(
                        new InetSocketAddress(
                                host,
                                port
                        ),
                        3000
                );

                JSObject result =
                        new JSObject();

                result.put("success", true);

                call.resolve(result);

            } catch (Exception e) {

                call.reject(
                        "Koneksi Wi-Fi gagal: "
                                + e.getMessage()
                );
            }

        }).start();
    }

    /**
     * Cetak melalui Bluetooth atau Wi-Fi.
     */
    @PluginMethod
    public void print(PluginCall call) {

        String connection =
                call.getString(
                        "connection",
                        "Bluetooth"
                );

        String content =
                call.getString(
                        "content",
                        ""
                );

        int copies =
                call.getInt(
                        "copies",
                        1
                );

        String paperWidth =
                call.getString(
                        "paperWidth",
                        "58mm"
                );

        copies =
                Math.max(
                        1,
                        Math.min(
                                copies,
                                20
                        )
                );

        if (content.trim().isEmpty()) {

            call.reject(
                    "Isi struk kosong"
            );

            return;
        }

        new Thread(() -> {

            try {

                byte[] data =
                        buildEscPos(
                                content,
                                paperWidth
                        );

                if ("WiFi".equalsIgnoreCase(connection)) {

                    String host =
                            call.getString(
                                    "host",
                                    ""
                            );

                    int port =
                            call.getInt(
                                    "port",
                                    9100
                            );

                    printWifi(
                            host,
                            port,
                            data,
                            copies
                    );

                } else {

                    String address =
                            call.getString(
                                    "address",
                                    ""
                            );

                    printBluetooth(
                            address,
                            data,
                            copies
                    );
                }

                JSObject result =
                        new JSObject();

                result.put(
                        "success",
                        true
                );

                call.resolve(result);

            } catch (Exception e) {

                call.reject(
                        "Cetak gagal: "
                                + e.getMessage()
                );
            }

        }).start();
    }

    /**
     * Cetak Bluetooth Classic / SPP.
     * POS-58B umumnya menggunakan protokol ini.
     */
    private void printBluetooth(
            String address,
            byte[] data,
            int copies
    ) throws Exception {

        BluetoothAdapter adapter =
                BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) {

            throw new Exception(
                    "Bluetooth tidak tersedia"
            );
        }

        BluetoothDevice device =
                adapter.getRemoteDevice(
                        address
                );

        BluetoothSocket socket =
                device.createRfcommSocketToServiceRecord(
                        SPP_UUID
                );

        socket.connect();

        try {

            OutputStream output =
                    socket.getOutputStream();

            for (
                    int i = 0;
                    i < copies;
                    i++
            ) {

                output.write(data);
                output.flush();
            }

            output.close();

        } finally {

            socket.close();
        }
    }

    /**
     * Cetak printer Wi-Fi ESC/POS
     * menggunakan raw TCP.
     */
    private void printWifi(
            String host,
            int port,
            byte[] data,
            int copies
    ) throws Exception {

        if (host.trim().isEmpty()) {

            throw new Exception(
                    "IP printer Wi-Fi kosong"
            );
        }

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            host,
                            port
                    ),
                    5000
            );

            OutputStream output =
                    socket.getOutputStream();

            for (
                    int i = 0;
                    i < copies;
                    i++
            ) {

                output.write(data);
                output.flush();
            }

            output.close();
        }
    }

    /**
     * Membentuk data ESC/POS sederhana.
     */
    private byte[] buildEscPos(
            String content,
            String paperWidth
    ) {

        byte[] initialize =
                new byte[]{
                        0x1B,
                        0x40
                };

        byte[] text =
                content.getBytes(
                        StandardCharsets.UTF_8
                );

        byte[] feed =
                new byte[]{
                        0x0A,
                        0x0A,
                        0x0A
                };

        /*
         * Full cut.
         * Printer yang tidak mendukung cutter
         * biasanya akan mengabaikan perintah ini.
         */
        byte[] cut =
                new byte[]{
                        0x1D,
                        0x56,
                        0x00
                };

        int length =
                initialize.length
                        + text.length
                        + feed.length
                        + cut.length;

        byte[] result =
                new byte[length];

        int position = 0;

        System.arraycopy(
                initialize,
                0,
                result,
                position,
                initialize.length
        );

        position += initialize.length;

        System.arraycopy(
                text,
                0,
                result,
                position,
                text.length
        );

        position += text.length;

        System.arraycopy(
                feed,
                0,
                result,
                position,
                feed.length
        );

        position += feed.length;

        System.arraycopy(
                cut,
                0,
                result,
                position,
                cut.length
        );

        return result;
    }
}
