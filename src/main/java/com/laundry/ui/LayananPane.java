package com.laundry.ui;

import com.laundry.dao.LayananDAO;
import com.laundry.model.Layanan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class LayananPane {

    private static LayananDAO dao = new LayananDAO();
    private static ObservableList<Layanan> data;
    private static TableView<Layanan> tabel;

    public static Pane create() {
        data = FXCollections.observableArrayList(dao.getAll());

        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        Label judul = new Label("Manajemen Layanan");
        judul.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        TextField tfNama     = field("Nama Layanan");
        TextField tfHarga    = field("Harga (Rp)");
        TextField tfEstimasi = field("Estimasi Hari");

        ComboBox<String> cbJenis = new ComboBox<>();
        cbJenis.getItems().addAll("kiloan", "satuan", "express");
        cbJenis.setPromptText("Jenis");
        cbJenis.setStyle("-fx-border-color:#dce1e7;-fx-border-radius:6;-fx-background-radius:6;");

        tabel = buatTabel();

        tabel.getSelectionModel().selectedItemProperty().addListener((o, lama, l) -> {
            if (l != null) {
                tfNama.setText(l.getNamaLayanan());
                cbJenis.setValue(l.getJenis());
                tfHarga.setText(String.valueOf((int) l.getHarga()));
                tfEstimasi.setText(String.valueOf(l.getEstimasiHari()));
            }
        });

        Button btnSimpan = tombol("💾 Simpan", "#27ae60");
        Button btnHapus  = tombol("🗑 Hapus",  "#e74c3c");
        Button btnBatal  = tombol("✖ Batal",   "#1e6091");

        btnSimpan.setOnAction(e -> aksiSimpan(tfNama, cbJenis, tfHarga, tfEstimasi));
        btnHapus.setOnAction(e  -> aksiHapus());
        btnBatal.setOnAction(e  -> { tabel.getSelectionModel().clearSelection(); bersih(tfNama, tfHarga, tfEstimasi); cbJenis.setValue(null); });

        HBox form = new HBox(10, tfNama, cbJenis, tfHarga, tfEstimasi, btnSimpan, btnHapus, btnBatal);
        form.setStyle("-fx-background-color:white;-fx-padding:16;-fx-background-radius:8;");

        root.getChildren().addAll(judul, form, tabel);
        return root;
    }

    private static void aksiSimpan(TextField tfNama, ComboBox<String> cbJenis, TextField tfHarga, TextField tfEstimasi) {
        if (tfNama.getText().isBlank() || cbJenis.getValue() == null || tfHarga.getText().isBlank()) {
            alert("Nama, jenis, dan harga wajib diisi!"); return;
        }
        try {
            double harga = Double.parseDouble(tfHarga.getText().trim());
            if (harga <= 0) { alert("Harga harus lebih dari 0!"); return; }
            int estimasi = tfEstimasi.getText().isBlank() ? 2 : Integer.parseInt(tfEstimasi.getText().trim());

            Layanan pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan != null) {
                pilihan.setNamaLayanan(tfNama.getText().trim());
                pilihan.setJenis(cbJenis.getValue());
                pilihan.setHarga(harga);
                pilihan.setEstimasiHari(estimasi);
                if (dao.update(pilihan)) refresh(); else alert("Gagal mengedit!");
            } else {
                Layanan l = new Layanan(0, tfNama.getText().trim(), cbJenis.getValue(), harga, estimasi);
                if (dao.insert(l)) refresh(); else alert("Gagal menambah!");
            }
            bersih(tfNama, tfHarga, tfEstimasi);
            cbJenis.setValue(null);
        } catch (NumberFormatException ex) { alert("Harga dan estimasi harus berupa angka!"); }
    }

    private static void aksiHapus() {
        Layanan l = tabel.getSelectionModel().getSelectedItem();
        if (l == null) { alert("Pilih layanan terlebih dahulu!"); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Hapus layanan " + l.getNamaLayanan() + "?", ButtonType.YES, ButtonType.NO);
        c.showAndWait().ifPresent(b -> { if (b == ButtonType.YES) { if (dao.delete(l.getIdLayanan())) refresh(); else alert("Gagal menghapus!"); } });
    }

    private static TableView<Layanan> buatTabel() {
        TableView<Layanan> t = new TableView<>(data);
        t.setPrefHeight(400);
        t.getColumns().addAll(
            kolom("ID",           "idLayanan",    60),
            kolom("Nama Layanan", "namaLayanan",  200),
            kolom("Jenis",        "jenis",        100),
            kolom("Harga (Rp)",   "harga",        120),
            kolom("Estimasi Hari","estimasiHari", 110)
        );
        return t;
    }

    private static void refresh() { data.setAll(dao.getAll()); tabel.getSelectionModel().clearSelection(); }
    private static void bersih(TextField... fs) { for (TextField f : fs) f.clear(); }
    private static void alert(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
    private static TextField field(String ph) { TextField tf = new TextField(); tf.setPromptText(ph); tf.setStyle("-fx-border-color:#dce1e7;-fx-border-radius:6;-fx-background-radius:6;-fx-padding:8;"); return tf; }
    private static Button tombol(String t, String w) { Button b = new Button(t); b.setStyle("-fx-background-color:"+w+";-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:7 14;-fx-background-radius:6;"); return b; }

    @SuppressWarnings("unchecked")
    private static <T> TableColumn<T, ?> kolom(String nama, String prop, double lebar) {
        TableColumn<T, Object> c = new TableColumn<>(nama);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(lebar);
        return c;
    }
}
