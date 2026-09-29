# Cara membuat APK Anton Service POS di Windows

## 1. Pasang perangkat lunak

- Node.js 22 LTS
- pnpm 10
- Android Studio terbaru yang mendukung Android SDK 35
- Android SDK Platform 35
- Android SDK Build-Tools
- JDK 21

## 2. Buka PowerShell pada folder proyek

```powershell
npm install -g pnpm
pnpm install
pnpm build
npx cap sync android
npx cap open android
```

Atau klik dua kali `setup-android.bat`.

## 3. Membuat APK debug

Di Android Studio:

**Build > Generate App Bundles or APKs > Generate APKs**

APK biasanya berada di:

`android/app/build/outputs/apk/debug/app-debug.apk`

## 4. Memasang ke HP

Salin `app-debug.apk` ke HP Android, buka file tersebut, izinkan pemasangan dari sumber yang digunakan jika diminta, lalu Install.

Untuk printer POS-58B, pasangkan printer terlebih dahulu melalui Bluetooth Android. Setelah Anton POS dibuka, masuk ke pengaturan printer, pilih Bluetooth, cari printer, pilih POS-58B, lalu lakukan Tes Koneksi.
