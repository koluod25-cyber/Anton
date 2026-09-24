# Project TODO

- [x] Ekstrak logo asli dari file HTML referensi.
- [x] Unggah logo asli ke penyimpanan aset proyek.
- [x] Terapkan logo asli pada sidebar dan header.
- [x] Gunakan logo asli pada favicon.
- [x] Pastikan informasi header mengikuti NIB, alamat, dan telepon referensi.
- [x] Validasi TypeScript, build produksi, dan screenshot logo/header selesai.

Catatan: fitur kasir, kembalian otomatis, transaksi, laporan, dan data lokal tidak diubah.

- [x] Terapkan susunan header asli dari file referensi, bukan hanya logo asli.
- [x] Validasi ulang header selesai; checkpoint baru akan disimpan setelah pembacaan todo.

- [x] Samakan ukuran logo, posisi identitas, tipografi, dan jarak header dengan format asli referensi.
- [x] Uji desktop/mobile dan build selesai; checkpoint baru siap disimpan.

- [x] Validasi integrasi seluruh modul POS, laporan, inventori, setting, dan struk.
- [x] Jalankan test, TypeScript, dan build produksi untuk integrasi final.
- [x] Simpan checkpoint integrasi final setelah perubahan COA.

- [x] Tambahkan tombol Hapus di samping Edit pada halaman Akun COA.
- [x] Tambahkan konfirmasi penghapusan dan pertahankan akun bawaan agar tidak terhapus.
- [x] Validasi test, TypeScript, dan build setelah perubahan COA.

- [x] Verifikasi preview dan kontrak integrasi lintas modul setelah perubahan COA.
- [x] Perbaiki error server dotenv agar status runtime bersih.
- [x] Simpan checkpoint baru setelah verifikasi COA final.

- [x] Samakan susunan dashboard dengan referensi asli.
- [x] Pertahankan gambar dashboard dan seluruh aset visual yang sudah ada.
- [x] Validasi dashboard desktop, test, dan build selesai; checkpoint siap disimpan.

- [x] Uji dashboard hasil penyamaan layout pada viewport mobile.
- [x] Simpan checkpoint baru setelah perubahan dashboard tervalidasi.

- [x] Ubah kartu Omset Hari Ini dan Total Nota menjadi dua kartu putih di baris pertama.
- [x] Ubah kartu Jasa 401 menjadi kartu merah muda dengan nilai merah dan garis progres.
- [x] Ubah kartu Spare 402 + HPP 502 menjadi kartu biru muda dengan nilai biru dan garis progres.
- [x] Validasi responsif mobile/desktop, test, dan build selesai; checkpoint kartu dashboard siap disimpan.

- [x] Uji ulang kartu dashboard terbaru pada viewport desktop.
- [x] Simpan checkpoint baru setelah kartu dashboard tervalidasi desktop dan mobile.

- [x] Periksa konfigurasi, dependensi, dan status runtime seluruh aplikasi.
- [x] Jalankan test, TypeScript, build, smoke test kontrak lintas modul, dan validasi preview.
- [x] Perbaiki dependensi runtime dan verifikasi kontrak alur utama POS, dashboard, laporan, COA, inventori, setting, dan struk.
- [x] Simpan checkpoint final setelah seluruh pemeriksaan selesai.

- [x] Perkuat smoke test dengan kontrak kartu dashboard dan grafik omset terbaru.

- [x] Validasi kontrak render dan pemetaan modul utama melalui smoke test serta preview dashboard desktop/mobile.
- [x] Restart preview; runtime baru sehat dan log browser terbaru tanpa error dotenv.
- [x] Simpan checkpoint audit final setelah validasi otomatis dan runtime baru selesai.

- [x] Tambahkan smoke test pemetaan setiap menu sidebar ke id halaman dan handler navigate bersama.

- [x] Telusuri penyebab input keyboard/modal menutup atau kehilangan fokus saat mengetik.
- [x] Pertahankan fokus input dengan mencegah remount halaman saat state input berubah.
- [x] Validasi regresi input, test, TypeScript, build, restart runtime, dan preview selesai; checkpoint baru siap disimpan.

- [x] Uji interaktif input Nama pelanggan (Budi) dan modal barcode (SPR-001): fokus tetap aktif, nilai tidak hilang, modal tidak menutup saat mengetik.
- [x] Tambahkan test regresi anti-remount untuk memastikan halaman input tetap terpasang saat parent state berubah.
- [x] Simpan checkpoint baru setelah validasi interaktif selesai.

