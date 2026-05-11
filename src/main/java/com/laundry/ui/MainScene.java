package com.laundry.ui;

import com.laundry.model.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainScene {

    private static Button tombolAktif;

    public static Scene create(Stage stage, User user) {
        StackPane konten = new StackPane();
        konten.setPadding(new Insets(20));
        konten.setStyle("-fx-background-color:#f0f4f8;");

        VBox sidebar = buatSidebar(stage, user, konten);

        konten.getChildren().setAll(DashboardPane.create(user));

        HBox root = new HBox(sidebar, konten);
        HBox.setHgrow(konten, Priority.ALWAYS);
        return new Scene(root, 1100, 680);
    }

    private static VBox buatSidebar(Stage stage, User user, StackPane konten) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color:#1a3a5c;");

        Label appName = new Label("🧺 LaundryApp");
        appName.setStyle("-fx-text-fill:white;-fx-font-size:16px;-fx-font-weight:bold;-fx-padding:0 0 10 0;");

        Label lblUser = new Label("👤 " + user.getNama());
        lblUser.setStyle("-fx-text-fill:#90caf9;-fx-font-size:12px;-fx-padding:0 0 10 0;");

        String[] menu = {"🏠 Dashboard", "👥 Pelanggan", "🧴 Layanan", "🧾 Transaksi", "💰 Pembayaran"};
        Button[] nav = new Button[menu.length];
        for (int i = 0; i < menu.length; i++) nav[i] = buatNav(menu[i]);

        setAktif(nav[0]);
        nav[0].setOnAction(e -> { konten.getChildren().setAll(DashboardPane.create(user));   setAktif(nav[0]); });
        nav[1].setOnAction(e -> { konten.getChildren().setAll(PelangganPane.create());        setAktif(nav[1]); });
        nav[2].setOnAction(e -> { konten.getChildren().setAll(LayananPane.create());          setAktif(nav[2]); });
        nav[3].setOnAction(e -> { konten.getChildren().setAll(TransaksiPane.create(user));    setAktif(nav[3]); });
        nav[4].setOnAction(e -> { konten.getChildren().setAll(PembayaranPane.create());       setAktif(nav[4]); });

        Button btnKeluar = new Button("🚪 Keluar");
        btnKeluar.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:8 16;-fx-min-width:178;-fx-background-radius:6;");
        btnKeluar.setOnAction(e -> konfirmasiKeluar(stage));

        Region spasi = new Region();
        VBox.setVgrow(spasi, Priority.ALWAYS);

        sidebar.getChildren().addAll(appName, lblUser);
        sidebar.getChildren().addAll(nav);
        sidebar.getChildren().addAll(spasi, btnKeluar);
        return sidebar;
    }

    private static void konfirmasiKeluar(Stage stage) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin keluar?", ButtonType.YES, ButtonType.NO);
        c.setTitle("Konfirmasi Logout");
        c.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                stage.setScene(LoginScene.create(stage));
                stage.setTitle("LaundryApp");
            }
        });
    }

    private static Button buatNav(String teks) {
        Button b = new Button(teks);
        b.setStyle("-fx-background-color:transparent;-fx-text-fill:white;-fx-font-size:13px;-fx-padding:10 16;-fx-alignment:CENTER_LEFT;-fx-min-width:178;-fx-background-radius:6;");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private static void setAktif(Button btn) {
        if (tombolAktif != null)
            tombolAktif.setStyle("-fx-background-color:transparent;-fx-text-fill:white;-fx-font-size:13px;-fx-padding:10 16;-fx-alignment:CENTER_LEFT;-fx-min-width:178;-fx-background-radius:6;");
        tombolAktif = btn;
        btn.setStyle("-fx-background-color:#2980b9;-fx-text-fill:white;-fx-font-size:13px;-fx-padding:10 16;-fx-alignment:CENTER_LEFT;-fx-min-width:178;-fx-background-radius:6;");
    }
}
