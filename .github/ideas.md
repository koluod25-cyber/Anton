# Arah Desain — Anton Service POS

## Referensi sebagai spesifikasi utama

File HTML yang diberikan pengguna menjadi **spesifikasi ground-truth** untuk fitur, istilah bisnis, rumus transaksi, navigasi, dan perilaku data aplikasi. Implementasi ini mempertahankan alur kasir dengan kembalian otomatis, laporan akuntansi, persediaan, struk bergaransi, serta penyimpanan data lokal. Perbaikan yang dilakukan berfokus pada keterbacaan, responsivitas, konsistensi visual, dan perlindungan alur kerja tanpa mengubah tujuan produk.

## Pendekatan yang dipilih — Sistem Operasi Bengkel Modern

**Design Movement.** Antarmuka mengambil inspirasi dari sistem operasional bengkel dan perangkat ukur industri modern: panel modular, angka mudah dipindai, serta aksen warna yang bersifat semantik.

**Core Principles.** Informasi keuangan tampil dengan hierarki tinggi; tindakan transaksi selalu berada dekat nilai yang dipengaruhi; warna hanya dipakai sebagai sinyal status; dan ruang kerja dibagi berdasarkan tugas nyata kasir, bukan dekorasi. 

**Color Philosophy.** Grafit gelap memberi kesan teknik yang andal dan menjadi jangkar navigasi. Putih hangat menjaga tabel dan nominal tetap jelas. Merah servis menyatakan aksi utama, sedangkan hijau, biru, amber, dan merah dipakai khusus untuk status kas, bank, peringatan, dan pengurangan.

**Layout Paradigm.** Sidebar operasi yang tetap menjadi tulang punggung, diikuti area kerja asimetris yang bergerak dari ringkasan ke detail. Halaman kasir memprioritaskan dua panel yang tidak seimbang: katalog produk di kiri dan panel keputusan pembayaran yang selalu terlihat di kanan.

**Signature Elements.** Garis aksen merah vertikal pada judul, kartu metrik dengan label kapital kecil, serta barcode garansi pseudo-SVG yang muncul pada produk dan struk.

**Interaction Philosophy.** Interaksi frequent seperti menambah barang dan mengubah kuantitas bersifat ringkas serta tanpa animasi berlebihan. Dialog, drawer, dan struk menggunakan transisi yang pendek untuk menegaskan perubahan konteks.

**Animation.** Hover dan press menggunakan transform ringan. Drawer dan dialog masuk dengan opacity dan translasi singkat memakai easing cepat; seluruh gerak dinonaktifkan untuk preferensi reduced motion.

**Typography System.** `Manrope` untuk antarmuka dan angka operasional karena bentuknya terbuka dan efisien, sementara `DM Mono` digunakan pada kode, barcode, dan struk. Judul menggunakan bobot 800; label berukuran kecil dan berjarak untuk memudahkan pemindaian.

**Brand Essence.** Sistem kasir dan akuntansi bagi bengkel AC yang memerlukan transaksi cepat, pencatatan stok, serta laporan yang terbaca langsung. Kepribadiannya **presisi, sigap, dan tepercaya**.

**Brand Voice.** Headline bersifat operasional dan lugas; CTA memakai kata kerja spesifik. Contoh: “Selesaikan transaksi dengan kembalian tepat.” dan “Cocokkan stok fisik sebelum menutup hari.”

**Wordmark & Logo.** Marka grafis tanpa teks memadukan aliran udara AC, kilat kelistrikan, serta ruang negatif yang ringan; wordmark menggunakan huruf kapital teknis dengan tagline merah sebagai penanda layanan.

**Signature Brand Color.** Merah layanan `#c7362f`.

## Style Decisions

- Tidak memakai latar ungu, glow neon, atau layout landing-page terpusat.
- Citra visual digunakan sekali pada konteks yang sesuai: aktivitas service pada dashboard, bahan struk pada setting, dan inventori pada katalog.
- Semua keputusan warna untuk nilai nominal mengikuti arti akuntansi/status; warna tidak digunakan sekadar hiasan.
- Shell desktop selalu menggunakan rel operasi grafit dengan marka Anton Service. Metadata bisnis tetap sekunder terhadap navigasi.
- Material panel menggunakan bidang putih hangat, pemisah tegas, bayangan terbatas, dan label teknis; tidak memakai kartu pastel dekoratif.
- Merah layanan `#c7362f` hanya digunakan bagi identitas service, aksi transaksi utama, serta aksen judul. Warna hijau, biru, amber, dan merah lainnya memikul arti status operasional atau akuntansi.