- [x] Tambahkan tombol Download / Install Aplikasi pada shell/header aplikasi.
- [x] Tambahkan manifest PWA, ikon asli, dan service worker minimal.
- [x] Sediakan fallback modal panduan install per platform bila browser tidak mendukung instalasi langsung.
- [x] Validasi tombol install, fallback, responsivitas desktop/mobile, test, build, dan restart runtime selesai; checkpoint baru siap disimpan.

- [x] Tambahkan modal panduan instalasi nyata sebagai fallback ketika browser tidak memberi beforeinstallprompt.
- [x] Tambahkan kontrak event beforeinstallprompt/appinstalled dan eksekusi simulasi handler install berhasil diverifikasi melalui smoke test.

- [x] Telusuri pemetaan alamat, NIB, telepon, logo, dan teks printer dari Setting ke ReceiptOverlay serta fungsi cetak.
- [x] Pastikan alamat Setting dipakai konsisten pada preview dan cetak struk.
- [x] Tambahkan regresi test alamat dan validasi TypeScript/build untuk integrasi struk.

- [x] Telusuri handler Edit/Hapus Akun COA dan persistensi data akun.
- [x] Pastikan hasil edit tersimpan melalui localStorage dan langsung muncul pada daftar COA.
- [x] Pastikan akun manual dapat dihapus dengan konfirmasi dan akun sistem tetap terlindungi.
- [x] Tambahkan regresi test operasi edit/hapus, validasi TypeScript/build, dan checkpoint baru siap disimpan.

- [x] Ganti label Pendapatan Sparepart menjadi Pendapatan Penjualan pada seluruh tampilan dan referensi COA 402.
- [x] Pastikan kode akun 402, nominal, kalkulasi, dan transaksi tidak berubah.
- [x] Tambahkan regresi test, validasi TypeScript/build, dan checkpoint baru siap disimpan.

- [x] Telusuri sumber logo pada ReceiptPreview dan fungsi cetak struk.
- [x] Hilangkan logo kotak hitam dan tampilkan hanya logo bulat asli.
- [x] Tambahkan regresi test, validasi preview/cetak, TypeScript/build, dan checkpoint baru.

- [x] Audit alur Download/Install aplikasi yang sudah ada, termasuk manifest, service worker, dan install prompt.
- [x] Sempurnakan tombol Download/Install agar memicu pemasangan PWA atau menampilkan fallback yang jelas.
- [x] Validasi alur install, fallback browser, responsivitas, test, TypeScript, build, dan checkpoint baru.

- [x] Audit sumber tanggal transaksi pada POS, daftar transaksi, dan struk.
- [x] Tambahkan input tanggal yang dapat diedit pada halaman POS Kasir dan simpan nilainya pada transaksi.
- [x] Validasi tanggal pada laporan/struk, test, TypeScript, build, responsivitas, dan checkpoint baru.

- [x] Tambahkan regresi test aliran tanggal transaksi terpilih ke daftar transaksi, laporan, dan ReceiptOverlay.
- [x] Validasi UI field tanggal POS pada viewport desktop dan mobile.
- [x] Simpan checkpoint setelah validasi lanjutan tanggal POS selesai.

- [x] Audit ketersediaan Android SDK/Gradle dan kelayakan wrapper APK untuk aplikasi web saat ini.
- [x] Siapkan artefak APK offline atau paket offline alternatif yang dapat dipasang.
- [x] Validasi build/signature/bundle offline APK dan dokumentasikan cara penggunaan serta batasannya; uji perangkat fisik dicatat sebagai batasan lingkungan.

- [x] Audit struktur transaksi, handler hapus semua, dan persistensi localStorage.
- [x] Tambahkan tombol Hapus di samping Lihat struk untuk setiap transaksi dengan konfirmasi.
- [x] Pastikan hanya transaksi terpilih yang dihapus dan laporan/dashboard ikut diperbarui.
- [x] Tambahkan regresi test, validasi TypeScript/build/responsif, lalu simpan checkpoint.

- [x] Tambahkan regresi test bahwa total pendapatan jasa berubah dari 350000 menjadi 150000 setelah satu transaksi dihapus.
- [x] Validasi breakpoint desktop dan mobile; kontrol aksi dibuat flex-wrap agar Lihat struk dan Hapus tetap terlihat.
- [x] Simpan checkpoint final fitur hapus transaksi individual.

- [x] Audit seluruh label Spare pada halaman Produk & Jasa dan nilai tipe internalnya.
- [x] Ganti label option Spare menjadi Barang pada form dan daftar Produk & Jasa tanpa mengubah logika internal.
- [x] Tambahkan regresi test, validasi TypeScript/build/responsif, lalu simpan checkpoint.

