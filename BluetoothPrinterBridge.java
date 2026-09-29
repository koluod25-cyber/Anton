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

    private static final UUID SPP_UUID =
            UUID.fromString(
                    "00001101-0000-1000-8000-00805F9B34FB"
            );

    private static final String USB_PERMISSION_ACTION =
            "com.antonservice.pos.USB_PERMISSION";

    private UsbManager usbManager;

    private final BroadcastReceiver usbPermissionReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (!USB_PERMISSION_ACTION.equals(
                            intent.getAction()
                    )) {
                        return;
                    }

                    // Permission result is handled by the
                    // print/list operation when requested.
                }
            };

    @Override
    public void load() {
        super.load();

        usbManager =
                (UsbManager) getContext()
                        .getSystemService(Context.USB_SERVICE);

        IntentFilter filter =
                new IntentFilter(USB_PERMISSION_ACTION);

        if (Build.VERSION.SDK_INT >= 33) {
            getContext().registerReceiver(
                    usbPermissionReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            getContext().registerReceiver(
                    usbPermissionReceiver,
                    filter
            );
        }
    }

    /**
     * Bluetooth paired printers.
     */
    @PluginMethod
    public void listPairedPrinters(PluginCall call) {

        BluetoothAdapter adapter =
                BluetoothAdapter.getDefaultAdapter();

        JSObject result = new JSObject();
        JSArray printers = new JSArray();

        if (adapter == null) {
            result.put("printers", printers);
            call.resolve(result);
            return;
        }

        if (Build.VERSION.SDK_INT >= 31) {

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
     * Test Bluetooth.
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

                JSObject result = new JSObject();
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
     * Test Wi-Fi printer.
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

                JSObject result = new JSObject();
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
     * Mencari perangkat USB yang terhubung melalui OTG.
     */
    @PluginMethod
    public void listUsbPrinters(PluginCall call) {

        if (usbManager == null) {
            usbManager =
                    (UsbManager) getContext()
                            .getSystemService(Context.USB_SERVICE);
        }

        JSArray printers = new JSArray();
        JSObject result = new JSObject();

        if (usbManager == null) {
            result.put("printers", printers);
            call.resolve(result);
            return;
        }

        try {

            for (UsbDevice device :
                    usbManager.getDeviceList().values()) {

                JSObject item = new JSObject();

                item.put(
                        "deviceId",
                        device.getDeviceId()
                );

                item.put(
                        "vendorId",
                        device.getVendorId()
                );

                item.put(
                        "productId",
                        device.getProductId()
                );

                String name =
                        device.getProductName();

                if (name == null ||
                        name.trim().isEmpty()) {
                    name = "USB Printer";
                }

                item.put("name", name);

                if (device.getProductName() != null) {
                    item.put(
                            "productName",
                            device.getProductName()
                    );
                }

                if (device.getManufacturerName() != null) {
                    item.put(
                            "manufacturerName",
                            device.getManufacturerName()
                    );
                }

                printers.put(item);
            }

            result.put("printers", printers);
            call.resolve(result);

        } catch (Exception e) {

            call.reject(
                    "Gagal membaca perangkat USB: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Test USB printer.
     *
     * Android akan meminta izin USB jika belum diberikan.
     */
    @PluginMethod
    public void testUsb(PluginCall call) {

        int deviceId =
                call.getInt("deviceId", -1);

        if (deviceId < 0) {
            call.reject(
                    "Perangkat USB belum dipilih"
            );
            return;
        }

        UsbDevice device =
                findUsbDevice(deviceId);

        if (device == null) {
            call.reject(
                    "Perangkat USB tidak ditemukan"
            );
            return;
        }

        new Thread(() -> {

            try {

                UsbDeviceConnection connection =
                        openUsbConnection(device);

                if (connection == null) {
                    throw new Exception(
                            "Tidak mendapat izin atau gagal membuka USB"
                    );
                }

                connection.close();

                JSObject result = new JSObject();
                result.put("success", true);

                call.resolve(result);

            } catch (Exception e) {

                call.reject(
                        "USB tidak dapat terhubung: "
                                + e.getMessage()
                );
            }

        }).start();
    }

    /**
     * Cetak Bluetooth / Wi-Fi / USB.
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
            call.reject("Isi struk kosong");
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

                } else if (
                        "USB".equalsIgnoreCase(connection)
                ) {

                    int deviceId =
                            call.getInt(
                                    "deviceId",
                                    -1
                            );

                    if (deviceId < 0) {
                        throw new Exception(
                                "Perangkat USB belum dipilih"
                        );
                    }

                    UsbDevice device =
                            findUsbDevice(deviceId);

                    if (device == null) {
                        throw new Exception(
                                "Printer USB tidak ditemukan"
                        );
                    }

                    printUsb(
                            device,
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

                JSObject result = new JSObject();
                result.put("success", true);

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
     * Cari USB device berdasarkan deviceId.
     */
    private UsbDevice findUsbDevice(
            int deviceId
    ) {

        if (usbManager == null) {
            usbManager =
                    (UsbManager) getContext()
                            .getSystemService(Context.USB_SERVICE);
        }

        if (usbManager == null) {
            return null;
        }

        for (UsbDevice device :
                usbManager.getDeviceList().values()) {

            if (device.getDeviceId() == deviceId) {
                return device;
            }
        }

        return null;
    }

    /**
     * Membuka koneksi USB.
     *
     * Jika belum diberi izin Android, permission request
     * dikirim ke sistem.
     */
    private UsbDeviceConnection openUsbConnection(
            UsbDevice device
    ) throws Exception {

        if (usbManager == null) {
            throw new Exception(
                    "USB Manager tidak tersedia"
            );
        }

        if (!usbManager.hasPermission(device)) {

            int flags =
                    PendingIntent.FLAG_UPDATE_CURRENT;

            if (Build.VERSION.SDK_INT >= 31) {
                flags |=
                        PendingIntent.FLAG_MUTABLE;
            }

            Intent permissionIntent =
                    new Intent(
                            USB_PERMISSION_ACTION
                    );

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            getContext(),
                            device.getDeviceId(),
                            permissionIntent,
                            flags
                    );

            usbManager.requestPermission(
                    device,
                    pendingIntent
            );

            throw new Exception(
                    "Izin USB belum diberikan. "
                            + "Silakan izinkan akses USB pada Android "
                            + "lalu tekan tes/cetak lagi."
            );
        }

        UsbInterface printerInterface = null;
        UsbEndpoint outEndpoint = null;

        for (
                int i = 0;
                i < device.getInterfaceCount();
                i++
        ) {

            UsbInterface usbInterface =
                    device.getInterface(i);

            for (
                    int j = 0;
                    j < usbInterface.getEndpointCount();
                    j++
            ) {

                UsbEndpoint endpoint =
                        usbInterface.getEndpoint(j);

                if (
                        endpoint.getType()
                                == UsbConstants.USB_ENDPOINT_XFER_BULK
                        &&
                        endpoint.getDirection()
                                == UsbConstants.USB_DIR_OUT
                ) {

                    printerInterface = usbInterface;
                    outEndpoint = endpoint;
                    break;
                }
            }

            if (outEndpoint != null) {
                break;
            }
        }

        if (
                printerInterface == null
                        ||
                outEndpoint == null
        ) {

            throw new Exception(
                    "Endpoint USB OUT printer tidak ditemukan"
            );
        }

        UsbDeviceConnection connection =
                usbManager.openDevice(device);

        if (connection == null) {
            throw new Exception(
                    "Gagal membuka perangkat USB"
            );
        }

        if (!connection.claimInterface(
                printerInterface,
                true
        )) {

            connection.close();

            throw new Exception(
                    "Tidak dapat mengakses interface printer USB"
            );
        }

        return connection;
    }

    /**
     * Cetak melalui USB bulk OUT.
     */
    private void printUsb(
            UsbDevice device,
            byte[] data,
            int copies
    ) throws Exception {

        if (usbManager == null) {
            usbManager =
                    (UsbManager) getContext()
                            .getSystemService(Context.USB_SERVICE);
        }

        if (!usbManager.hasPermission(device)) {

            // Meminta izin terlebih dahulu.
            openUsbConnection(device);

            throw new Exception(
                    "Izin USB belum diberikan. "
                            + "Setujui izin USB kemudian cetak kembali."
            );
        }

        UsbInterface printerInterface = null;
        UsbEndpoint outEndpoint = null;

        for (
                int i = 0;
                i < device.getInterfaceCount();
                i++
        ) {

            UsbInterface usbInterface =
                    device.getInterface(i);

            for (
                    int j = 0;
                    j < usbInterface.getEndpointCount();
                    j++
            ) {

                UsbEndpoint endpoint =
                        usbInterface.getEndpoint(j);

                if (
                        endpoint.getType()
                                == UsbConstants.USB_ENDPOINT_XFER_BULK
                        &&
                        endpoint.getDirection()
                                == UsbConstants.USB_DIR_OUT
                ) {

                    printerInterface = usbInterface;
                    outEndpoint = endpoint;
                    break;
                }
            }

            if (outEndpoint != null) {
                break;
            }
        }

        if (
                printerInterface == null
                        ||
                outEndpoint == null
        ) {

            throw new Exception(
                    "Printer USB tidak memiliki endpoint OUT ESC/POS"
            );
        }

        UsbDeviceConnect
