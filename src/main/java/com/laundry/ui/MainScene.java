package com.laundry.ui;

import com.laundry.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Halaman utama setelah login.
 * Terdiri dari sidebar navigasi (kiri) dan area konten (kanan).
 */
public class MainScene {

    // Menyimpan tombol navigasi yang sedang aktif
    private static Button tombolAktif = null;

    public static Scene create(Stage stage, User user) {

        // ── Sidebar Kiri ─────────────────────────────────────────
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(0, 12, 20, 12));
        sidebar.setStyle("-fx-background-color:" + StyleHelper.BIRU_TUA + ";");

        // Logo
        VBox logoBox = new VBox(4);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(24, 0, 20, 0));
        Label logo = new Label("🧺");
        logo.setFont(Font.font(36));
        Label appName = new Label("LaundryApp");
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        appName.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(logo, appName);

        Separator garis1 = new Separator();
        garis1.setStyle("-fx-background-color:#2c5282;");

        // Info User yang Login
        VBox infoUser = new VBox(2);
        infoUser.setPadding(new Insets(10, 8, 10, 8));
        Label lblNama = new Label("👤 " + user.getNama());
        lblNama.setStyle("-fx-text-fill:#90caf9;-fx-font-size:13px;-fx-font-weight:bold;");
        Label lblRole = new Label(user.getRole().toUpperCase());
        lblRole.setStyle("-fx-text-fill:#63b3ed;-fx-font-size:11px;");
        infoUser.getChildren().addAll(lblNama, lblRole);

        Separator garis2 = new Separator();
        garis2.setStyle("-fx-background-color:#2c5282;");

        // Area Konten (berganti sesuai menu yang dipilih)
        StackPane konten = new StackPane();
        konten.setPadding(new Insets(24));
        konten.setStyle("-fx-background-color:" + StyleHelper.BG + ";");

        // ── Tombol Navigasi ───────────────────────────────────────
        Button[] navMenu = {
                buatTombolNav("🏠  Dashboard"),
                buatTombolNav("👥  Pelanggan"),
                buatTombolNav("🧴  Layanan"),
                buatTombolNav("🧾  Transaksi"),
                buatTombolNav("💰  Pembayaran"),
        };

        // Tampilkan Dashboard saat pertama buka
        konten.getChildren().setAll(DashboardPane.create(user));
        setAktif(navMenu[0]);

        // Aksi setiap tombol menu
        navMenu[0].setOnAction(e -> {
            konten.getChildren().setAll(DashboardPane.create(user));
            setAktif(navMenu[0]);
        });
        navMenu[1].setOnAction(e -> {
            konten.getChildren().setAll(PelangganPane.create());
            setAktif(navMenu[1]);
        });
        navMenu[2].setOnAction(e -> {
            konten.getChildren().setAll(LayananPane.create());
            setAktif(navMenu[2]);
        });
        navMenu[3].setOnAction(e -> {
            konten.getChildren().setAll(TransaksiPane.create(user));
            setAktif(navMenu[3]);
        });
        navMenu[4].setOnAction(e -> {
            konten.getChildren().setAll(PembayaranPane.create());
            setAktif(navMenu[4]);
        });

        // Tombol Keluar
        Region spasi = new Region();
        VBox.setVgrow(spasi, Priority.ALWAYS);

        Button btnKeluar = new Button("🚪  Keluar");
        btnKeluar.setStyle(StyleHelper.btnMerah() + "-fx-min-width:196;-fx-alignment:CENTER_LEFT;");

        // Tambahan: konfirmasi sebelum logout
        btnKeluar.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Yakin ingin keluar dari LaundryApp?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Konfirmasi Logout");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    stage.setScene(LoginScene.create(stage));
                    stage.setTitle("LaundryApp - Login");
                }
            });
        });

        sidebar.getChildren().addAll(logoBox, garis1, infoUser, garis2);
        sidebar.getChildren().addAll(navMenu);
        sidebar.getChildren().addAll(spasi, btnKeluar);

        // ── Root Layout ──────────────────────────────────────────
        HBox root = new HBox(sidebar, konten);
        HBox.setHgrow(konten, Priority.ALWAYS);

        return new Scene(root, 1100, 680);
    }

    private static Button buatTombolNav(String teks) {
        Button btn = new Button(teks);
        btn.setStyle(StyleHelper.btnSidebar(false));
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private static void setAktif(Button btn) {
        if (tombolAktif != null)
            tombolAktif.setStyle(StyleHelper.btnSidebar(false));
        tombolAktif = btn;
        btn.setStyle(StyleHelper.btnSidebar(true));
    }
}