- [x] Validasi responsivitas markup halaman Produk & Jasa pada viewport mobile: grid satu kolom di bawah xl dan label Barang terdeteksi pada kartu/form.
- [x] Simpan checkpoint baru khusus perubahan label Spare menjadi Barang setelah validasi final selesai.

- [x] Sinkronkan bundle web terbaru ke wrapper Android untuk APK offline.
- [x] Bangun dan verifikasi APK offline versi terbaru.
- [x] Bagikan APK beserta instruksi pemasangan offline.

- [x] Audit implementasi Scan/barcode, form Produk & Jasa, modal, stok/HPP, dan formula Neraca.
- [x] Integrasikan tombol Scan dengan kamera HP dan fallback input barcode manual.
- [x] Pastikan input modal memperbarui nilai persediaan/HPP dan Neraca tetap balance.
- [x] Tambahkan regresi test, validasi TypeScript/build/responsif, dan simpan checkpoint.

- [x] Audit implementasi Scan/barcode, form Produk & Jasa, modal, stok/HPP, dan formula Neraca.
- [x] Integrasikan tombol Scan dengan kamera HP dan fallback input barcode manual.
- [x] Pastikan input modal memperbarui nilai persediaan/HPP dan Neraca tetap balance.
- [x] Tambahkan regresi test kamera/modal, validasi TypeScript/build/responsif, dan siapkan checkpoint.

- [x] Perbaiki scanner barcode/QR agar kamera HP benar-benar aktif dan hasil scan terhubung ke input POS.
- [x] Audit dan perbaiki formula Neraca agar Aktiva = Pasiva + Modal pada data transaksi dan persediaan aktual.
- [x] Tambahkan regresi test kamera/fallback dan invariant Neraca, lalu validasi TypeScript, build, responsif, dan checkpoint.

- [x] Ganti balancing plug dengan formula Neraca dari saldo akun sumber; akun Modal 301 tidak lagi menerima residual correction otomatis.
- [x] Pulihkan stok barang yang terjual saat transaksi individual dihapus agar persediaan/HPP dan Neraca konsisten.
- [x] Tambahkan regresi test pemulihan stok, formula saldo sumber, dan responsivitas scanner/Neraca; 20/20 test, TypeScript, build, dan screenshot mobile berhasil.
- [x] Simpan checkpoint final bugfix scanner dan Neraca.

- [x] Audit aliran nilai modal Produk & Jasa ke persediaan, HPP, dan akun Modal Neraca.
- [x] Perbaiki integrasi agar biaya modal barang tercatat satu kali sebagai Persediaan dan Modal.
- [x] Validasi tambah/edit, stok opname, penjualan, hapus barang, Neraca balance, test, build, dan checkpoint.

- [x] Tambahkan regresi eksplisit tambah produk baru, edit modal/stok, stok opname, penjualan barang, dan hapus barang.
- [x] Validasi Neraca setelah setiap skenario inventori utama agar Aktiva = Pasiva + Modal tanpa duplikasi.
- [x] Simpan checkpoint baru setelah integrasi modal Produk & Jasa selesai diverifikasi.

- [x] Audit penyebab Neraca masih tidak balance pada data aktual dan formula akun berjalan.
- [x] Perbaiki rekonsiliasi Persediaan, Modal, transaksi, HPP, dan stok agar Aktiva = Pasiva + Modal.
- [x] Tambahkan validasi saldo awal, skenario inventori, dan selisih Neraca sebelum checkpoint.
- [x] Simpan checkpoint final setelah Neraca terverifikasi balance.

- [x] Audit ulang rumus Modal Persediaan, saldo awal, Persediaan aktual, dan HPP terjual.
- [x] Perbaiki sumber selisih atau nilai negatif tanpa menutupnya dengan balancing plug.
- [x] Tambahkan regresi test nilai negatif dan skenario stok/HPP, lalu validasi build dan checkpoint.

- [x] Simpan checkpoint baru setelah perubahan rumus Modal Persediaan, regresi test, dan validasi build terbaru selesai.

- [x] Audit subtotal, diskon, pajak, pendapatan bersih, HPP, dan dampaknya pada laporan.
- [x] Perbaiki rumus pendapatan setelah diskon jika terdapat perhitungan ganda atau dasar yang tidak konsisten.
- [x] Tambahkan regresi test transaksi diskon dan validasi dashboard, Laba Rugi, Neraca, TypeScript, build, serta checkpoint.

