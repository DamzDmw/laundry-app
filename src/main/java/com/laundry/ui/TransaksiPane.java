package com.laundry.ui;

import com.laundry.dao.*;
import com.laundry.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;
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

        // ================= HEADER =================

        HBox header = new HBox(10);
        Label judul = new Label("Manajemen Transaksi");
        judul.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLaporan = tombol("Buka Laporan", "#8e44ad");
        btnLaporan.setOnAction(e -> bukaLaporan());

        header.getChildren().addAll(judul, spacer, btnLaporan);

        // ================= TABEL =================

        tabel = buatTabel();

        // ================= FORM =================

        VBox formCard = buatForm(user);

        // ================= STATUS =================

        HBox panelStatus = buatPanelStatus();

        root.getChildren().addAll(header, formCard, panelStatus, tabel);

        return root;
    }

    private static VBox buatForm(User user) {

        ComboBox<Pelanggan> cbPelanggan = new ComboBox<>();
        cbPelanggan.getItems().addAll(new PelangganDAO().getAll());
        cbPelanggan.setPromptText("Pilih Pelanggan");

        DatePicker dpMasuk = new DatePicker(LocalDate.now());
        DatePicker dpEstimasi = new DatePicker(LocalDate.now().plusDays(2));

        // ================= TABEL LAYANAN =================

        TableView<Layanan> tabelLayanan = new TableView<>();
        tabelLayanan.setPrefHeight(120);
        tabelLayanan.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabelLayanan.getItems().addAll(new LayananDAO().getAll());

        tabelLayanan.getColumns().addAll(
                kolom("Nama Layanan", "namaLayanan", 180),
                kolom("Jenis", "jenis", 100),
                kolom("Harga/unit", "harga", 120));

        // [FIX] Dua input terpisah: berat (kiloan) dan jumlah (satuan/express)
        TextField tfBerat = field("Berat (kg) — untuk layanan kiloan");
        TextField tfJumlah = field("Jumlah (pcs) — untuk layanan satuan/express");
        TextField tfCatatan = field("Catatan (opsional)");

        // Petunjuk kecil supaya user tidak bingung
        Label lblHint = new Label(
                "Tip: Isi Berat untuk layanan kiloan, isi Jumlah untuk satuan/express.");
        lblHint.setStyle("-fx-text-fill:#888; -fx-font-size:11px;");

        // ================= BUTTON =================

        Button btnBuat = tombol("Buat Transaksi", "#27ae60");
        Button btnHapus = tombol("Hapus", "#e74c3c");

        btnBuat.setOnAction(e -> {
            try {
                aksiBuat(user, cbPelanggan, dpMasuk, dpEstimasi,
                        tabelLayanan, tfBerat, tfJumlah, tfCatatan);
            } catch (Exception ex) {
                ex.printStackTrace();
                alert("Terjadi error:\n" + ex.getMessage());
            }
        });

        btnHapus.setOnAction(e -> aksiHapus());

        HBox barisTanggal = new HBox(10,
                new Label("Masuk:"), dpMasuk,
                new Label("Estimasi:"), dpEstimasi);

        HBox barisTombol = new HBox(10, btnBuat, btnHapus);

        VBox form = new VBox(10,
                cbPelanggan,
                barisTanggal,
                new Label("Pilih layanan (CTRL+Klik untuk multi):"),
                tabelLayanan,
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

            boolean berhasil = dao.updateStatus(
                    pilihan.getIdTransaksi(), cbStatus.getValue());

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

        HBox panel = new HBox(10,
                new Label("Update Status:"), cbStatus, btnUpdate);
        panel.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:12;" +
                        "-fx-background-radius:8;");

        return panel;
    }

    // ================= AKSI BUAT TRANSAKSI =================

    private static void aksiBuat(
            User user,
            ComboBox<Pelanggan> cbPelanggan,
            DatePicker dpMasuk,
            DatePicker dpEstimasi,
            TableView<Layanan> tabelLayanan,
            TextField tfBerat, // [FIX] input berat untuk kiloan
            TextField tfJumlah, // [FIX] input jumlah untuk satuan/express
            TextField tfCatatan) {

        // ================= VALIDASI =================

        if (cbPelanggan.getValue() == null) {
            alert("Pilih pelanggan!");
            return;
        }

        List<Layanan> dipilih = new ArrayList<>(
                tabelLayanan.getSelectionModel().getSelectedItems());

        if (dipilih.isEmpty()) {
            alert("Pilih minimal 1 layanan!");
            return;
        }

        if (dpEstimasi.getValue().isBefore(dpMasuk.getValue())) {
            alert("Estimasi tidak boleh sebelum tanggal masuk!");
            return;
        }

        // [FIX] Parse dua input terpisah
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

        // [FIX] Validasi: pastikan input yang relevan sudah diisi
        // berdasarkan jenis layanan yang dipilih
        for (Layanan l : dipilih) {
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

        // ================= INSERT TRANSAKSI =================

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

        // ================= INSERT DETAIL TRANSAKSI =================
        // [FIX] Setiap layanan dibuatkan DetailTransaksi sendiri,
        // dengan beratKg atau jumlah sesuai jenisnya

        for (Layanan l : dipilih) {
            try {
                DetailTransaksi d = new DetailTransaksi();
                d.setIdTransaksi(idBaru);
                d.setIdLayanan(l.getIdLayanan());

                if ("kiloan".equals(l.getJenis())) {
                    // Kiloan: isi beratKg, jumlah = 0
                    d.setBeratKg(berat);
                    d.setJumlah(0);
                } else {
                    // Satuan / Express: isi jumlah, beratKg = 0
                    d.setBeratKg(0);
                    d.setJumlah(jumlah);
                }

                boolean ok = dtDao.insert(d);
                if (!ok) {
                    System.err.println("Gagal insert detail untuk layanan: " + l.getNamaLayanan());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // ================= RESET FORM & REFRESH =================

        refreshData();
        cbPelanggan.setValue(null);
        tfBerat.clear();
        tfJumlah.clear();
        tfCatatan.clear();
        tabelLayanan.getSelectionModel().clearSelection();

        alert("Transaksi berhasil dibuat!");
    }

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

    @SuppressWarnings("unchecked")
    private static <T> TableColumn<T, ?> kolom(String nama, String property, double width) {
        TableColumn<T, Object> c = new TableColumn<>(nama);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }
}