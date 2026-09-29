# Anton Service POS — Android APK

Proyek ini sudah disiapkan untuk Capacitor 7 dan plugin printer `BluetoothPrinterBridge`.

## Di Windows + Android Studio

1. Install Node.js 22 LTS, Android Studio, Android SDK 35, dan JDK 21.
2. Buka folder proyek ini di terminal.
3. Jalankan:

```powershell
pnpm install
pnpm build
npx cap sync android
npx cap open android
```

4. Di Android Studio tunggu Gradle Sync selesai.
5. Hubungkan HP Android dengan USB debugging aktif.
6. Pilih perangkat Android lalu tekan Run.
7. Untuk APK debug: buka `android/app/build/outputs/apk/debug/` setelah menjalankan `assembleDebug`.

## Printer POS-58B

- Pasangkan POS-58B terlebih dahulu melalui Settings > Bluetooth Android.
- Di Anton Service POS pilih Printer > Bluetooth.
- Tekan Cari Printer Bluetooth.
- Pilih POS-58B.
- Tekan Tes Koneksi.
- Gunakan Cetak-Preview untuk mencetak.

Untuk printer Wi-Fi gunakan IP printer dan port 9100.