- [x] Perbaiki bug Omset Dashboard bernilai 0 setelah transaksi Rp350.000 dengan diskon Rp300.000.
- [x] Tambahkan regresi test omset transaksi berdiskon besar dan validasi dashboard.
- [x] Validasi TypeScript, test, build, preview, dan simpan checkpoint bugfix omset.

- [x] Audit dan perbaiki rumus Pendapatan Jasa pada transaksi jasa dan transaksi campuran.
- [x] Tambahkan regresi Pendapatan Jasa setelah diskon, pajak, dan transaksi campuran.
- [x] Validasi Dashboard, Laba Rugi, Neraca, TypeScript, test, build, preview, dan checkpoint.

- [x] Pastikan Omset Dashboard menggabungkan Pendapatan Jasa neto dan Pendapatan Barang/Spare neto.
- [x] Tambahkan regresi omset gabungan jasa dan spare setelah diskon.
- [x] Validasi kartu, grafik, TypeScript, test, build, preview, dan checkpoint.

- [x] Audit ulang sumber jasaTotal dan spareTotal saat transaksi disimpan serta fallback Omset Dashboard.
- [x] Perbaiki Omset agar tetap menghitung transaksi lama maupun transaksi baru dengan jasa dan barang.
- [x] Tambahkan regresi bentuk transaksi kosong/legacy, validasi penuh, dan checkpoint baru.

- [x] Perbaiki kartu Omset Hari Ini agar menjumlahkan Jasa neto dan Barang/Spare neto; kasus bukti Rp50.000 + Rp150.000 harus Rp200.000.
- [x] Tambahkan regresi kartu Omset gabungan dan validasi grafik/rincian Dashboard.
- [x] Validasi TypeScript, test, build, preview mobile/desktop, restart, dan checkpoint.

- [x] Ubah label akun 403 pada Buku Besar COA dari “Diskon Penjualan” menjadi “Diskon”.
- [x] Pastikan kode 403, saldo, dan rumus contra-pendapatan tidak berubah serta perbarui regresi label.
- [x] Validasi test, TypeScript, build, preview, restart, dan checkpoint.

- [x] Pastikan Buku Besar COA dapat mengedit dan menghapus akun sesuai aturan akun sistem/manual.
- [x] Sinkronkan saldo Modal Buku Besar dengan penambahan/pengurangan modal dari halaman Jasa & Penjualan.
- [x] Tambahkan regresi edit/hapus akun, perubahan modal, stok, dan Neraca balance.
- [x] Validasi test, TypeScript, build, preview, restart, dan checkpoint.

- [x] Pastikan input Modal × Stok Produk & Jasa langsung menambah saldo Modal Buku Besar COA.
- [x] Pastikan edit, pengurangan stok, dan stok opname mengurangi/menambah saldo Modal tepat satu kali.
- [x] Tambahkan regresi perubahan modal, validasi Neraca, test, build, preview, dan checkpoint.

- [x] Bangun APK Android offline dari bundle Anton Service POS terbaru.
- [x] Verifikasi metadata, signature, dan aset offline APK.
- [x] Perbarui dokumentasi instalasi dan simpan checkpoint artefak APK.

- [x] Tambahkan akun dan input biaya “Penyusutan Alat” pada Produk & Jasa.
- [x] Integrasikan Penyusutan Alat ke Buku Besar COA dan perhitungan Laba Rugi.
- [x] Tampilkan Penyusutan Alat sebagai pengurang aset/Modal pada Neraca dengan balance tetap terjaga.
- [x] Tambahkan regresi, validasi test, TypeScript, build, preview, dan checkpoint.

- [x] Tambahkan input Biaya Gedung pada Produk & Jasa.
- [x] Integrasikan Biaya Gedung ke Buku Besar COA dan perhitungan Laba Rugi.
- [x] Tampilkan Biaya Gedung pada Neraca dengan formula balance yang konsisten.
- [x] Tambahkan regresi, validasi test, TypeScript, build, preview, dan checkpoint.

- [x] Tambahkan option tipe “Beban” pada Produk & Jasa dan Kasir POS.
- [x] Catat transaksi pengeluaran Beban serta integrasikan ke Buku Besar COA dan Laba Rugi.
- [x] Tampilkan dampak Beban pada Neraca dan pastikan Aktiva = Pasiva + Modal tanpa duplikasi.
- [x] Tambahkan regresi transaksi Beban, validasi test, TypeScript, build, preview, dan checkpoint.

