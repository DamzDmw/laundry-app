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

public class LoginScene {

        public static Scene create(Stage stage) {

                // ───────────────── PANEL KIRI ─────────────────
                VBox panelKiri = new VBox(16);
                panelKiri.setAlignment(Pos.CENTER);
                panelKiri.setPrefWidth(360);
                panelKiri.setPadding(new Insets(60));
                panelKiri.setStyle("-fx-background-color:#1e6091;");

                Label ikon = new Label("🧺");
                ikon.setFont(Font.font(72));

                Label namaApp = new Label("LaundryApp");
                namaApp.setFont(Font.font("Arial", FontWeight.BOLD, 28));
                namaApp.setTextFill(Color.WHITE);

                Label tagline = new Label("Sistem Manajemen Laundry\nSederhana & Mudah Digunakan");
                tagline.setStyle("-fx-text-fill:#d6eaf8;-fx-font-size:13px;-fx-text-alignment:center;");
                tagline.setWrapText(true);
                tagline.setAlignment(Pos.CENTER);

                panelKiri.getChildren().addAll(ikon, namaApp, tagline);

                // ───────────────── PANEL KANAN ─────────────────
                VBox panelKanan = new VBox(20);
                panelKanan.setAlignment(Pos.CENTER);
                panelKanan.setPadding(new Insets(60, 70, 60, 70));
                panelKanan.setStyle("-fx-background-color:#f0f4f8;");

                Label judul = new Label("Selamat Datang");
                judul.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                judul.setTextFill(Color.web("#1f2937"));

                Label subJudul = new Label("Masuk ke akun Anda untuk melanjutkan");
                subJudul.setStyle("-fx-text-fill:#6b7280;-fx-font-size:13px;");

                Label lblUser = new Label("Username");
                lblUser.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");

                TextField tfUser = new TextField();
                tfUser.setPromptText("Masukkan username");
                tfUser.setPrefHeight(40);
                tfUser.setStyle(fieldStyle());

                Label lblPass = new Label("Password");
                lblPass.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");

                PasswordField tfPass = new PasswordField();
                tfPass.setPromptText("Masukkan password");
                tfPass.setPrefHeight(40);
                tfPass.setStyle(fieldStyle());

                Label lblError = new Label();
                lblError.setStyle("-fx-text-fill:#e74c3c;-fx-font-size:12px;");

                Button btnLogin = new Button("MASUK");
                btnLogin.setMaxWidth(Double.MAX_VALUE);
                btnLogin.setStyle(
                                "-fx-background-color:#1e6091;" +
                                                "-fx-text-fill:white;" +
                                                "-fx-font-weight:bold;" +
                                                "-fx-font-size:14px;" +
                                                "-fx-background-radius:8;" +
                                                "-fx-padding:12;" +
                                                "-fx-cursor:hand;");

                // ───────────────── AKSI LOGIN ─────────────────
                Runnable aksiLogin = () -> {

                        String usr = tfUser.getText().trim();
                        String pwd = tfPass.getText().trim();

                        if (usr.isEmpty() || pwd.isEmpty()) {
                                lblError.setText("⚠ Username dan password wajib diisi!");
                                return;
                        }

                        // [FIX] Ganti dummy user dengan query ke database sungguhan
                        UserDAO userDAO = new UserDAO();
                        User user = userDAO.login(usr, pwd);

                        if (user == null) {
                                // Login gagal — username atau password salah
                                lblError.setText("⚠ Username atau password salah!");
                                return;
                        }

                        // Login berhasil — user sudah punya id_user yang benar dari DB
                        Alert info = new Alert(
                                        Alert.AlertType.INFORMATION,
                                        "Selamat datang, " + user.getNama() + "!");
                        info.setTitle("Login Berhasil");
                        info.setHeaderText(null);
                        info.showAndWait();

                        stage.setScene(MainScene.create(stage, user));
                        stage.setTitle("LaundryApp - " + user.getNama());
                };

                tfUser.textProperty().addListener((o, oldV, newV) -> lblError.setText(""));
                tfPass.textProperty().addListener((o, oldV, newV) -> lblError.setText(""));

                btnLogin.setOnAction(e -> aksiLogin.run());
                tfPass.setOnAction(e -> aksiLogin.run());

                VBox formBox = new VBox(10,
                                lblUser, tfUser,
                                lblPass, tfPass,
                                lblError,
                                btnLogin);

                formBox.setPadding(new Insets(24));
                formBox.setMaxWidth(320);
                formBox.setStyle(
                                "-fx-background-color:white;" +
                                                "-fx-background-radius:12;" +
                                                "-fx-border-radius:12;" +
                                                "-fx-border-color:#e5e7eb;" +
                                                "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08),10,0,0,3);");

                panelKanan.getChildren().addAll(judul, subJudul, formBox);

                HBox root = new HBox(panelKiri, panelKanan);
                HBox.setHgrow(panelKanan, Priority.ALWAYS);

                return new Scene(root, 800, 500);
        }

        private static String fieldStyle() {
                return "-fx-border-color:#dce1e7;" +
                                "-fx-border-radius:8;" +
                                "-fx-background-radius:8;" +
                                "-fx-padding:10;" +
                                "-fx-background-color:white;";
        }
}