package com.antonservice.pos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        registerPlugin(
                BluetoothPrinterBridge.class
        );

        super.onCreate(savedInstanceState);

        requestAppPermissions();
    }

    private void requestAppPermissions() {

        if (Build.VERSION.SDK_INT >= 31) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                    },
                    PERMISSION_REQUEST_CODE
            );

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    PERMISSION_REQUEST_CODE
            );
        }
    }
}
