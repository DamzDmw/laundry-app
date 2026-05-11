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
import java.util.List;

public class TransaksiPane {

    private static final String URL_LAPORAN = "http://localhost/laporan/laporan.php";

    private static TransaksiDAO dao = new TransaksiDAO();
    private static DetailTransaksiDAO dtDao = new DetailTransaksiDAO();

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

        Button btnLaporan = tombol("📊 Buka Laporan", "#8e44ad");
        btnLaporan.setOnAction(e -> bukaLaporan());

        header.getChildren().addAll(judul, spacer, btnLaporan);

        // ================= CONTENT =================
        tabel = buatTabel();

        VBox formCard = buatForm(user);

        HBox panelStatus = buatPanelStatus();

        root.getChildren().addAll(
                header,
                formCard,
                panelStatus,
                tabel);

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
        tabelLayanan.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tabelLayanan.setPrefHeight(120);

        tabelLayanan.getItems().addAll(new LayananDAO().getAll());

        tabelLayanan.getColumns().addAll(
                kolom("Nama Layanan", "namaLayanan", 180),
                kolom("Jenis", "jenis", 90),
                kolom("Harga", "harga", 110));

        TextField tfJumlah = field("Jumlah / Berat per layanan");

        TextField tfCatatan = field("Catatan (opsional)");

        Button btnBuat = tombol("💾 Buat Transaksi", "#27ae60");

        Button btnHapus = tombol("🗑 Hapus Transaksi", "#e74c3c");

        btnBuat.setOnAction(e -> aksiBuat(
                user,
                cbPelanggan,
                dpMasuk,
                dpEstimasi,
                tabelLayanan,
                tfJumlah,
                tfCatatan));

        btnHapus.setOnAction(e -> aksiHapus());

        HBox barisTanggal = new HBox(
                10,
                new Label("Masuk:"),
                dpMasuk,
                new Label("Estimasi:"),
                dpEstimasi);

        HBox barisTombol = new HBox(
                10,
                btnBuat,
                btnHapus);

