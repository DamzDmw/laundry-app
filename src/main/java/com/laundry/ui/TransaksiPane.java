package com.laundry.ui;

import com.laundry.dao.*;
import com.laundry.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransaksiPane {

    private static final String URL_LAPORAN = "http://localhost/laporan/laporan.php";

    private static final TransaksiDAO dao = new TransaksiDAO();
    private static final DetailTransaksiDAO dtDao = new DetailTransaksiDAO();

    private static ObservableList<Transaksi> data;
    private static TableView<Transaksi> tabel;

    public static Pane create(User user) {

        data = FXCollections.observableArrayList(dao.getAll());

        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        // ===== HEADER =====
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label judul = new Label("Manajemen Transaksi");
        judul.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLaporan = tombol("Buka Laporan", "#8e44ad");
        btnLaporan.setOnAction(e -> bukaLaporan());

        header.getChildren().addAll(judul, spacer, btnLaporan);

        // ===== TABEL =====
        tabel = buatTabel();

        // ===== FORM =====
        VBox formCard = buatForm(user);

        // ===== PANEL STATUS =====
        HBox panelStatus = buatPanelStatus();

        root.getChildren().addAll(header, formCard, panelStatus, tabel);
        return root;
    }

    // =====================================================================
    // FORM BUAT TRANSAKSI
    // =====================================================================
    private static VBox buatForm(User user) {

        // --- Pelanggan ---
        ComboBox<Pelanggan> cbPelanggan = new ComboBox<>();
        cbPelanggan.getItems().addAll(new PelangganDAO().getAll());
        cbPelanggan.setPromptText("Pilih Pelanggan");
        cbPelanggan.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbPelanggan);

        // --- Tanggal ---
        DatePicker dpMasuk = new DatePicker(LocalDate.now());
        DatePicker dpEstimasi = new DatePicker(LocalDate.now().plusDays(2));

        // --- ComboBox pilih layanan ---
        List<Layanan> semuaLayanan = new LayananDAO().getAll();

        Label lblPilihLayanan = new Label("Pilih Layanan:");
        lblPilihLayanan.setStyle("-fx-font-weight:bold;");

        ComboBox<Layanan> cbLayanan = new ComboBox<>();
        cbLayanan.getItems().addAll(semuaLayanan);
        cbLayanan.setPromptText("-- Pilih Layanan --");
        cbLayanan.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbLayanan);

        // --- Tombol Tambah layanan ke list ---
        Button btnTambahLayanan = tombol("+ Tambah", "#2980b9");

        HBox barisLayanan = new HBox(10, cbLayanan, btnTambahLayanan);
        HBox.setHgrow(cbLayanan, Priority.ALWAYS);
        barisLayanan.setAlignment(Pos.CENTER_LEFT);

        // --- Daftar layanan yang sudah dipilih (ListView) ---
        ObservableList<Layanan> dipilihList = FXCollections.observableArrayList();
        ListView<Layanan> lvDipilih = new ListView<>(dipilihList);
        lvDipilih.setPrefHeight(110);
        lvDipilih.setStyle("-fx-border-color:#dce1e7; -fx-border-radius:6; -fx-background-radius:6;");
        lvDipilih.setPlaceholder(new Label("Belum ada layanan dipilih"));

        // Custom cell supaya tampil nama + jenis + harga, dan ada tombol Hapus
        lvDipilih.setCellFactory(lv -> new ListCell<Layanan>() {
            private final Button btnHapusItem = new Button("✕");
            private final Label lblNama = new Label();
            private final HBox box = new HBox(10, lblNama, new Region(), btnHapusItem);
            {
                HBox.setHgrow(box.getChildren().get(1), Priority.ALWAYS);
                box.setAlignment(Pos.CENTER_LEFT);
                btnHapusItem.setStyle(
                        "-fx-background-color:#e74c3c;-fx-text-fill:white;" +
                                "-fx-font-size:10px;-fx-padding:2 6;-fx-background-radius:4;");
                btnHapusItem.setOnAction(e -> {
                    Layanan item = getItem();
                    if (item != null)
                        dipilihList.remove(item);
                });
            }

            @Override
            protected void updateItem(Layanan item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lblNama.setText(item.getNamaLayanan()
                            + "  [" + item.getJenis() + "]"
                            + "  — Rp" + (long) item.getHarga() + "/" + item.getJenis());
                    setGraphic(box);
                }
            }
        });

        // Logika tombol Tambah: cegah duplikat
        btnTambahLayanan.setOnAction(e -> {
            Layanan dipilih = cbLayanan.getValue();
            if (dipilih == null) {
                alert("Pilih layanan terlebih dahulu!");
                return;
            }
            if (dipilihList.contains(dipilih)) {
                alert("Layanan \"" + dipilih.getNamaLayanan() + "\" sudah ada dalam daftar.");
                return;
            }
            dipilihList.add(dipilih);
            cbLayanan.setValue(null);
        });

        // --- Input Berat & Jumlah ---
        Label lblHint = new Label(
                "Isi Berat (kg) untuk layanan kiloan  |  Isi Jumlah (pcs) untuk satuan / express");
        lblHint.setStyle("-fx-text-fill:#888; -fx-font-size:11px;");

        TextField tfBerat = field("Berat (kg)  — layanan kiloan");
        TextField tfJumlah = field("Jumlah (pcs)  — layanan satuan / express");
        TextField tfCatatan = field("Catatan (opsional)");

        // --- Tombol Buat & Hapus ---
        Button btnBuat = tombol("Buat Transaksi", "#27ae60");
        Button btnHapus = tombol("Hapus", "#e74c3c");

        btnBuat.setOnAction(e -> {
            try {
                aksiBuat(user, cbPelanggan, dpMasuk, dpEstimasi,
                        dipilihList, tfBerat, tfJumlah, tfCatatan);
            } catch (Exception ex) {
                ex.printStackTrace();
                alert("Terjadi error:\n" + ex.getMessage());
            }
        });

        btnHapus.setOnAction(e -> aksiHapus());

        // --- Susunan baris ---
        HBox barisTanggal = new HBox(10,
                new Label("Masuk:"), dpMasuk,
                new Label("Estimasi:"), dpEstimasi);
        barisTanggal.setAlignment(Pos.CENTER_LEFT);

        HBox barisTombol = new HBox(10, btnBuat, btnHapus);

        Label lblDipilih = new Label("Layanan yang dipilih:");
        lblDipilih.setStyle("-fx-font-weight:bold;");

        VBox form = new VBox(10,
                new Label("Pelanggan:"),
                cbPelanggan,
                barisTanggal,
                lblPilihLayanan,
                barisLayanan,
                lblDipilih,
                lvDipilih,
                lblHint,
                tfBerat,
                tfJumlah,
                tfCatatan,
                barisTombol);

        form.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:16;" +
                        "-fx-background-radius:8;");

        return form;
    }

    // =====================================================================
    // PANEL UPDATE STATUS
    // =====================================================================
    private static HBox buatPanelStatus() {

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("diterima", "diproses", "selesai", "diambil");
        cbStatus.setPromptText("Pilih status");

        Button btnUpdate = tombol("Update Status", "#f39c12");

        btnUpdate.setOnAction(e -> {
            Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();
            if (pilihan == null) {
                alert("Pilih transaksi!");
                return;
            }
            if (cbStatus.getValue() == null) {
                alert("Pilih status!");
                return;
            }

            boolean berhasil = dao.updateStatus(pilihan.getIdTransaksi(), cbStatus.getValue());
            if (berhasil)
                refreshData();
            else
                alert("Gagal update status!");
        });

        tabel.getSelectionModel().selectedItemProperty()
                .addListener((o, lama, baru) -> {
                    if (baru != null)
                        cbStatus.setValue(baru.getStatus());
                });

        HBox panel = new HBox(10, new Label("Update Status:"), cbStatus, btnUpdate);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:12;" +
                        "-fx-background-radius:8;");

        return panel;
    }

    // =====================================================================
    // AKSI BUAT TRANSAKSI
    // =====================================================================
    private static void aksiBuat(
            User user,
            ComboBox<Pelanggan> cbPelanggan,
            DatePicker dpMasuk,
            DatePicker dpEstimasi,
            ObservableList<Layanan> dipilihList,
            TextField tfBerat,
            TextField tfJumlah,
            TextField tfCatatan) {

        // --- Validasi wajib ---
        if (cbPelanggan.getValue() == null) {
            alert("Pilih pelanggan!");
            return;
        }
        if (dipilihList.isEmpty()) {
            alert("Tambahkan minimal 1 layanan!");
            return;
        }
        if (dpEstimasi.getValue().isBefore(dpMasuk.getValue())) {
            alert("Estimasi tidak boleh sebelum tanggal masuk!");
            return;
        }

        // --- Parse berat & jumlah ---
        double berat = 0;
        int jumlah = 0;

        String txtBerat = tfBerat.getText().trim();
        String txtJumlah = tfJumlah.getText().trim();

        if (!txtBerat.isEmpty()) {
            try {
                berat = Double.parseDouble(txtBerat);
            } catch (Exception e) {
                alert("Berat harus angka! Contoh: 2.5");
                return;
            }
        }
        if (!txtJumlah.isEmpty()) {
            try {
                jumlah = Integer.parseInt(txtJumlah);
            } catch (Exception e) {
                alert("Jumlah harus angka bulat! Contoh: 3");
                return;
            }
        }

        // --- Validasi tiap layanan ---
        for (Layanan l : dipilihList) {
            if ("kiloan".equals(l.getJenis()) && berat <= 0) {
                alert("Layanan \"" + l.getNamaLayanan() +
                        "\" adalah kiloan.\nIsi kolom Berat (kg) terlebih dahulu!");
                return;
            }
            if (!"kiloan".equals(l.getJenis()) && jumlah <= 0) {
                alert("Layanan \"" + l.getNamaLayanan() +
                        "\" adalah " + l.getJenis() +
                        ".\nIsi kolom Jumlah (pcs) terlebih dahulu!");
                return;
            }
        }

        // --- Insert transaksi ---
        Transaksi t = new Transaksi();
        t.setIdPelanggan(cbPelanggan.getValue().getIdPelanggan());
        t.setIdUser(user.getIdUser());
        t.setTglMasuk(dpMasuk.getValue().toString());
        t.setTglEstimasi(dpEstimasi.getValue().toString());
        t.setCatatan(tfCatatan.getText());

        int idBaru = dao.insertGetId(t);
        System.out.println("ID TRANSAKSI BARU = " + idBaru);

        if (idBaru <= 0) {
            refreshData();
            alert("Transaksi berhasil dibuat.\nSilakan cek tabel.");
            return;
        }

        // --- Insert detail per layanan ---
        for (Layanan l : dipilihList) {
            try {
                DetailTransaksi d = new DetailTransaksi();
                d.setIdTransaksi(idBaru);
                d.setIdLayanan(l.getIdLayanan());

                if ("kiloan".equals(l.getJenis())) {
                    d.setBeratKg(berat);
                    d.setJumlah(0);
                } else {
                    d.setBeratKg(0);
                    d.setJumlah(jumlah);
                }

                boolean ok = dtDao.insert(d);
                if (!ok)
                    System.err.println("Gagal insert detail: " + l.getNamaLayanan());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // --- Reset form ---
        refreshData();
        cbPelanggan.setValue(null);
        dipilihList.clear();
        tfBerat.clear();
        tfJumlah.clear();
        tfCatatan.clear();

        alert("Transaksi berhasil dibuat!");
    }

    // =====================================================================
    // AKSI HAPUS TRANSAKSI
    // =====================================================================
    private static void aksiHapus() {

        Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();
        if (pilihan == null) {
            alert("Pilih transaksi!");
            return;
        }

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus transaksi " + pilihan.getKodeTransaksi() + " ?",
                ButtonType.YES, ButtonType.NO);

        konfirmasi.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                boolean berhasil = dao.delete(pilihan.getIdTransaksi());
                if (berhasil)
                    refreshData();
                else
                    alert("Gagal menghapus!");
            }
        });
    }

    // =====================================================================
    // TABEL TRANSAKSI
    // =====================================================================
    private static TableView<Transaksi> buatTabel() {

        TableView<Transaksi> t = new TableView<>(data);
        t.setPrefHeight(260);

        t.getColumns().addAll(
                kolom("Kode", "kodeTransaksi", 140),
                kolom("Pelanggan", "namaPelanggan", 160),
                kolom("Layanan", "ringkasanLayanan", 220),
                kolom("Tanggal Masuk", "tglMasuk", 120),
                kolom("Estimasi", "tglEstimasi", 120),
                kolom("Status", "status", 100));

        return t;
    }

    // =====================================================================
    // HELPER
    // =====================================================================
    private static void refreshData() {
        data.setAll(dao.getAll());
    }

    private static void bukaLaporan() {
        try {
            Desktop.getDesktop().browse(new URI(URL_LAPORAN));
        } catch (Exception e) {
            alert("Gagal membuka laporan!\n" + URL_LAPORAN);
        }
    }

    private static void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private static TextField field(String ph) {
        TextField tf = new TextField();
        tf.setPromptText(ph);
        tf.setStyle(
                "-fx-border-color:#dce1e7;" +
                        "-fx-border-radius:6;" +
                        "-fx-background-radius:6;" +
                        "-fx-padding:8;");
        return tf;
    }

    private static Button tombol(String text, String warna) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:" + warna + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:7 14;" +
                        "-fx-background-radius:6;");
        return b;
    }

    private static void styleCombo(ComboBox<?> cb) {
        cb.setStyle(
                "-fx-border-color:#dce1e7;" +
                        "-fx-border-radius:6;" +
                        "-fx-background-radius:6;" +
                        "-fx-padding:4;");
    }

    @SuppressWarnings("unchecked")
    private static <T> TableColumn<T, ?> kolom(String nama, String property, double width) {
        TableColumn<T, Object> c = new TableColumn<>(nama);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }
}