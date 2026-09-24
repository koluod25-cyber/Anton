# Instalasi Anton Service POS Offline

## Artefak

Gunakan file `Anton-Service-POS-offline-latest.apk` yang disediakan bersama hasil pekerjaan ini. APK tersebut merupakan build debug yang ditandatangani untuk sideload pada perangkat Android.

## Cara memasang

Pindahkan file APK ke perangkat Android melalui kabel USB, Bluetooth, atau penyimpanan lokal. Buka file tersebut dari aplikasi File Manager, izinkan pemasangan dari sumber tersebut apabila Android memintanya, kemudian tekan **Install**. Setelah selesai, buka aplikasi **Anton Service POS** dari launcher.

Pemasangan pertama memerlukan izin pemasangan aplikasi dari sumber selain Play Store. Pengaturan ini biasanya berada pada **Settings → Security/Privacy → Install unknown apps**, tetapi nama menu dapat berbeda menurut merek dan versi Android.

## Penggunaan offline

Bundle aplikasi web terbaru, manifest, service worker, dan aset JavaScript/CSS telah dimasukkan ke dalam APK. Saat aplikasi mulai dibuka, native splash menampilkan logo Anton Service pada latar gelap, kemudian splash web menampilkan animasi logo singkat sebelum halaman POS muncul. Verifikasi internal memastikan `index.html`, `manifest.webmanifest`, `sw.js`, dan bundle aplikasi berada di dalam APK. Modul POS dan data yang menggunakan LocalStorage dapat digunakan tanpa koneksi internet. Data tersimpan secara lokal pada perangkat tempat aplikasi dipakai.

## Batasan penting

APK ini adalah build debug untuk penggunaan internal dan sideload, bukan paket Play Store yang ditandatangani dengan kunci produksi. Tidak ada uji perangkat fisik atau emulator Android yang tersedia pada lingkungan build ini; validasi yang dilakukan mencakup keberhasilan build Gradle, metadata APK, verifikasi signature APK v2, checksum, dan pemeriksaan bundle offline yang tertanam.

Data LocalStorage tidak otomatis tersinkronisasi antarperangkat. Jangan menghapus data aplikasi sebelum melakukan backup/restore yang sesuai. Pembaruan aplikasi dilakukan secara manual dengan memasang APK versi baru. Karena APK debug menggunakan application ID yang sama, pemasangan pembaruan di atas versi sebelumnya seharusnya mempertahankan data lokal selama Android menerima signature build tersebut; tetap lakukan backup sebelum pembaruan.

## Informasi verifikasi
APK terbaru memakai **logo Anton Service yang diberikan pengguna sebagai ikon launcher**, termasuk varian ikon bulat dan adaptive icon Android.


| Item | Nilai |
|---|---|
| Application ID | `com.antonservice.pos` |
| Version | `1.0` |
| Minimum Android SDK | 24 |
| Target Android SDK | 36 |
| Tipe build | Debug / sideload |
| Ukuran APK | 8.327.978 byte (sekitar 7,9 MB) |
| Signature | APK Signature Scheme v2 terverifikasi |
| Ikon launcher | Logo Anton Service pengguna, seluruh density Android |
| Kamera | Izin `android.permission.CAMERA` dan runtime request native |
| Splash | Native splash + animasi splash web berlogo Anton Service |
| SHA-256 | `11039cca06fb6649142c7ff59c66ea6f831077d32df39e30681de5bdd48a1f3b` |

Jika Android menolak pemasangan karena konflik signature atau versi, hapus versi lama hanya setelah memastikan data telah dicadangkan, lalu pasang APK baru.
