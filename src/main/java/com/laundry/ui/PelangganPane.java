package com.laundry.ui;

import com.laundry.dao.PelangganDAO;
import com.laundry.model.Pelanggan;
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
 * Halaman manajemen Pelanggan.
 * Fitur: Tampil, Tambah, Edit, Hapus pelanggan.
 */
public class PelangganPane {

    public static Pane create() {
        PelangganDAO dao = new PelangganDAO();

        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        // ── Judul ────────────────────────────────────────────────
        Label judul = new Label("Manajemen Pelanggan");
        judul.setStyle(StyleHelper.judulHalaman());

        // ── Form Input ───────────────────────────────────────────
        VBox formCard = new VBox(12);
        formCard.setStyle(StyleHelper.card());

        Label judulForm = new Label("Form Pelanggan");
        judulForm.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField tfNama   = buatField("Nama lengkap");
        TextField tfNoHp   = buatField("Nomor HP");
        TextField tfAlamat = buatField("Alamat");

        // Grid 2 kolom untuk form
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(10);
        grid.add(label("Nama"),   0, 0); grid.add(tfNama,   1, 0);
        grid.add(label("No. HP"), 0, 1); grid.add(tfNoHp,   1, 1);
        grid.add(label("Alamat"), 0, 2); grid.add(tfAlamat, 1, 2);
        GridPane.setHgrow(tfNama,   Priority.ALWAYS);
        GridPane.setHgrow(tfNoHp,   Priority.ALWAYS);
        GridPane.setHgrow(tfAlamat, Priority.ALWAYS);

        // Tabel data pelanggan
        ObservableList<Pelanggan> data = FXCollections.observableArrayList(dao.getAll());
        TableView<Pelanggan> tabel     = buatTabel(data);

        // Tombol aksi
        Button btnTambah = new Button("➕ Tambah");  btnTambah.setStyle(StyleHelper.btnHijau());
        Button btnEdit   = new Button("✏ Edit");     btnEdit.setStyle(StyleHelper.btnKuning());
        Button btnHapus  = new Button("🗑 Hapus");   btnHapus.setStyle(StyleHelper.btnMerah());
        Button btnBatal  = new Button("✖ Batal");    btnBatal.setStyle(StyleHelper.btnBiru());

        HBox tombolRow = new HBox(10, btnTambah, btnEdit, btnHapus, btnBatal);
        tombolRow.setAlignment(Pos.CENTER_LEFT);

        // ── Aksi Tambah ──────────────────────────────────────────
        btnTambah.setOnAction(e -> {
            if (tfNama.getText().isBlank() || tfNoHp.getText().isBlank()) {
                peringatan("Nama dan No. HP wajib diisi!");
                return;
            }
            Pelanggan p = new Pelanggan(0, tfNama.getText().trim(),
                                           tfNoHp.getText().trim(),
                                           tfAlamat.getText().trim());
            if (dao.insert(p)) {
                data.setAll(dao.getAll());
                kosongkanForm(tfNama, tfNoHp, tfAlamat);
            } else peringatan("Gagal menambah pelanggan!");
        });

        // ── Aksi Edit ────────────────────────────────────────────
        btnEdit.setOnAction(e -> {
            Pelanggan pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null) { peringatan("Pilih pelanggan yang ingin diedit!"); return; }
            if (tfNama.getText().isBlank()) { peringatan("Nama wajib diisi!"); return; }

            pilihan.setNama(tfNama.getText().trim());
            pilihan.setNoHp(tfNoHp.getText().trim());
            pilihan.setAlamat(tfAlamat.getText().trim());

            if (dao.update(pilihan)) {
                data.setAll(dao.getAll());
                kosongkanForm(tfNama, tfNoHp, tfAlamat);
            } else peringatan("Gagal mengedit pelanggan!");
        });

        // ── Aksi Hapus ───────────────────────────────────────────
        btnHapus.setOnAction(e -> {
            Pelanggan pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null) { peringatan("Pilih pelanggan yang ingin dihapus!"); return; }
            if (konfirmasi("Hapus pelanggan " + pilihan.getNama() + "?")) {
                if (dao.delete(pilihan.getIdPelanggan())) data.setAll(dao.getAll());
                else peringatan("Gagal menghapus! Mungkin masih ada transaksi terkait.");
            }
        });

        // ── Klik baris tabel → isi form ──────────────────────────
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                tfNama.setText(baru.getNama());
                tfNoHp.setText(baru.getNoHp());
                tfAlamat.setText(baru.getAlamat() != null ? baru.getAlamat() : "");
            }
        });

        btnBatal.setOnAction(e -> {
            tabel.getSelectionModel().clearSelection();
            kosongkanForm(tfNama, tfNoHp, tfAlamat);
        });

        formCard.getChildren().addAll(judulForm, grid, tombolRow);
        root.getChildren().addAll(judul, formCard, tabel);
        return root;
    }

    // ── Helper Tabel ─────────────────────────────────────────────
    private static TableView<Pelanggan> buatTabel(ObservableList<Pelanggan> data) {
        TableView<Pelanggan> tabel = new TableView<>(data);
        tabel.setStyle(StyleHelper.card());
        tabel.setPrefHeight(320);
        tabel.getColumns().addAll(
            kolom("ID",      "idPelanggan", 60),
            kolom("Nama",    "nama",        200),
            kolom("No. HP",  "noHp",        140),
            kolom("Alamat",  "alamat",      300)
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

    // ── Helper Komponen ──────────────────────────────────────────
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

    private static void kosongkanForm(TextField... fields) {
        for (TextField tf : fields) tf.clear();
    }

    private static void peringatan(String pesan) {
        new Alert(Alert.AlertType.WARNING, pesan, ButtonType.OK).showAndWait();
    }

    private static boolean konfirmasi(String pesan) {
        Optional<ButtonType> hasil = new Alert(Alert.AlertType.CONFIRMATION, pesan,
                ButtonType.YES, ButtonType.NO).showAndWait();
        return hasil.isPresent() && hasil.get() == ButtonType.YES;
    }
}
