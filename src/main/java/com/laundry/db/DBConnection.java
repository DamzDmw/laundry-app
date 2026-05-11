package com.laundry.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sistem_laundry?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                System.out.println(">>> Mencoba koneksi ke: " + URL);
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println(">>> KONEKSI BERHASIL!");
            }
        } catch (Exception e) {
            System.err.println(">>> KONEKSI GAGAL: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}