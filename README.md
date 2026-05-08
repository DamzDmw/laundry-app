# 🧺 LaundryApp — Sistem Kasir & Manajemen Laundry

Aplikasi desktop berbasis **JavaFX + Maven** yang digunakan untuk membantu proses pengelolaan usaha laundry secara lebih cepat, rapi, dan terstruktur. Sistem ini mendukung pengelolaan pelanggan, layanan laundry, transaksi, hingga pembayaran dengan integrasi database MySQL.

---

# ✨ Fitur Utama

## 📊 Dashboard

* Menampilkan ringkasan data laundry
* Statistik transaksi
* Daftar transaksi terbaru
* Tampilan modern dan sederhana

## 👥 Manajemen Pelanggan

* Tambah data pelanggan
* Edit data pelanggan
* Hapus pelanggan
* Pencarian data pelanggan

## 🧼 Manajemen Layanan

* Menambah jenis layanan laundry
* Mengatur harga layanan
* Edit dan hapus layanan

## 🧾 Manajemen Transaksi

* Membuat transaksi laundry
* Mengubah status cucian
* Menghapus transaksi
* Perhitungan total otomatis

## 💳 Pembayaran

* Mendukung pembayaran:

  * Tunai
  * Transfer
  * QRIS
* Mencatat riwayat pembayaran

---

# 🛠️ Teknologi yang Digunakan

| Teknologi | Keterangan                      |
| --------- | ------------------------------- |
| Java 17   | Bahasa pemrograman utama        |
| JavaFX    | Framework antarmuka desktop     |
| Maven     | Dependency & project management |
| MySQL     | Database penyimpanan data       |
| JDBC      | Koneksi Java ke MySQL           |

---

# 📁 Struktur Proyek

```text
laundry-app/
├── pom.xml
└── src/main/java/com/laundry/
    ├── Main.java
    ├── db/
    │   └── DBConnection.java
    ├── model/
    │   ├── User.java
    │   ├── Pelanggan.java
    │   ├── Layanan.java
    │   ├── Transaksi.java
    │   └── Pembayaran.java
    ├── dao/
    │   ├── UserDAO.java
    │   ├── PelangganDAO.java
    │   ├── LayananDAO.java
    │   ├── TransaksiDAO.java
    │   └── PembayaranDAO.java
    └── ui/
        ├── StyleHelper.java
        ├── LoginScene.java
        ├── MainScene.java
        ├── DashboardPane.java
        ├── PelangganPane.java
        ├── LayananPane.java
        ├── TransaksiPane.java
        └── PembayaranPane.java
```

---

# 🧱 Arsitektur Aplikasi

```text
UI (JavaFX)  →  DAO  →  DBConnection  →  MySQL
   Pane           CRUD      Singleton       Database
```

## Penjelasan

* **UI (JavaFX)** → Menangani tampilan dan interaksi pengguna
* **DAO** → Menjalankan operasi CRUD dan query SQL
* **DBConnection** → Mengatur koneksi database menggunakan pola Singleton
* **Model** → Representasi data/tabel database menggunakan POJO

---

# ⚙️ Persyaratan Sistem

Sebelum menjalankan aplikasi, pastikan sudah menginstal:

* Java JDK 17 atau lebih baru
* Maven 3.6+
* MySQL Server / XAMPP / WAMP
* IDE seperti IntelliJ IDEA atau VS Code

---

# 🚀 Cara Menjalankan Aplikasi

## 1️⃣ Clone Repository

```bash
git clone https://github.com/username/laundry-app.git
cd laundry-app
```

---

## 2️⃣ Import Database

Buka phpMyAdmin atau MySQL CLI lalu jalankan file SQL:

```sql
source sistem_laundry_sederhana.sql
```

Database `laundry_sederhana` beserta tabel akan otomatis dibuat.

---

## 3️⃣ Konfigurasi Database

Buka file:

```text
src/main/java/com/laundry/db/DBConnection.java
```

Lalu sesuaikan konfigurasi berikut:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/laundry_sederhana";
private static final String USER = "root";
private static final String PASS = "";
```

---

## 4️⃣ Jalankan Aplikasi

Gunakan perintah berikut:

```bash
mvn javafx:run
```

---

# 🔐 Akun Login Default

| Username | Password | Role    |
| -------- | -------- | ------- |
| admin    | admin123 | Admin   |
| sari     | sari123  | Petugas |
| riko     | riko123  | Petugas |

---

# 📌 Konsep OOP yang Digunakan

Aplikasi ini menerapkan konsep Object Oriented Programming (OOP):

* **Encapsulation** → Data pada model menggunakan private field dan getter/setter
* **Inheritance** → Struktur class dapat dikembangkan dengan pewarisan
* **Abstraction** → DAO memisahkan logika database dari UI
* **Modularity** → Setiap fitur dipisahkan dalam package berbeda

---

# 🎨 Tampilan Aplikasi

Antarmuka dibuat menggunakan JavaFX dengan desain:

* Modern
* Bersih
* Mudah digunakan
* Responsif untuk desktop

Komponen style dipusatkan pada:

```text
StyleHelper.java
```

agar warna dan tema aplikasi lebih konsisten.

---

# 📈 Pengembangan Selanjutnya

Fitur yang dapat ditambahkan:

* Cetak struk transaksi
* Export laporan PDF
* Grafik pendapatan
* Sistem member pelanggan
* Notifikasi status laundry
* Multi-user authentication

---

# 👨‍💻 Developer

Dikembangkan sebagai proyek pembelajaran Java Desktop menggunakan:

* JavaFX
* Maven
* MySQL
* JDBC

---

# 📄 Lisensi

Project ini dibuat untuk keperluan pembelajaran dan pengembangan sistem desktop Java.
