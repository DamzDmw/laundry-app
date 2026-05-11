package com.laundry.ui;

import com.laundry.dao.PelangganDAO;
import com.laundry.model.Pelanggan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class PelangganPane {

    private static PelangganDAO dao = new PelangganDAO();
    private static ObservableList<Pelanggan> data;
    private static TableView<Pelanggan> tabel;

    public static Pane create() {
        data = FXCollections.observableArrayList(dao.getAll());

        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        Label judul = new Label("Manajemen Pelanggan");
        judul.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        TextField tfNama   = field("Nama Lengkap");
        TextField tfNoHp   = field("No. HP (10-13 digit)");
        TextField tfAlamat = field("Alamat");

        tabel = buatTabel();

        tabel.getSelectionModel().selectedItemProperty().addListener((o, lama, p) -> {
            if (p != null) {
                tfNama.setText(p.getNama());
                tfNoHp.setText(p.getNoHp());
                tfAlamat.setText(p.getAlamat() != null ? p.getAlamat() : "");
            }
        });

        Button btnSimpan = tombol("💾 Simpan", "#27ae60");
        Button btnHapus  = tombol("🗑 Hapus",  "#e74c3c");
        Button btnBatal  = tombol("✖ Batal",   "#1e6091");

        btnSimpan.setOnAction(e -> aksiSimpan(tfNama, tfNoHp, tfAlamat));
        btnHapus.setOnAction(e  -> aksiHapus());
        btnBatal.setOnAction(e  -> bersih(tfNama, tfNoHp, tfAlamat));

        HBox form = new HBox(10, tfNama, tfNoHp, tfAlamat, btnSimpan, btnHapus, btnBatal);
        form.setStyle("-fx-background-color:white;-fx-padding:16;-fx-background-radius:8;");

        root.getChildren().addAll(judul, form, tabel);
        return root;
    }

    private static void aksiSimpan(TextField tfNama, TextField tfNoHp, TextField tfAlamat) {
        if (tfNama.getText().isBlank() || tfNoHp.getText().isBlank()) {
            alert("Nama dan No. HP wajib diisi!"); return;
        }
        if (!tfNoHp.getText().trim().matches("\\d{10,13}")) {
            alert("Format No. HP tidak valid (10-13 digit angka)!"); return;
        }
        Pelanggan pilihan = tabel.getSelectionModel().getSelectedItem();
        if (pilihan != null) {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Edit data " + pilihan.getNama() + "?", ButtonType.YES, ButtonType.NO);
            c.showAndWait().ifPresent(b -> {
                if (b == ButtonType.YES) {
                    pilihan.setNama(tfNama.getText().trim());
                    pilihan.setNoHp(tfNoHp.getText().trim());
                    pilihan.setAlamat(tfAlamat.getText().trim());
                    if (dao.update(pilihan)) refresh(); else alert("Gagal mengedit!");
                }
            });
        } else {
            Pelanggan p = new Pelanggan(0, tfNama.getText().trim(), tfNoHp.getText().trim(), tfAlamat.getText().trim());
            if (dao.insert(p)) refresh(); else alert("Gagal menambah!");
        }
        bersih(tfNama, tfNoHp, tfAlamat);
    }

    private static void aksiHapus() {
        Pelanggan p = tabel.getSelectionModel().getSelectedItem();
        if (p == null) { alert("Pilih pelanggan terlebih dahulu!"); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Hapus pelanggan " + p.getNama() + "?", ButtonType.YES, ButtonType.NO);
        c.setHeaderText("Tindakan ini tidak dapat dibatalkan!");
        c.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                if (dao.delete(p.getIdPelanggan())) refresh(); else alert("Gagal menghapus!");
            }
        });
    }

    private static TableView<Pelanggan> buatTabel() {
        TableView<Pelanggan> t = new TableView<>(data);
        t.setPrefHeight(400);
        t.getColumns().addAll(
            kolom("ID",      "idPelanggan", 60),
            kolom("Nama",    "nama",        200),
            kolom("No. HP",  "noHp",        140),
            kolom("Alamat",  "alamat",      300)
        );
        return t;
    }

    private static void refresh() { data.setAll(dao.getAll()); tabel.getSelectionModel().clearSelection(); }
    private static void bersih(TextField... fs) { tabel.getSelectionModel().clearSelection(); for (TextField f : fs) f.clear(); }
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
