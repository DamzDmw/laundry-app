package com.laundry.ui;

import com.laundry.dao.PembayaranDAO;
import com.laundry.dao.TransaksiDAO;
import com.laundry.model.Pembayaran;
import com.laundry.model.Transaksi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class PembayaranPane {

    private static PembayaranDAO dao  = new PembayaranDAO();
    private static TransaksiDAO  tDao = new TransaksiDAO();
    private static ObservableList<Pembayaran> data;

    public static Pane create() {
        data = FXCollections.observableArrayList(dao.getAll());

        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        Label judul = new Label("Manajemen Pembayaran");
        judul.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        ComboBox<Transaksi> cbTransaksi = buatCbTransaksi();
        TextField tfTotal  = field("Total Bayar (Rp)");
        TextField tfDiskon = field("Diskon (Rp) — opsional");

        ComboBox<String> cbMetode = new ComboBox<>();
        cbMetode.getItems().addAll("tunai", "transfer", "qris");
        cbMetode.setPromptText("Metode Pembayaran");
        cbMetode.setStyle("-fx-border-color:#dce1e7;-fx-border-radius:6;-fx-background-radius:6;");

        Button btnSimpan = tombol("💾 Simpan Pembayaran", "#27ae60");
        Button btnBatal  = tombol("✖ Batal", "#1e6091");

        btnSimpan.setOnAction(e -> aksiSimpan(cbTransaksi, tfTotal, tfDiskon, cbMetode));
        btnBatal.setOnAction(e  -> { cbTransaksi.setValue(null); tfTotal.clear(); tfDiskon.clear(); cbMetode.setValue(null); });

        HBox form = new HBox(10, cbTransaksi, tfTotal, tfDiskon, cbMetode, btnSimpan, btnBatal);
        form.setStyle("-fx-background-color:white;-fx-padding:16;-fx-background-radius:8;");
        for (var n : form.getChildren())
            if (n instanceof TextField || n instanceof ComboBox) HBox.setHgrow(n, Priority.ALWAYS);

        TableView<Pembayaran> tabel = buatTabel();

        root.getChildren().addAll(judul, form, tabel);
        return root;
    }

    private static void aksiSimpan(ComboBox<Transaksi> cbTransaksi, TextField tfTotal, TextField tfDiskon, ComboBox<String> cbMetode) {
        if (cbTransaksi.getValue() == null || tfTotal.getText().isBlank() || cbMetode.getValue() == null) {
            alert("Transaksi, total bayar, dan metode wajib diisi!"); return;
        }
        if (dao.sudahDibayar(cbTransaksi.getValue().getIdTransaksi())) {
            alert("Transaksi ini sudah dibayar!"); return;
        }
        try {
            double total = Double.parseDouble(tfTotal.getText().trim());
            if (total <= 0) { alert("Total bayar harus lebih dari 0!"); return; }

            Pembayaran pb = new Pembayaran();
            pb.setIdTransaksi(cbTransaksi.getValue().getIdTransaksi());
            pb.setTotalBayar(total);
            pb.setDiskon(tfDiskon.getText().isBlank() ? 0 : Double.parseDouble(tfDiskon.getText().trim()));
            pb.setMetode(cbMetode.getValue());

            if (dao.insert(pb)) {
                data.setAll(dao.getAll());
                cbTransaksi.getItems().clear();
                for (Transaksi t : tDao.getAll())
                    if (!dao.sudahDibayar(t.getIdTransaksi())) cbTransaksi.getItems().add(t);
                cbTransaksi.setValue(null); tfTotal.clear(); tfDiskon.clear(); cbMetode.setValue(null);
            } else alert("Gagal menyimpan pembayaran!");
        } catch (NumberFormatException ex) { alert("Total bayar dan diskon harus berupa angka!"); }
    }

    private static ComboBox<Transaksi> buatCbTransaksi() {
        ComboBox<Transaksi> cb = new ComboBox<>();
        for (Transaksi t : tDao.getAll())
            if (!dao.sudahDibayar(t.getIdTransaksi())) cb.getItems().add(t);
        cb.setPromptText("Pilih Transaksi (belum dibayar)");
        cb.setStyle("-fx-border-color:#dce1e7;-fx-border-radius:6;-fx-background-radius:6;");
        cb.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Transaksi t) { return t == null ? "" : t.getKodeTransaksi() + " - " + t.getNamaPelanggan(); }
            public Transaksi fromString(String s) { return null; }
        });
        return cb;
    }

    private static TableView<Pembayaran> buatTabel() {
        TableView<Pembayaran> t = new TableView<>(data);
        t.setPrefHeight(380);
        t.getColumns().addAll(
            kolom("ID",             "idPembayaran",  60),
            kolom("Kode Transaksi", "kodeTransaksi", 160),
            kolom("Pelanggan",      "namaPelanggan", 180),
            kolom("Metode",         "metode",         90),
            kolom("Total (Rp)",     "totalBayar",    130),
            kolom("Diskon (Rp)",    "diskon",        110),
            kolom("Tgl Bayar",      "tglBayar",      160)
        );
        return t;
    }

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
