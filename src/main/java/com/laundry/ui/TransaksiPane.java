package com.laundry.ui;

import com.laundry.dao.DetailTransaksiDAO;
import com.laundry.dao.LayananDAO;
import com.laundry.dao.PelangganDAO;
import com.laundry.dao.TransaksiDAO;
import com.laundry.model.DetailTransaksi;
import com.laundry.model.Layanan;
import com.laundry.model.Pelanggan;
import com.laundry.model.Transaksi;
import com.laundry.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransaksiPane {

    private static final String URL_LAPORAN = "http://localhost/laporan/laporan.php";

    public static Pane create(User user) {
        TransaksiDAO       dao   = new TransaksiDAO();
        PelangganDAO       pDao  = new PelangganDAO();
        LayananDAO         lDao  = new LayananDAO();
        DetailTransaksiDAO dtDao = new DetailTransaksiDAO();

        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        // ── Header ───────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label judul = new Label("Manajemen Transaksi");
        judul.setStyle(StyleHelper.judulHalaman());

        Region spacerH = new Region();
        HBox.setHgrow(spacerH, Priority.ALWAYS);

        Button btnLaporan = new Button("📊 Buka Laporan");
        btnLaporan.setStyle(StyleHelper.btnUngu());
        btnLaporan.setOnAction(e -> bukaLaporan());

        headerRow.getChildren().addAll(judul, spacerH, btnLaporan);

        // ── Form Transaksi Baru ──────────────────────────────────
        VBox formCard = new VBox(12);
        formCard.setStyle(StyleHelper.card());

        Label judulForm = new Label("Form Transaksi Baru");
        judulForm.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        ComboBox<Pelanggan> cbPelanggan = new ComboBox<>();
        cbPelanggan.getItems().addAll(pDao.getAll());
        cbPelanggan.setPromptText("Pilih Pelanggan");
        cbPelanggan.setStyle(StyleHelper.field());
        cbPelanggan.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpMasuk    = new DatePicker(LocalDate.now());
        DatePicker dpEstimasi = new DatePicker(LocalDate.now().plusDays(2));
        dpMasuk.setStyle(StyleHelper.field());    dpMasuk.setMaxWidth(Double.MAX_VALUE);
        dpEstimasi.setStyle(StyleHelper.field()); dpEstimasi.setMaxWidth(Double.MAX_VALUE);

        TextField tfCatatan = new TextField();
        tfCatatan.setPromptText("Catatan tambahan (opsional)");
        tfCatatan.setStyle(StyleHelper.field());

        // ── Pilih Layanan di Form Baru ────────────────────────────
        Label lblPilihLayanan = new Label("Pilih Layanan");
        lblPilihLayanan.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        List<Layanan> semuaLayanan = lDao.getAll();
        javafx.util.StringConverter<Layanan> layananConverter = new javafx.util.StringConverter<>() {
            @Override public String toString(Layanan l) {
                if (l == null) return "";
                return l.getNamaLayanan() + " — Rp" + String.format("%,.0f", l.getHarga()) + "/" + l.getJenis();
            }
            @Override public Layanan fromString(String s) { return null; }
        };

        // Tabel pilih layanan untuk form baru
        TableView<Layanan> tabelPilihLayanan = new TableView<>();
        tabelPilihLayanan.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabelPilihLayanan.setPrefHeight(150);
        tabelPilihLayanan.setStyle("-fx-background-color:#f8fafc;-fx-border-color:#e0e0e0;-fx-border-radius:6;");
        tabelPilihLayanan.setPlaceholder(new Label("Tidak ada layanan tersedia."));

        TableColumn<Layanan, String> colNamaL = new TableColumn<>("Nama Layanan");
        colNamaL.setCellValueFactory(new PropertyValueFactory<>("namaLayanan"));
        colNamaL.setPrefWidth(180);
        TableColumn<Layanan, String> colJenisL = new TableColumn<>("Jenis");
        colJenisL.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        colJenisL.setPrefWidth(90);
        TableColumn<Layanan, Double> colHargaL = new TableColumn<>("Harga");
        colHargaL.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colHargaL.setPrefWidth(120);
        tabelPilihLayanan.getColumns().addAll(colNamaL, colJenisL, colHargaL);
        tabelPilihLayanan.getItems().addAll(semuaLayanan);

        // Field jumlah per layanan yang dipilih
        TextField tfJumlahForm = new TextField();
        tfJumlahForm.setPromptText("Jumlah / Berat (kg atau pcs)");
        tfJumlahForm.setStyle(StyleHelper.field());
        tfJumlahForm.setPrefWidth(220);

        Label lblInfoLayanan = new Label("Pilih 1 atau lebih layanan dari tabel (Ctrl+Click untuk multi-pilih)");
        lblInfoLayanan.setStyle("-fx-text-fill:#7f8c8d;-fx-font-size:11px;-fx-font-style:italic;");

        VBox boxPilihLayanan = new VBox(6, lblPilihLayanan, lblInfoLayanan, tabelPilihLayanan,
            new HBox(8, new Label("Jumlah/Berat per layanan:"), tfJumlahForm));
        ((HBox) boxPilihLayanan.getChildren().get(3)).setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(10);
        grid.add(label("Pelanggan"),    0, 0); grid.add(cbPelanggan, 1, 0);
        grid.add(label("Tgl Masuk"),    0, 1); grid.add(dpMasuk,     1, 1);
        grid.add(label("Tgl Estimasi"), 0, 2); grid.add(dpEstimasi,  1, 2);
        grid.add(label("Catatan"),      0, 3); grid.add(tfCatatan,   1, 3);
        for (int i = 0; i < 4; i++) GridPane.setHgrow(grid.getChildren().get(i * 2 + 1), Priority.ALWAYS);

        ObservableList<Transaksi> data = FXCollections.observableArrayList(dao.getAll());
        TableView<Transaksi> tabel     = buatTabelTransaksi(data);

        Button btnSimpan = new Button("💾 Buat Transaksi"); btnSimpan.setStyle(StyleHelper.btnHijau());
        Button btnBatal  = new Button("✖ Batal");           btnBatal.setStyle(StyleHelper.btnBiru());
        HBox formBtn = new HBox(10, btnSimpan, btnBatal);
        formBtn.setAlignment(Pos.CENTER_LEFT);

        // ── Panel Update Status ──────────────────────────────────
        HBox panelStatus = new HBox(12);
        panelStatus.setAlignment(Pos.CENTER_LEFT);
        panelStatus.setStyle(StyleHelper.card());
        panelStatus.setPadding(new Insets(14));

        Label lblStatus = new Label("Update Status:");
        lblStatus.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("diterima", "diproses", "selesai", "diambil");
        cbStatus.setPromptText("Pilih status");
        cbStatus.setStyle(StyleHelper.field());

        Label lblKunci = new Label();
        lblKunci.setStyle("-fx-text-fill:#e74c3c;-fx-font-size:12px;-fx-font-style:italic;");

        Button btnUpdate = new Button("🔄 Update Status"); btnUpdate.setStyle(StyleHelper.btnKuning());
        Button btnHapus  = new Button("🗑 Hapus");         btnHapus.setStyle(StyleHelper.btnMerah());
        panelStatus.getChildren().addAll(lblStatus, cbStatus, btnUpdate, btnHapus, lblKunci);

        // ── Panel Detail Layanan ─────────────────────────────────
        VBox panelDetail = new VBox(10);
        panelDetail.setStyle(StyleHelper.card());
        panelDetail.setVisible(false);
        panelDetail.setManaged(false);

        Label judulDetail = new Label("Detail Layanan Transaksi");
        judulDetail.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox formLayanan = new HBox(10);
        formLayanan.setAlignment(Pos.CENTER_LEFT);

        ComboBox<Layanan> cbLayanan = new ComboBox<>();
        cbLayanan.getItems().addAll(lDao.getAll());
        cbLayanan.setPromptText("Pilih Layanan");
        cbLayanan.setStyle(StyleHelper.field());
        cbLayanan.setPrefWidth(240);
        cbLayanan.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Layanan l) {
                if (l == null) return "";
                return l.getNamaLayanan() + " — Rp" + (long) l.getHarga() + "/" + l.getJenis();
            }
            @Override public Layanan fromString(String s) { return null; }
        });

        TextField tfJumlah = new TextField();
        tfJumlah.setPromptText("Jumlah / Berat");
        tfJumlah.setStyle(StyleHelper.field());
        tfJumlah.setPrefWidth(150);

        Label lblPreview = new Label();
        lblPreview.setStyle("-fx-text-fill:#27ae60;-fx-font-weight:bold;-fx-font-size:13px;");

        Button btnTambahLayanan = new Button("➕ Tambah");
        btnTambahLayanan.setStyle(StyleHelper.btnHijau());

        Button btnHapusLayanan = new Button("🗑 Hapus Layanan");
        btnHapusLayanan.setStyle(StyleHelper.btnMerah());

        formLayanan.getChildren().addAll(cbLayanan, tfJumlah, lblPreview, btnTambahLayanan, btnHapusLayanan);

        ObservableList<DetailTransaksi> dataDetail = FXCollections.observableArrayList();
        TableView<DetailTransaksi> tabelDetail = buatTabelDetail(dataDetail);

        panelDetail.getChildren().addAll(judulDetail, formLayanan, tabelDetail);

        // ── Preview subtotal otomatis ────────────────────────────
        tfJumlah.textProperty().addListener((obs, lama, baru) -> {
            try {
                Layanan l = cbLayanan.getValue();
                if (l != null && !baru.isBlank()) {
                    double sub = l.getHarga() * Double.parseDouble(baru);
                    lblPreview.setText("= Rp " + String.format("%,.0f", sub));
                } else lblPreview.setText("");
            } catch (NumberFormatException ex) { lblPreview.setText("angka tidak valid"); }
        });

        // ── Listener: pilih baris transaksi ─────────────────────
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru == null) {
                panelDetail.setVisible(false);
                panelDetail.setManaged(false);
                lblKunci.setText("");
                btnUpdate.setDisable(false);
                cbStatus.setDisable(false);
                return;
            }

            panelDetail.setVisible(true);
            panelDetail.setManaged(true);
            judulDetail.setText("Detail Layanan — " + baru.getKodeTransaksi()
                    + " (" + baru.getNamaPelanggan() + ")");

            dataDetail.setAll(dtDao.getByTransaksi(baru.getIdTransaksi()));

            boolean final_ = "selesai".equals(baru.getStatus()) || "diambil".equals(baru.getStatus());
            btnUpdate.setDisable(final_);
            cbStatus.setDisable(final_);
            btnTambahLayanan.setDisable(final_);
            btnHapusLayanan.setDisable(final_);

            if (final_) {
                lblKunci.setText("🔒 Status \"" + baru.getStatus() + "\" sudah final.");
            } else {
                lblKunci.setText("");
                cbStatus.setValue(baru.getStatus());
            }
        });

        // ── Aksi Tambah Layanan ──────────────────────────────────
        btnTambahLayanan.setOnAction(e -> {
            Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null)              { peringatan("Pilih transaksi dulu!"); return; }
            if (cbLayanan.getValue() == null) { peringatan("Pilih layanan!"); return; }
            if (tfJumlah.getText().isBlank()) { peringatan("Isi jumlah/berat!"); return; }
            try {
                double jumlah = Double.parseDouble(tfJumlah.getText().trim());
                if (jumlah <= 0) { peringatan("Jumlah harus lebih dari 0!"); return; }

                DetailTransaksi d = new DetailTransaksi();
                d.setIdTransaksi(pilihan.getIdTransaksi());
                d.setIdLayanan(cbLayanan.getValue().getIdLayanan());
                d.setJumlah(jumlah);

                if (dtDao.insert(d)) {
                    dataDetail.setAll(dtDao.getByTransaksi(pilihan.getIdTransaksi()));
                    data.setAll(dao.getAll()); // refresh ringkasan layanan di tabel utama
                    cbLayanan.setValue(null);
                    tfJumlah.clear();
                    lblPreview.setText("");
                } else peringatan("Gagal menambah layanan!");
            } catch (NumberFormatException ex) { peringatan("Jumlah harus berupa angka!"); }
        });

        // ── Aksi Hapus Layanan ───────────────────────────────────
        btnHapusLayanan.setOnAction(e -> {
            Transaksi pilihan   = tabel.getSelectionModel().getSelectedItem();
            DetailTransaksi sel = tabelDetail.getSelectionModel().getSelectedItem();
            if (sel == null) { peringatan("Pilih baris layanan yang ingin dihapus!"); return; }
            if (konfirmasi("Hapus layanan \"" + sel.getNamaLayanan() + "\" dari transaksi ini?")) {
                if (dtDao.delete(sel.getIdDetail())) {
                    dataDetail.setAll(dtDao.getByTransaksi(pilihan.getIdTransaksi()));
                    data.setAll(dao.getAll()); // refresh ringkasan layanan di tabel utama
                } else peringatan("Gagal menghapus layanan!");
            }
        });

        // ── Aksi Simpan Transaksi ────────────────────────────────
        btnSimpan.setOnAction(e -> {
            if (cbPelanggan.getValue() == null) { peringatan("Pilih pelanggan terlebih dahulu!"); return; }
            if (dpEstimasi.getValue().isBefore(dpMasuk.getValue())) {
                peringatan("Tanggal estimasi tidak boleh sebelum tanggal masuk!"); return;
            }
            List<Layanan> layananDipilih = new java.util.ArrayList<>(tabelPilihLayanan.getSelectionModel().getSelectedItems());
            if (layananDipilih.isEmpty()) { peringatan("Pilih minimal satu layanan!"); return; }
            double jumlahPerLayanan = 1;
            if (!tfJumlahForm.getText().isBlank()) {
                try {
                    jumlahPerLayanan = Double.parseDouble(tfJumlahForm.getText().trim());
                    if (jumlahPerLayanan <= 0) { peringatan("Jumlah harus lebih dari 0!"); return; }
                } catch (NumberFormatException ex) { peringatan("Jumlah harus berupa angka!"); return; }
            }
            Transaksi t = new Transaksi();
            t.setIdPelanggan(cbPelanggan.getValue().getIdPelanggan());
            t.setIdUser(user.getIdUser());
            t.setTglMasuk(dpMasuk.getValue().toString());
            t.setTglEstimasi(dpEstimasi.getValue().toString());
            t.setCatatan(tfCatatan.getText().trim());
            int idBaru = dao.insertGetId(t);
            if (idBaru > 0) {
                final double jml = jumlahPerLayanan;
                for (Layanan lyr : layananDipilih) {
                    DetailTransaksi d = new DetailTransaksi();
                    d.setIdTransaksi(idBaru);
                    d.setIdLayanan(lyr.getIdLayanan());
                    d.setJumlah(jml);
                    dtDao.insert(d);
                }
                data.setAll(dao.getAll());
                cbPelanggan.setValue(null);
                tfCatatan.clear();
                tfJumlahForm.clear();
                tabelPilihLayanan.getSelectionModel().clearSelection();
                dpMasuk.setValue(LocalDate.now());
                dpEstimasi.setValue(LocalDate.now().plusDays(2));
            } else peringatan("Gagal membuat transaksi!");
        });

        btnBatal.setOnAction(e -> { cbPelanggan.setValue(null); tfCatatan.clear(); });

        // ── Aksi Update Status ───────────────────────────────────
        btnUpdate.setOnAction(e -> {
            Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null || cbStatus.getValue() == null) {
                peringatan("Pilih transaksi dan status baru!"); return;
            }
            if ("selesai".equals(pilihan.getStatus()) || "diambil".equals(pilihan.getStatus())) {
                peringatan("Status sudah final, tidak bisa diubah!"); return;
            }
            if (dao.updateStatus(pilihan.getIdTransaksi(), cbStatus.getValue()))
                data.setAll(dao.getAll());
            else peringatan("Gagal update status!");
        });

        // ── Aksi Hapus Transaksi ─────────────────────────────────
        btnHapus.setOnAction(e -> {
            Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null) { peringatan("Pilih transaksi yang ingin dihapus!"); return; }
            if (konfirmasi("Hapus transaksi " + pilihan.getKodeTransaksi() + "?")) {
                if (dao.delete(pilihan.getIdTransaksi())) {
                    data.setAll(dao.getAll());
                    panelDetail.setVisible(false);
                    panelDetail.setManaged(false);
                    dataDetail.clear();
                } else peringatan("Gagal menghapus transaksi!");
            }
        });

        formCard.getChildren().addAll(judulForm, grid, boxPilihLayanan, formBtn);
        root.getChildren().addAll(headerRow, formCard, panelStatus, tabel, panelDetail);
        return root;
    }

    private static TableView<Transaksi> buatTabelTransaksi(ObservableList<Transaksi> data) {
        TableView<Transaksi> tabel = new TableView<>(data);
        tabel.setStyle(StyleHelper.card());
        tabel.setPrefHeight(240);
        tabel.getColumns().addAll(
            kolom("Kode",      "kodeTransaksi",    140),
            kolom("Pelanggan", "namaPelanggan",    160),
            kolom("Layanan",   "ringkasanLayanan", 220),
            kolom("Petugas",   "namaUser",         110),
            kolom("Tgl Masuk", "tglMasuk",         110),
            kolom("Estimasi",  "tglEstimasi",      110),
            kolom("Status",    "status",            90),
            kolom("Catatan",   "catatan",          160)
        );
        return tabel;
    }

    private static TableView<DetailTransaksi> buatTabelDetail(ObservableList<DetailTransaksi> data) {
        TableView<DetailTransaksi> tabel = new TableView<>(data);
        tabel.setPrefHeight(180);
        tabel.setStyle("-fx-background-color:#f8fafc;-fx-border-color:#e0e0e0;-fx-border-radius:6;");
        tabel.setPlaceholder(new Label("Belum ada layanan — pilih layanan lalu klik Tambah."));
        tabel.getColumns().addAll(
            kolom("Layanan",      "namaLayanan",  200),
            kolom("Jenis",        "jenis",         90),
            kolom("Jumlah/Berat", "jumlah",       110),
            kolom("Harga Satuan", "hargaSatuan",  130),
            kolom("Subtotal",     "subtotal",     130)
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

    private static void bukaLaporan() {
        try { Desktop.getDesktop().browse(new URI(URL_LAPORAN)); }
        catch (Exception ex) { peringatan("Gagal membuka browser!\nBuka manual: " + URL_LAPORAN); }
    }

    private static Label label(String teks) {
        Label l = new Label(teks);
        l.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
        return l;
    }
    private static void peringatan(String pesan) {
        new Alert(Alert.AlertType.WARNING, pesan, ButtonType.OK).showAndWait();
    }
    private static boolean konfirmasi(String pesan) {
        Optional<ButtonType> h = new Alert(Alert.AlertType.CONFIRMATION, pesan,
                ButtonType.YES, ButtonType.NO).showAndWait();
        return h.isPresent() && h.get() == ButtonType.YES;
    }
}