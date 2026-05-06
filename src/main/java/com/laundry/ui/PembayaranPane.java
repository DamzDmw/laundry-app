package com.laundry.ui;

import com.laundry.dao.PembayaranDAO;
import com.laundry.dao.TransaksiDAO;
import com.laundry.model.Pembayaran;
import com.laundry.model.Transaksi;
import com.laundry.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Halaman manajemen Pembayaran.
 * Fitur: Catat pembayaran transaksi yang sudah selesai.
 */
public class PembayaranPane {

    public static Pane create() {
        PembayaranDAO dao  = new PembayaranDAO();
        TransaksiDAO  tDao = new TransaksiDAO();

        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        Label judul = new Label("Manajemen Pembayaran");
        judul.setStyle(StyleHelper.judulHalaman());

        // ── Form Pembayaran ──────────────────────────────────────
        VBox formCard = new VBox(12);
        formCard.setStyle(StyleHelper.card());

        Label judulForm = new Label("Catat Pembayaran");
        judulForm.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Hanya tampilkan transaksi yang BELUM dibayar
        ComboBox<Transaksi> cbTransaksi = new ComboBox<>();
        for (Transaksi t : tDao.getAll()) {
            if (!dao.sudahDibayar(t.getIdTransaksi())) cbTransaksi.getItems().add(t);
        }
        cbTransaksi.setPromptText("Pilih Transaksi (belum dibayar)");
        cbTransaksi.setStyle(StyleHelper.field());
        cbTransaksi.setMaxWidth(Double.MAX_VALUE);
        cbTransaksi.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Transaksi t) {
                return t == null ? "" : t.getKodeTransaksi() + " - " + t.getNamaPelanggan();
            }
            @Override public Transaksi fromString(String s) { return null; }
        });

        TextField tfTotal  = buatField("Total bayar (Rp)");
        TextField tfDiskon = buatField("Diskon (Rp) — opsional");

        ComboBox<String> cbMetode = new ComboBox<>();
        cbMetode.getItems().addAll("tunai", "transfer", "qris");
        cbMetode.setPromptText("Metode pembayaran");
        cbMetode.setStyle(StyleHelper.field());
        cbMetode.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(10);
        grid.add(label("Transaksi"), 0, 0); grid.add(cbTransaksi, 1, 0);
        grid.add(label("Total Bayar"), 0, 1); grid.add(tfTotal,  1, 1);
        grid.add(label("Diskon (Rp)"), 0, 2); grid.add(tfDiskon, 1, 2);
        grid.add(label("Metode"),      0, 3); grid.add(cbMetode,  1, 3);
        for (int i = 0; i < 4; i++) GridPane.setHgrow(grid.getChildren().get(i * 2 + 1), Priority.ALWAYS);

        ObservableList<Pembayaran> data = FXCollections.observableArrayList(dao.getAll());
        TableView<Pembayaran> tabel     = buatTabel(data);

        Button btnSimpan = new Button("💾 Simpan Pembayaran"); btnSimpan.setStyle(StyleHelper.btnHijau());
        Button btnBatal  = new Button("✖ Batal");              btnBatal.setStyle(StyleHelper.btnBiru());
        HBox tombolRow = new HBox(10, btnSimpan, btnBatal);
        tombolRow.setAlignment(Pos.CENTER_LEFT);

        // ── Aksi Simpan ──────────────────────────────────────────
        btnSimpan.setOnAction(e -> {
            if (cbTransaksi.getValue() == null || tfTotal.getText().isBlank() || cbMetode.getValue() == null) {
                peringatan("Transaksi, total bayar, dan metode wajib diisi!"); return;
            }
            try {
                Pembayaran pb = new Pembayaran();
                pb.setIdTransaksi(cbTransaksi.getValue().getIdTransaksi());
                pb.setTotalBayar(Double.parseDouble(tfTotal.getText().trim()));
                pb.setDiskon(tfDiskon.getText().isBlank() ? 0 : Double.parseDouble(tfDiskon.getText().trim()));
                pb.setMetode(cbMetode.getValue());

                if (dao.insert(pb)) {
                    data.setAll(dao.getAll());
                    // Perbarui daftar transaksi belum bayar
                    cbTransaksi.getItems().clear();
                    for (Transaksi t : tDao.getAll()) {
                        if (!dao.sudahDibayar(t.getIdTransaksi())) cbTransaksi.getItems().add(t);
                    }
                    cbTransaksi.setValue(null);
                    tfTotal.clear(); tfDiskon.clear(); cbMetode.setValue(null);
                } else peringatan("Gagal menyimpan pembayaran!");
            } catch (NumberFormatException ex) { peringatan("Total bayar dan diskon harus berupa angka!"); }
        });

        btnBatal.setOnAction(e -> { cbTransaksi.setValue(null); tfTotal.clear(); tfDiskon.clear(); cbMetode.setValue(null); });

        formCard.getChildren().addAll(judulForm, grid, tombolRow);
        root.getChildren().addAll(judul, formCard, tabel);
        return root;
    }

    private static TableView<Pembayaran> buatTabel(ObservableList<Pembayaran> data) {
        TableView<Pembayaran> tabel = new TableView<>(data);
        tabel.setStyle(StyleHelper.card());
        tabel.setPrefHeight(300);
        tabel.getColumns().addAll(
            kolom("ID",          "idPembayaran",  60),
            kolom("Kode Transaksi","kodeTransaksi",160),
            kolom("Pelanggan",   "namaPelanggan", 180),
            kolom("Metode",      "metode",         90),
            kolom("Total (Rp)",  "totalBayar",    130),
            kolom("Diskon (Rp)", "diskon",        110),
            kolom("Tgl Bayar",   "tglBayar",      160)
        );
        return tabel;
    }

    @SuppressWarnings("unchecked")
    private static <T> TableColumn<T, ?> kolom(String nama, String properti, double lebar) {
        TableColumn<T, Object> col = new TableColumn<>(nama);
        col.setCellValueFactory(new PropertyValueFactory<>(properti));
        col.setPrefWidth(lebar);
        return col;
    }

    private static TextField buatField(String placeholder) {
        TextField tf = new TextField(); tf.setPromptText(placeholder); tf.setStyle(StyleHelper.field()); return tf;
    }
    private static Label label(String teks) {
        Label l = new Label(teks); l.setStyle("-fx-font-weight:bold;-fx-font-size:13px;"); return l;
    }
    private static void peringatan(String pesan) { new Alert(Alert.AlertType.WARNING, pesan, ButtonType.OK).showAndWait(); }
}