        VBox form = new VBox(
                10,
                cbPelanggan,
                barisTanggal,
                new Label("Pilih Layanan (Ctrl+Klik untuk multi):"),
                tabelLayanan,
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

        cbStatus.getItems().addAll(
                "diterima",
                "diproses",
                "selesai",
                "diambil");

        cbStatus.setPromptText("Pilih status baru");

        Button btnUpdate = tombol("🔄 Update Status", "#f39c12");

        btnUpdate.setOnAction(e -> {

            Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();

            if (pilihan == null || cbStatus.getValue() == null) {
                alert("Pilih transaksi dan status!");
                return;
            }

            if ("selesai".equals(pilihan.getStatus())
                    || "diambil".equals(pilihan.getStatus())) {

                alert("Status sudah final!");
                return;
            }

            if (dao.updateStatus(
                    pilihan.getIdTransaksi(),
                    cbStatus.getValue())) {

                data.setAll(dao.getAll());

            } else {
                alert("Gagal update status!");
            }
        });

        tabel.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, lama, baru) -> {

                    if (baru != null) {
                        cbStatus.setValue(baru.getStatus());
                    }
                });

        HBox panel = new HBox(
                10,
                new Label("Update Status:"),
                cbStatus,
                btnUpdate);

        panel.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:12;" +
                        "-fx-background-radius:8;");

        return panel;
    }

    private static void aksiBuat(
            User user,
            ComboBox<Pelanggan> cbPelanggan,
            DatePicker dpMasuk,
            DatePicker dpEstimasi,
            TableView<Layanan> tabelLayanan,
            TextField tfJumlah,
            TextField tfCatatan) {

        if (cbPelanggan.getValue() == null) {
            alert("Pilih pelanggan!");
            return;
        }

        List<Layanan> dipilih = new java.util.ArrayList<>(
                tabelLayanan
                        .getSelectionModel()
                        .getSelectedItems());

        if (dipilih.isEmpty()) {
            alert("Pilih minimal satu layanan!");
            return;
        }

        if (dpEstimasi.getValue().isBefore(dpMasuk.getValue())) {
            alert("Tanggal estimasi harus setelah tanggal masuk!");
            return;
        }

        double jumlah = 1;

        if (!tfJumlah.getText().isBlank()) {

            try {

                jumlah = Double.parseDouble(
                        tfJumlah.getText().trim());

                if (jumlah <= 0) {
                    alert("Jumlah harus lebih dari 0!");
                    return;
                }

            } catch (NumberFormatException ex) {
                alert("Jumlah harus berupa angka!");
                return;
            }
        }

        Transaksi t = new Transaksi();

        t.setIdPelanggan(
                cbPelanggan.getValue().getIdPelanggan());

        t.setIdUser(user.getIdUser());

        t.setTglMasuk(dpMasuk.getValue().toString());

        t.setTglEstimasi(dpEstimasi.getValue().toString());

        t.setCatatan(tfCatatan.getText().trim());

        int idBaru = dao.insertGetId(t);

        if (idBaru > 0) {

            final double jml = jumlah;

            for (Layanan l : dipilih) {

                DetailTransaksi d = new DetailTransaksi();

                d.setIdTransaksi(idBaru);

                d.setIdLayanan(l.getIdLayanan());

                d.setJumlah(jml);

                dtDao.insert(d);
            }

            data.setAll(dao.getAll());

            cbPelanggan.setValue(null);

            tfJumlah.clear();

            tfCatatan.clear();

            tabelLayanan.getSelectionModel().clearSelection();

        } else {

            alert("Gagal membuat transaksi!");
        }
    }

    private static void aksiHapus() {

        Transaksi pilihan = tabel.getSelectionModel().getSelectedItem();

        if (pilihan == null) {
            alert("Pilih transaksi yang ingin dihapus!");
            return;
        }

        Alert c = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Hapus transaksi " +
                        pilihan.getKodeTransaksi() +
                        "?",
                ButtonType.YES,
                ButtonType.NO);

        c.showAndWait().ifPresent(b -> {

            if (b == ButtonType.YES) {

                if (dao.delete(
                        pilihan.getIdTransaksi())) {

                    data.setAll(dao.getAll());

                } else {

                    alert("Gagal menghapus!");
                }
            }
        });
    }

    private static TableView<Transaksi> buatTabel() {

        TableView<Transaksi> t = new TableView<>(data);

        t.setPrefHeight(250);

        t.getColumns().addAll(
                kolom("Kode", "kodeTransaksi", 140),
                kolom("Pelanggan", "namaPelanggan", 160),
                kolom("Layanan", "ringkasanLayanan", 200),
                kolom("Tgl Masuk", "tglMasuk", 110),
                kolom("Estimasi", "tglEstimasi", 110),
                kolom("Status", "status", 90));

        return t;
    }

    private static void bukaLaporan() {

        try {

            Desktop.getDesktop().browse(
                    new URI(URL_LAPORAN));

        } catch (Exception ex) {

            alert(
                    "Gagal membuka laporan!\n" +
                            "Buka manual: " + URL_LAPORAN);
        }
    }

    private static void alert(String msg) {

        new Alert(
                Alert.AlertType.WARNING,
                msg,
                ButtonType.OK).showAndWait();
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

    private static Button tombol(String t, String w) {

        Button b = new Button(t);

        b.setStyle(
                "-fx-background-color:" + w + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:7 14;" +
                        "-fx-background-radius:6;");

        return b;
    }

    @SuppressWarnings("unchecked")
    private static <T> TableColumn<T, ?> kolom(
            String nama,
            String prop,
            double lebar) {

        TableColumn<T, Object> c = new TableColumn<>(nama);

        c.setCellValueFactory(
                new PropertyValueFactory<>(prop));

        c.setPrefWidth(lebar);

        return c;
    }
}