- [x] Bangun ulang APK Android offline dari versi aplikasi terbaru.
- [x] Verifikasi metadata, signature, checksum, dan aset bundle offline APK.
- [x] Perbarui dokumentasi instalasi serta siapkan artefak APK untuk dibagikan.

- [x] Tambahkan tombol Scan Barcode/QR pada form Produk & Jasa.
- [x] Hubungkan hasil kamera atau input manual ke field barcode untuk menambahkan produk.
- [x] Validasi barcode duplikat, test, build, preview, dan checkpoint.

- [x] Tolak barcode yang sudah dipakai produk lain saat menyimpan Produk & Jasa.
- [x] Tambahkan regresi barcode duplikat untuk produk baru dan edit produk.

- [x] Tambahkan helper validasi barcode yang dapat diuji secara perilaku.
- [x] Uji produk baru, edit mempertahankan barcode sendiri, dan edit ke barcode produk lain.

- [x] Bangun APK Android offline dari versi terbaru Anton Service POS.
- [x] Verifikasi metadata, signature, checksum, dan aset offline APK.
- [x] Perbarui dokumentasi instalasi APK dengan ukuran dan checksum terbaru.
- [x] Simpan checkpoint setelah rebuild APK offline terbaru.

- [x] Tambahkan tombol dan fungsi ekspor Excel untuk halaman Neraca dan Buku Besar.
- [x] Tambahkan tombol dan fungsi ekspor PDF dengan identitas usaha, periode, dan total laporan.
- [x] Tambahkan regresi format/data ekspor serta validasi test, TypeScript, build, preview, dan checkpoint.

- [x] Tambahkan ekspor Excel untuk halaman Neraca dan Buku Besar COA.
- [x] Tambahkan ekspor PDF dengan identitas usaha, rincian akun, dan total laporan.
- [x] Tambahkan regresi untuk struktur data ekspor dan pemicu UI.
- [x] Validasi TypeScript, build, preview, dan checkpoint final ekspor laporan.

- [x] Tambahkan filter tanggal mulai dan tanggal akhir pada Neraca serta Buku Besar COA.
- [x] Pastikan data transaksi, saldo pendapatan, beban, pajak, kas, bank, piutang, dan HPP mengikuti periode ekspor yang dipilih.
- [x] Tampilkan periode aktif pada laporan dan ekspor PDF/Excel.
- [x] Tambahkan regresi filter periode, validasi TypeScript/build/preview, dan checkpoint baru.

- [x] Tambahkan preset cepat Bulan Ini, Bulan Lalu, dan Tahun Ini pada filter Neraca serta Buku Besar COA.
- [x] Pastikan preset mengisi tanggal mulai/akhir dan tetap memengaruhi tampilan serta ekspor laporan.
- [x] Tambahkan regresi preset periode, validasi TypeScript/build/preview, dan checkpoint baru.

- [x] Tambahkan animasi loading saat filter tanggal atau preset periode diterapkan.
- [x] Tambahkan animasi loading saat ekspor PDF/Excel Neraca dan Buku Besar berlangsung.
- [x] Nonaktifkan kontrol terkait selama proses berlangsung dan tambahkan regresi serta validasi final.

- [x] Tampilkan toast sukses di pojok layar setelah ekspor Excel Neraca dan Buku Besar selesai.
- [x] Tambahkan regresi toast ekspor Excel serta validasi TypeScript, build, preview, dan checkpoint.

- [x] Tambahkan bunyi konfirmasi saat barcode/QR berhasil dipindai.
- [x] Tambahkan getaran singkat dengan fallback aman jika API tidak tersedia.
- [x] Integrasikan feedback sukses pada scanner POS dan Produk & Jasa serta tambahkan regresi dan validasi final.

- [x] Tambahkan suara peringatan khusus untuk barcode tidak ditemukan.
- [x] Tambahkan suara dan pola getaran berbeda untuk stok barang kosong.
- [x] Integrasikan kondisi peringatan ke scanner POS, tambahkan regresi, dan validasi final.

- [x] Catat barcode/SKU gagal dengan jenis error, waktu, nama produk jika tersedia, dan jumlah kejadian.
- [x] Simpan riwayat pemindaian gagal di LocalStorage agar tersedia offline.
- [x] Tambahkan halaman/section riwayat dengan hapus satu item dan hapus semua.
- [x] Tambahkan regresi dan validasi TypeScript, build, preview, serta checkpoint final.

- [x] Tambahkan tombol Tambah Barang pada setiap riwayat barcode tidak ditemukan.
- [x] Buka editor Produk & Jasa dengan barcode/SKU riwayat terisi otomatis dan arahkan ke halaman Produk & Jasa.
- [x] Tambahkan regresi interaksi serta validasi TypeScript, build, preview, dan checkpoint final.

