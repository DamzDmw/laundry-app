package com.laundry.ui;

import com.laundry.dao.LayananDAO;
import com.laundry.model.Layanan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

/**
 * Halaman manajemen Layanan.
 * Fitur: Tampil, Tambah, Edit, Hapus layanan laundry.
 */
public class LayananPane {

    public static Pane create() {
        LayananDAO dao = new LayananDAO();

        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        Label judul = new Label("Manajemen Layanan");
        judul.setStyle(StyleHelper.judulHalaman());

        // ── Form Input ───────────────────────────────────────────
        VBox formCard = new VBox(12);
        formCard.setStyle(StyleHelper.card());

        Label judulForm = new Label("Form Layanan");
        judulForm.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField tfNama = buatField("Nama layanan");
        TextField tfHarga = buatField("Harga (Rp)");
        TextField tfEstimasi = buatField("Estimasi hari");

        ComboBox<String> cbJenis = new ComboBox<>();
        cbJenis.getItems().addAll("kiloan", "satuan", "express");
        cbJenis.setPromptText("Pilih jenis");
        cbJenis.setStyle(StyleHelper.field());
        cbJenis.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.add(label("Nama Layanan"), 0, 0);
        grid.add(tfNama, 1, 0);
        grid.add(label("Jenis"), 0, 1);
        grid.add(cbJenis, 1, 1);
        grid.add(label("Harga (Rp)"), 0, 2);
        grid.add(tfHarga, 1, 2);
        grid.add(label("Estimasi Hari"), 0, 3);
        grid.add(tfEstimasi, 1, 3);
        for (int i = 0; i < 4; i++) {
            GridPane.setHgrow(grid.getChildren().get(i * 2 + 1), Priority.ALWAYS);
        }

        ObservableList<Layanan> data = FXCollections.observableArrayList(dao.getAll());
        TableView<Layanan> tabel = buatTabel(data);

        Button btnTambah = new Button("➕ Tambah");
        btnTambah.setStyle(StyleHelper.btnHijau());
        Button btnEdit = new Button("✏ Edit");
        btnEdit.setStyle(StyleHelper.btnKuning());
        Button btnHapus = new Button("🗑 Hapus");
        btnHapus.setStyle(StyleHelper.btnMerah());
        Button btnBatal = new Button("✖ Batal");
        btnBatal.setStyle(StyleHelper.btnBiru());

        HBox tombolRow = new HBox(10, btnTambah, btnEdit, btnHapus, btnBatal);
        tombolRow.setAlignment(Pos.CENTER_LEFT);

        // ── Aksi ─────────────────────────────────────────────────
        btnTambah.setOnAction(e -> {
            if (tfNama.getText().isBlank() || cbJenis.getValue() == null || tfHarga.getText().isBlank()) {
                peringatan("Nama, jenis, dan harga wajib diisi!");
                return;
            }
            try {
                Layanan l = new Layanan(0, tfNama.getText().trim(), cbJenis.getValue(),
                        Double.parseDouble(tfHarga.getText().trim()),
                        tfEstimasi.getText().isBlank() ? 2 : Integer.parseInt(tfEstimasi.getText().trim()));
                if (dao.insert(l)) {
                    data.setAll(dao.getAll());
                    bersihkanForm(tfNama, tfHarga, tfEstimasi, cbJenis);
                } else
                    peringatan("Gagal menambah layanan!");
            } catch (NumberFormatException ex) {
                peringatan("Harga dan estimasi harus berupa angka!");
            }
        });

        btnEdit.setOnAction(e -> {
            Layanan pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null) {
                peringatan("Pilih layanan yang ingin diedit!");
                return;
            }
            try {
                pilihan.setNamaLayanan(tfNama.getText().trim());
                pilihan.setJenis(cbJenis.getValue());
                pilihan.setHarga(Double.parseDouble(tfHarga.getText().trim()));
                pilihan.setEstimasiHari(
                        tfEstimasi.getText().isBlank() ? 2 : Integer.parseInt(tfEstimasi.getText().trim()));
                if (dao.update(pilihan)) {
                    data.setAll(dao.getAll());
                    bersihkanForm(tfNama, tfHarga, tfEstimasi, cbJenis);
                } else
                    peringatan("Gagal mengedit layanan!");
            } catch (NumberFormatException ex) {
                peringatan("Harga dan estimasi harus berupa angka!");
            }
        });

        btnHapus.setOnAction(e -> {
            Layanan pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null) {
                peringatan("Pilih layanan yang ingin dihapus!");
                return;
            }
            if (konfirmasi("Hapus layanan " + pilihan.getNamaLayanan() + "?")) {
                if (dao.delete(pilihan.getIdLayanan()))
                    data.setAll(dao.getAll());
                else
                    peringatan("Gagal menghapus layanan!");
            }
        });

        tabel.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                tfNama.setText(baru.getNamaLayanan());
                cbJenis.setValue(baru.getJenis());
                tfHarga.setText(String.valueOf((int) baru.getHarga()));
                tfEstimasi.setText(String.valueOf(baru.getEstimasiHari()));
            }
        });

        btnBatal.setOnAction(e -> {
            tabel.getSelectionModel().clearSelection();
            bersihkanForm(tfNama, tfHarga, tfEstimasi, cbJenis);
        });

        formCard.getChildren().addAll(judulForm, grid, tombolRow);
        root.getChildren().addAll(judul, formCard, tabel);
        return root;
    }

    private static TableView<Layanan> buatTabel(ObservableList<Layanan> data) {
        TableView<Layanan> tabel = new TableView<>(data);
        tabel.setStyle(StyleHelper.card());
        tabel.setPrefHeight(300);
        tabel.getColumns().addAll(
                kolom("ID", "idLayanan", 60),
                kolom("Nama Layanan", "namaLayanan", 220),
                kolom("Jenis", "jenis", 100),
                kolom("Harga (Rp)", "harga", 120),
                kolom("Estimasi Hari", "estimasiHari", 120));
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
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle(StyleHelper.field());
        return tf;
    }

    private static Label label(String teks) {
        Label l = new Label(teks);
        l.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
        return l;
    }

    private static void bersihkanForm(TextField tfNama, TextField tfHarga, TextField tfEst, ComboBox<String> cb) {
        tfNama.clear();
        tfHarga.clear();
        tfEst.clear();
        cb.setValue(null);
    }

    private static void peringatan(String pesan) {
        new Alert(Alert.AlertType.WARNING, pesan, ButtonType.OK).showAndWait();
    }

    private static boolean konfirmasi(String pesan) {
        Optional<ButtonType> h = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO)
                .showAndWait();
        return h.isPresent() && h.get() == ButtonType.YES;
    }
}
