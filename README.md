# LaundryApp — Sistem Manajemen Laundry (JavaFX + Maven)

Aplikasi manajemen laundry sederhana berbasis JavaFX dengan database MySQL.

---

## 📁 Struktur Proyek

```
laundry-app/
├── pom.xml
└── src/main/java/com/laundry/
    ├── Main.java              ← Titik masuk aplikasi
    ├── db/
    │   └── DBConnection.java  ← Koneksi ke MySQL
    ├── model/                 ← Kelas data (POJO)
    │   ├── User.java
    │   ├── Pelanggan.java
    │   ├── Layanan.java
    │   ├── Transaksi.java
    │   └── Pembayaran.java
    ├── dao/                   ← Operasi database (CRUD)
    │   ├── UserDAO.java
    │   ├── PelangganDAO.java
    │   ├── LayananDAO.java
    │   ├── TransaksiDAO.java
    │   └── PembayaranDAO.java
    └── ui/                    ← Tampilan JavaFX
        ├── StyleHelper.java   ← Warna & style terpusat
        ├── LoginScene.java
        ├── MainScene.java
        ├── DashboardPane.java
        ├── PelangganPane.java
        ├── LayananPane.java
        ├── TransaksiPane.java
        └── PembayaranPane.java
```

---

## ⚙️ Persyaratan

- Java 17 atau lebih baru
- Maven 3.6+
- MySQL Server (XAMPP / WAMP / native)

---

## 🛠️ Langkah Setup

### 1. Import Database
Buka phpMyAdmin atau MySQL CLI, lalu jalankan:
```sql
source sistem_laundry_sederhana.sql
```
Ini akan membuat database `laundry_sederhana` beserta tabel dan data contoh.

### 2. Sesuaikan Koneksi Database
Edit file `src/main/java/com/laundry/db/DBConnection.java`:
```java
private static final String URL  = "jdbc:mysql://localhost:3306/laundry_sederhana";
private static final String USER = "root";   // ganti sesuai username MySQL kamu
private static final String PASS = "";       // ganti sesuai password MySQL kamu
```

### 3. Jalankan Aplikasi
```bash
cd laundry-app
mvn javafx:run
```

---

## 🔑 Akun Login (Data Contoh)

| Username | Password  | Role    |
|----------|-----------|---------|
| admin    | admin123  | admin   |
| sari     | sari123   | petugas |
| riko     | riko123   | petugas |

---

## 📋 Fitur Aplikasi

| Menu        | Fitur                                          |
|-------------|------------------------------------------------|
| Dashboard   | Statistik ringkas + 5 transaksi terbaru        |
| Pelanggan   | Tambah, edit, hapus data pelanggan             |
| Layanan     | Kelola jenis layanan dan harga                 |
| Transaksi   | Buat transaksi, update status, hapus           |
| Pembayaran  | Catat pembayaran (tunai/transfer/QRIS)         |

---

## 🏗️ Arsitektur Aplikasi

```
UI (JavaFX)  →  DAO  →  DBConnection  →  MySQL
   Pane           CRUD     Singleton        DB
```

- **Model**: Kelas POJO yang merepresentasikan tabel database
- **DAO**: Berisi semua query SQL (dipisah dari UI)
- **UI**: Hanya menangani tampilan dan interaksi pengguna