- [x] Isi otomatis SKU dengan nomor barcode saat Tambah Barang dibuka dari Riwayat Scan Gagal.
- [x] Pertahankan agar SKU tidak tertimpa setelah pengguna mengeditnya.
- [x] Tambahkan regresi dan validasi TypeScript, build, preview, serta checkpoint final.

- [x] Validasi SKU otomatis atau SKU baru terhadap produk lain sebelum penyimpanan.
- [x] Hentikan penyimpanan dan tampilkan pesan jelas jika SKU sudah digunakan.
- [x] Tambahkan regresi validasi serta validasi TypeScript, build, preview, dan checkpoint final.

- [x] Tampilkan status SKU kosong, tersedia, atau duplikat secara real-time di form Tambah Barang.
- [x] Cegah penyimpanan saat indikator menunjukkan SKU duplikat dan pertahankan validasi submit.
- [x] Tambahkan regresi indikator serta validasi TypeScript, build, preview, dan checkpoint final.

- [x] Bangun ulang bundle web dan APK Android offline dari versi aplikasi terbaru.
- [x] Verifikasi application ID, SDK, signature, aset offline, ukuran, dan checksum APK.
- [x] Perbarui dokumentasi instalasi offline dan siapkan file APK untuk pengguna.

- [x] Tambahkan pilihan biaya Transportasi pada Kasir POS.
- [x] Integrasikan Transportasi sebagai Beban Operasional ke Buku Besar COA, Laba Rugi, dan Neraca.
- [x] Tambahkan regresi transaksi Transportasi, validasi Neraca balance, TypeScript, build, preview, dan checkpoint.

- [x] Tampilkan baris Transportasi di antara Diskon dan Pajak pada ringkasan Kasir POS.
- [x] Pastikan Transportasi tetap tercatat sebagai Beban Operasional ke COA, Laba Rugi, dan Neraca.
- [x] Tambahkan regresi urutan tampilan dan validasi Neraca balance, TypeScript, build, preview, serta checkpoint.

- [x] Aktifkan input manual nominal Transportasi pada Kasir POS.
- [x] Ubah Diskon Kasir POS menjadi persentase dan hitung nominal diskonnya secara konsisten.
- [x] Integrasikan perubahan ke total transaksi, Beban Operasional, COA, laporan, regresi, dan validasi final.

- [x] Periksa mengapa header dari Pengaturan Struk tidak tampil pada seluruh preview struk.
- [x] Sinkronkan identitas header ke semua varian preview dan cetak struk.
- [x] Tambahkan regresi header struk serta validasi TypeScript, build, preview, dan checkpoint.

- [x] Tambahkan pilihan sumber keuangan modal: Kas, Hutang, Bank, QRIS, dan Piutang pada Produk & Jasa.
- [x] Petakan sumber keuangan ke akun Buku Besar dan sesuaikan perubahan stok/modal saat tambah, edit, hapus, serta stok opname.
- [x] Pastikan Neraca tetap balance dan tambahkan regresi, validasi TypeScript, build, preview, serta checkpoint.

- [x] Hapus opsi sumber modal yang tidak diperlukan dari halaman Produk & Jasa.
- [x] Pertahankan opsi Kas, Hutang, Bank, QRIS, dan Piutang serta kompatibilitas data lama.
- [x] Tambahkan regresi opsi modal dan validasi TypeScript, build, preview, serta checkpoint.

- [x] Hapus hanya kolom input Modal yang sejajar dengan Harga Jual pada form Produk & Jasa.
- [x] Pertahankan sumber dana modal dan perhitungan persediaan agar tidak ada fungsi ganda atau Neraca tidak balance.
- [x] Perbarui regresi, validasi TypeScript/build/preview, dan simpan checkpoint perbaikan.

- [x] Hapus kolom input Modal yang sejajar dengan Harga Jual pada form Produk & Jasa tanpa menghapus logika Sumber Keuangan Modal.
- [x] Pertahankan nilai cost produk lama saat edit melalui field internal non-visual dan default aman untuk produk baru.
- [x] Tambahkan regresi penghapusan field Modal visual, jalankan test, TypeScript, build, preview, dan simpan checkpoint.

- [x] Audit arus keuangan persediaan, sumber modal, transaksi penjualan, beban, dan laporan Neraca.
- [x] Pastikan setiap skenario perubahan produk, stok, penjualan, penghapusan transaksi, dan beban menjaga Aktiva = Pasiva + Modal + Laba.
- [x] Tambahkan atau perkuat regresi arus keuangan, validasi TypeScript/build/preview, dan simpan checkpoint.

