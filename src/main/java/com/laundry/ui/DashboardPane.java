package com.laundry.ui;

import com.laundry.db.DBConnection;
import com.laundry.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.ResultSet;

/**
 * Halaman Dashboard — menampilkan statistik ringkas dan transaksi terbaru.
 */
public class DashboardPane {

    public static Pane create(User user) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(10));

        // ── Header ───────────────────────────────────────────────
        Label judul = new Label("Dashboard");
        judul.setStyle(StyleHelper.judulHalaman());

        Label sub = new Label("Selamat datang, " + user.getNama() + "! — " + user.getRole().toUpperCase());
        sub.setStyle(StyleHelper.subJudul());

        // ── Kartu Statistik ──────────────────────────────────────
        HBox kartuRow = new HBox(16);
        kartuRow.getChildren().addAll(
            buatKartu("👥", "Total Pelanggan",  hitungDari("SELECT COUNT(*) FROM pelanggan"),                               "#2980b9"),
            buatKartu("🧾", "Total Transaksi",   hitungDari("SELECT COUNT(*) FROM transaksi"),                              "#8e44ad"),
            buatKartu("⏳", "Sedang Diproses",   hitungDari("SELECT COUNT(*) FROM transaksi WHERE status='diproses'"),      StyleHelper.KUNING),
            buatKartu("✅", "Sudah Selesai",     hitungDari("SELECT COUNT(*) FROM transaksi WHERE status='selesai'"),       StyleHelper.HIJAU)
        );
        for (var node : kartuRow.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        // ── Tabel Transaksi Terbaru ───────────────────────────────
        Label lblRecent = new Label("Transaksi Terbaru");
        lblRecent.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblRecent.setTextFill(Color.web(StyleHelper.TEXT_DARK));

        VBox daftarTransaksi = new VBox(8);
        daftarTransaksi.setStyle(StyleHelper.card());

        try {
            String sql =
                "SELECT t.kode_transaksi, p.nama, t.status, t.tgl_masuk " +
                "FROM transaksi t " +
                "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
                "ORDER BY t.created_at DESC LIMIT 5";
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);

            while (rs.next()) {
                HBox baris = new HBox(12);
                baris.setAlignment(Pos.CENTER_LEFT);
                baris.setPadding(new Insets(8, 12, 8, 12));
                baris.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:6;");

                Label kode   = kolom(rs.getString("kode_transaksi"), 160, true);
                Label nama   = kolom(rs.getString("nama") != null ? rs.getString("nama") : "-", 180, false);
                Label tgl    = kolom(rs.getString("tgl_masuk"), 110, false);
                tgl.setStyle("-fx-font-size:12px;-fx-text-fill:#7f8c8d;-fx-min-width:110;");

                Label badge  = buatBadge(rs.getString("status"));
                Region spasi = new Region();
                HBox.setHgrow(spasi, Priority.ALWAYS);

                baris.getChildren().addAll(kode, nama, tgl, spasi, badge);
                daftarTransaksi.getChildren().add(baris);
            }

            if (daftarTransaksi.getChildren().isEmpty()) {
                daftarTransaksi.getChildren().add(new Label("Belum ada transaksi."));
            }
        } catch (Exception e) {
            daftarTransaksi.getChildren().add(new Label("Gagal memuat data: " + e.getMessage()));
        }

        root.getChildren().addAll(judul, sub, kartuRow, lblRecent, daftarTransaksi);
        return root;
    }

    // ── Helper Kartu Statistik ───────────────────────────────────
    private static VBox buatKartu(String ikon, String label, String nilai, String warna) {
        VBox kartu = new VBox(8);
        kartu.setAlignment(Pos.CENTER_LEFT);
        kartu.setPadding(new Insets(20));
        kartu.setStyle("-fx-background-color:" + warna + ";-fx-background-radius:12;" +
                       "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.15),10,0,0,3);");

        Label ico = new Label(ikon);   ico.setFont(Font.font(26));
        Label lbl = new Label(label);  lbl.setStyle("-fx-text-fill:rgba(255,255,255,0.85);-fx-font-size:12px;");
        Label val = new Label(nilai);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        val.setTextFill(Color.WHITE);

        kartu.getChildren().addAll(ico, lbl, val);
        return kartu;
    }

    private static String hitungDari(String sql) {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) { /* abaikan */ }
        return "0";
    }

    private static Label kolom(String teks, double lebar, boolean tebal) {
        Label l = new Label(teks);
        l.setMinWidth(lebar);
        l.setStyle("-fx-font-size:13px;" + (tebal ? "-fx-font-weight:bold;" : ""));
        return l;
    }

    private static Label buatBadge(String status) {
        Label lbl = new Label(status != null ? status.toUpperCase() : "-");
        String warna = switch (status != null ? status : "") {
            case "diterima" -> "#3498db";
            case "diproses" -> StyleHelper.KUNING;
            case "selesai"  -> StyleHelper.HIJAU;
            case "diambil"  -> "#8e44ad";
            default         -> "#95a5a6";
        };
        lbl.setStyle("-fx-background-color:" + warna + ";-fx-text-fill:white;" +
                     "-fx-padding:3 10;-fx-background-radius:20;-fx-font-size:11px;-fx-font-weight:bold;");
        return lbl;
    }
}
