package com.laundry.ui;

import com.laundry.dao.UserDAO;
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
 * Halaman login aplikasi.
 * Terdiri dari panel kiri (merek) dan panel kanan (form login).
 */
public class LoginScene {

    public static Scene create(Stage stage) {

        // ── Panel Kiri: Identitas Aplikasi ───────────────────────
        VBox panelKiri = new VBox(16);
        panelKiri.setAlignment(Pos.CENTER);
        panelKiri.setPrefWidth(360);
        panelKiri.setPadding(new Insets(60));
        panelKiri.setStyle("-fx-background-color:" + StyleHelper.BIRU_TUA + ";");

        Label ikon = new Label("🧺");
        ikon.setFont(Font.font(72));

        Label nama = new Label("LaundryApp");
        nama.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        nama.setTextFill(Color.WHITE);

        Label tagline = new Label("Sistem Manajemen Laundry\nSederhana & Mudah Digunakan");
        tagline.setStyle("-fx-text-fill:#90caf9;-fx-font-size:13px;-fx-text-alignment:center;");
        tagline.setWrapText(true);
        tagline.setAlignment(Pos.CENTER);

        panelKiri.getChildren().addAll(ikon, nama, tagline);

        // ── Panel Kanan: Form Login ──────────────────────────────
        VBox panelKanan = new VBox(20);
        panelKanan.setAlignment(Pos.CENTER);
        panelKanan.setPadding(new Insets(60, 70, 60, 70));
        panelKanan.setStyle("-fx-background-color:#f0f4f8;");

        Label judul = new Label("Selamat Datang");
        judul.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        judul.setTextFill(Color.web(StyleHelper.TEXT_DARK));

        Label subJudul = new Label("Masuk ke akun Anda untuk melanjutkan");
        subJudul.setStyle(StyleHelper.subJudul());

        // Input Username
        Label lblUser = new Label("Username");
        lblUser.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
        TextField tfUser = new TextField();
        tfUser.setPromptText("Masukkan username");
        tfUser.setStyle(StyleHelper.field());
        tfUser.setPrefHeight(40);

        // Input Password
        Label lblPass = new Label("Password");
        lblPass.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
        PasswordField tfPass = new PasswordField();
        tfPass.setPromptText("Masukkan password");
        tfPass.setStyle(StyleHelper.field());
        tfPass.setPrefHeight(40);

        // Label error
        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill:" + StyleHelper.MERAH + ";-fx-font-size:12px;");

        // Tombol Login
        Button btnLogin = new Button("MASUK");
        btnLogin.setStyle(StyleHelper.btnBiru() + "-fx-font-size:14px;-fx-min-width:260;-fx-min-height:42;");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        // Aksi Login
        Runnable aksiLogin = () -> {
            String usr = tfUser.getText().trim();
            String pwd = tfPass.getText().trim();

            if (usr.isEmpty() || pwd.isEmpty()) {
                lblError.setText("⚠ Username dan password tidak boleh kosong!");
                return;
            }

            // Tambahan: validasi panjang minimum username
            if (usr.length() < 3) {
                lblError.setText("⚠ Username minimal 3 karakter!");
                return;
            }

            User user = new UserDAO().login(usr, pwd);
            if (user != null) {
                // Tambahan: dialog sambutan setelah login berhasil
                Alert info = new Alert(Alert.AlertType.INFORMATION,
                        "Selamat datang, " + user.getNama() + "!");
                info.setTitle("Login Berhasil");
                info.showAndWait();
                stage.setScene(MainScene.create(stage, user));
                stage.setTitle("LaundryApp - " + user.getNama());
            } else {
                lblError.setText("⚠ Username atau password salah!");
                tfPass.clear();
            }
        };

        // Tambahan: sembunyikan pesan error saat pengguna mulai mengetik
        tfUser.textProperty().addListener((o, old, newV) -> {
            if (!lblError.getText().isEmpty())
                lblError.setText("");
        });
        tfPass.textProperty().addListener((o, old, newV) -> {
            if (!lblError.getText().isEmpty())
                lblError.setText("");
        });

        btnLogin.setOnAction(e -> aksiLogin.run());
        tfPass.setOnAction(e -> aksiLogin.run()); // enter di password langsung login

        VBox formBox = new VBox(10, lblUser, tfUser, lblPass, tfPass, lblError, btnLogin);
        formBox.setStyle(StyleHelper.card());
        formBox.setPadding(new Insets(24));

        panelKanan.getChildren().addAll(judul, subJudul, formBox);

        // ── Root Layout ──────────────────────────────────────────
        HBox root = new HBox(panelKiri, panelKanan);
        HBox.setHgrow(panelKanan, Priority.ALWAYS);

        return new Scene(root, 800, 500);
    }
}