- [x] Sinkronkan bundle web Anton Service POS terbaru ke wrapper Android offline.
- [x] Bangun dan verifikasi APK Android yang dapat dipasang tanpa koneksi internet.
- [x] Perbarui instruksi instalasi dan siapkan artefak APK untuk pengguna.

- [x] Gunakan logo Anton Service dari pengguna sebagai ikon launcher APK offline.
- [x] Bangun ulang APK offline terbaru dengan ikon baru dan verifikasi artefak instalasi.
- [x] Perbarui dokumentasi instalasi, checksum, dan siapkan APK untuk dibagikan.

- [x] Tambahkan splash screen animatif dengan logo Anton Service saat aplikasi web mulai dibuka.
- [x] Sinkronkan splash screen dan aset logo ke APK Android offline.
- [x] Validasi startup, test, TypeScript/build, preview, dokumentasi, dan checkpoint terbaru.

- [x] Bangun ulang APK offline dari bundle Anton Service POS terbaru.
- [x] Verifikasi APK offline, ikon, splash screen, metadata, signature, dan checksum.
- [x] Siapkan file APK terbaru beserta instruksi pemasangan untuk pengguna.

- [x] Perbaiki integrasi scanner barcode/QR dengan kamera Android dan izin kamera native.
- [x] Pastikan splash screen animatif tampil pada startup web dan APK offline, bukan hanya aset statis.
- [x] Tambahkan regresi, validasi build APK, dan dokumentasikan keterbatasan uji perangkat fisik.

- [x] Tampilkan pesan panduan saat izin kamera ditolak pada scanner barcode.
- [x] Tambahkan tombol Buka Pengaturan untuk Android dan fallback instruksi browser.
- [x] Tambahkan regresi, bangun APK offline, validasi, dan simpan checkpoint.

- [x] Perhalus animasi splash screen dengan transisi masuk dan keluar yang lebih lembut.
- [x] Tambahkan indikator loading startup yang aksesibel dan tetap ringan untuk APK offline.
- [x] Tambahkan regresi, validasi build web/APK, dan simpan checkpoint.

- [x] Rancang jingle startup pendek dengan vokal “Anton Service” dan volume lembut.
- [x] Tambahkan efek getar startup dengan fallback aman bila API tidak tersedia.
- [x] Integrasikan audio ke splash web/APK, tambahkan regresi, validasi, dan checkpoint.

- [x] Tambahkan animasi logo Anton Service zoom in lalu zoom out secara halus sebelum splash menghilang.

- [x] Perbaiki scanner barcode agar tombol Scan benar-benar membuka kamera HP pada APK.
- [x] Pastikan hasil barcode dari kamera terhubung ke POS dan Produk & Jasa, dengan fallback manual.
- [x] Tambahkan regresi native/WebView, bangun APK offline, validasi, dan simpan checkpoint.

- [x] Audit ulang tombol Scan, izin kamera, WebView bridge, stream video, dan hasil decoder barcode.
- [x] Pastikan hasil scan kamera mengisi POS serta Produk & Jasa dan fallback manual tetap aman.
- [x] Tambahkan regresi, bangun APK offline terbaru, validasi, dan simpan checkpoint.

- [x] Tambahkan tombol senter pada layar scan barcode dan hubungkan ke torch kamera belakang.
- [x] Tampilkan status aktif/nonaktif serta fallback bila perangkat tidak mendukung torch.
- [x] Tambahkan regresi, build APK offline, validasi, dan simpan checkpoint.

- [x] Perbaiki scanner barcode agar stabil pada mode online dan offline.
- [x] Perbaiki kontrol senter dan error build `controlsRef`.
- [x] Pastikan bridge kamera, permission Android, fallback manual, regresi, APK, dan checkpoint tervalidasi.

- [x] Tambahkan beep sukses saat barcode berhasil dipindai.
- [x] Tambahkan getaran singkat sukses dengan fallback aman.
- [x] Tambahkan regresi, build APK offline, validasi, dan checkpoint.

- [x] Bangun APK offline terbaru dari bundle aplikasi dengan kamera, senter, beep/getar, dan splash animatif.
- [x] Verifikasi metadata, aset offline, checksum, signature, dan siapkan file instalasi.

- [x] Bangun APK Anton Service POS terbaru untuk instalasi offline.
- [x] Verifikasi bundle offline, metadata, checksum, signature, dan siapkan artefak instalasi.

