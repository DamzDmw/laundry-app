package com.laundry.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Kelas untuk mengelola koneksi ke database MySQL.
 * Menggunakan pola Singleton agar hanya ada satu koneksi aktif.
 */
public class DBConnection {

    // Sesuaikan dengan konfigurasi database kamu
    private static final String URL  = "jdbc:mysql://localhost:3306/sistem_laundry";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connection;

    // Mencegah pembuatan objek dari luar
    private DBConnection() {}

    /**
     * Mengembalikan koneksi yang sudah ada, atau membuat baru jika belum ada.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (Exception e) {
            System.err.println("Gagal koneksi database: " + e.getMessage());
        }
        return connection;
    }
}
