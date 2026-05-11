package com.laundry.ui;

import com.laundry.db.DBConnection;
import com.laundry.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.sql.ResultSet;

public class DashboardPane {

    public static Pane create(User user) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        Label judul = new Label("Dashboard");
        judul.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        Label sub = new Label("Selamat datang, " + user.getNama() + " (" + user.getRole().toUpperCase() + ")");
        sub.setStyle("-fx-text-fill:#7f8c8d;");

        HBox kartu = new HBox(16);
        kartu.getChildren().addAll(
            buatKartu("👥 Pelanggan", hitung("SELECT COUNT(*) FROM pelanggan"),                                  "#2980b9"),
            buatKartu("🧾 Transaksi", hitung("SELECT COUNT(*) FROM transaksi"),                                  "#8e44ad"),
            buatKartu("⏳ Diproses",  hitung("SELECT COUNT(*) FROM transaksi WHERE status='diproses'"),           "#f39c12"),
            buatKartu("✅ Selesai",   hitung("SELECT COUNT(*) FROM transaksi WHERE status='selesai'"),            "#27ae60")
        );
        for (var n : kartu.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        root.getChildren().addAll(judul, sub, kartu);
        return root;
    }

    private static VBox buatKartu(String label, String nilai, String warna) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill:rgba(255,255,255,0.85);-fx-font-size:13px;");

        Label lblNilai = new Label(nilai);
        lblNilai.setStyle("-fx-text-fill:white;-fx-font-size:26px;-fx-font-weight:bold;");

        VBox k = new VBox(6, lblLabel, lblNilai);
        k.setPadding(new Insets(20));
        k.setStyle("-fx-background-color:" + warna + ";-fx-background-radius:10;");
        return k;
    }

    private static String hitung(String sql) {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { /* abaikan */ }
        return "0";
    }
}