- [x] Bangun APK Anton Service POS terbaru untuk instalasi offline.
- [x] Verifikasi bundle offline, metadata, checksum, signature, dan siapkan artefak instalasi.

- [x] Bangun file APK Anton Service POS terbaru untuk instalasi offline.
- [x] Verifikasi bundle offline, metadata, checksum, signature, dan siapkan artefak instalasi.

- [x] Audit penyebab APK gagal diinstal pada perangkat pengguna.
- [x] Bangun varian APK kompatibel dan verifikasi metadata, signature, ABI, serta SDK.
- [x] Siapkan file APK perbaikan dan panduan instalasi sesuai pesan error perangkat.

- [x] Periksa ulang keberadaan artefak APK dan siapkan salinan unduhan langsung.
- [x] Verifikasi ulang file APK dan kirim bersama checksum serta panduan instalasi.

- [x] Audit penyebab ikon aplikasi tidak muncul setelah instalasi APK.
- [x] Perbaiki launcher activity, manifest, adaptive icon, dan startup agar aplikasi terlihat normal.
- [x] Bangun APK perbaikan, verifikasi package/icon/signature, dan siapkan panduan instalasi.

- [x] Bangun file APK Anton Service POS terbaru untuk instalasi offline.
- [x] Verifikasi bundle offline, ikon launcher, metadata, checksum, signature, dan siapkan artefak unduhan.

- [x] Audit crash saat dialog izin kamera muncul pada APK Android.
- [x] Perbaiki request permission dan bridge WebView agar tidak crash setelah izin diberikan/ditolak.
- [x] Tambahkan regresi, bangun APK perbaikan, dan verifikasi startup scanner.

- [x] Bangun ulang file APK Anton Service POS untuk instalasi offline sesuai permintaan terbaru.
- [x] Verifikasi metadata, ikon, permission kamera, signature, checksum, dan integritas APK.
- [x] Siapkan artefak APK dan panduan instalasi offline untuk pengguna.

- [x] Siapkan lampiran file APK langsung karena tautan checkpoint belum dapat diakses pengguna.
- [x] Verifikasi ulang artefak APK langsung dan checksum sebelum dikirim.
- [x] Kirim APK sebagai lampiran unduhan langsung beserta instruksi instalasi.

- [x] Audit ulang penyebab APK masih gagal dipasang atau aplikasi menghilang setelah instalasi.
- [x] Buat varian APK kompatibel dengan metadata launcher dan signature yang terverifikasi.
- [x] Kirim APK perbaikan beserta panduan instalasi dan data perangkat yang diperlukan bila masalah berlanjut.

- [x] Audit format APK dan penyebab instalasi tetap ditolak pada perangkat pengguna.
- [x] Buat serta validasi paket instalasi alternatif yang sesuai kompatibilitas perangkat.
- [x] Kirim paket alternatif dan minta merek/model serta versi Android bila error berlanjut.

- [x] Audit APK yang terpasang tetapi tidak dapat bekerja, termasuk startup WebView, asset offline, dan kompatibilitas Android System WebView.
- [x] Perbaiki wrapper Android sesuai hasil audit dan bangun APK uji baru.
- [x] Validasi APK uji serta siapkan panduan pengumpulan log perangkat bila masalah berlanjut.

- [x] Audit crash startup APK yang langsung tertutup pada Oppo Reno 8 ColorOS 14.
- [x] Sederhanakan inisialisasi native/WebView dan bangun APK perbaikan startup.
- [x] Validasi APK perbaikan dan kirim untuk uji ulang di Oppo Reno 8.

- [x] Tambahkan kolom Modal/HPP pada form dan daftar Produk & Jasa.
- [x] Integrasikan Modal/HPP ke persediaan, Buku Besar COA, Neraca, dan Laba Rugi dengan saldo tetap seimbang.
- [x] Tambahkan regresi untuk input, edit, transaksi penjualan, dan penghapusan Produk & Jasa.

- [x] Bangun ulang APK offline dari versi terbaru dengan fitur Modal/HPP.
- [x] Verifikasi metadata paket, signature, permission kamera, ikon, dan checksum APK.
- [x] Kirim file APK langsung beserta instruksi instalasi offline.

- [x] Audit mismatch bundle APK yang belum menampilkan kolom Modal/HPP.
- [x] Pastikan bundle terbaru tersalin ke asset Capacitor sebelum build APK.
- [x] Verifikasi isi bundle APK dan kirim ulang file yang memuat Modal/HPP.
