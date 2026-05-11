package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.DetailTransaksi;
import com.laundry.model.Layanan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiDAO {

    // ================= GET DETAIL BY TRANSAKSI =================

    public List<DetailTransaksi> getByTransaksi(int idTransaksi) {

        List<DetailTransaksi> list = new ArrayList<>();

        String sql = "SELECT dt.*, " +
                "l.nama_layanan, " +
                "l.jenis, " +
                "l.harga AS harga_satuan " +
                "FROM detail_transaksi dt " +
                "JOIN layanan l " +
                "ON dt.id_layanan = l.id_layanan " +
                "WHERE dt.id_transaksi = ?";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetailTransaksi d = new DetailTransaksi();
                d.setIdDetail(rs.getInt("id_detail"));
                d.setIdTransaksi(rs.getInt("id_transaksi"));
                d.setIdLayanan(rs.getInt("id_layanan"));
                d.setBeratKg(rs.getDouble("berat_kg"));
                d.setJumlah(rs.getDouble("jumlah"));
                d.setSubtotal(rs.getDouble("subtotal"));
                d.setNamaLayanan(rs.getString("nama_layanan"));
                d.setJenis(rs.getString("jenis"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                list.add(d);
            }

        } catch (Exception e) {
            System.err.println("Error getByTransaksi:");
            e.printStackTrace();
        }

        return list;
    }

    // ================= INSERT DETAIL =================
    // [FIX] Membedakan perhitungan subtotal berdasarkan jenis layanan:
    //   - kiloan         -> subtotal = harga x beratKg
    //   - satuan/express -> subtotal = harga x jumlah (pcs)

    public boolean insert(DetailTransaksi d) {

        // Ambil data layanan lengkap (harga + jenis)
        Layanan layanan = getLayananById(d.getIdLayanan());

        if (layanan == null) {
            System.err.println("Error insert detail: layanan tidak ditemukan id=" + d.getIdLayanan());
            return false;
        }

        double harga  = layanan.getHarga();
        String jenis  = layanan.getJenis();

        // [FIX] Tentukan beratKg, jumlah, dan subtotal sesuai jenis layanan
        double beratKg;
        int    jumlah;
        double subtotal;

        if ("kiloan".equals(jenis)) {
            // Layanan kiloan -> pakai beratKg dari input user
            beratKg  = d.getBeratKg();
            jumlah   = 0;
            subtotal = harga * beratKg;
        } else {
            // Layanan satuan / express -> pakai jumlah (pcs) dari input user
            beratKg  = 0;
            jumlah   = (int) d.getJumlah();
            subtotal = harga * jumlah;
        }

        String sql = "INSERT INTO detail_transaksi " +
                "(id_transaksi, id_layanan, berat_kg, jumlah, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, d.getIdTransaksi());
            ps.setInt(2, d.getIdLayanan());
            ps.setDouble(3, beratKg);
            ps.setInt(4, jumlah);
            ps.setDouble(5, subtotal);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error insert detail:");
            e.printStackTrace();
        }

        return false;
    }

    // ================= DELETE DETAIL =================

    public boolean delete(int idDetail) {

        String sql = "DELETE FROM detail_transaksi WHERE id_detail = ?";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idDetail);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error delete detail:");
            e.printStackTrace();
        }

        return false;
    }

    // ================= AMBIL DATA LAYANAN LENGKAP =================
    // [FIX] Sebelumnya hanya ambil harga (getHargaLayanan),
    //       sekarang ambil semua kolom agar bisa cek jenis layanan

    private Layanan getLayananById(int idLayanan) {

        String sql = "SELECT id_layanan, nama_layanan, jenis, harga, estimasi_hari " +
                "FROM layanan WHERE id_layanan = ?";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idLayanan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Layanan(
                    rs.getInt("id_layanan"),
                    rs.getString("nama_layanan"),
                    rs.getString("jenis"),
                    rs.getDouble("harga"),
                    rs.getInt("estimasi_hari")
                );
            }

        } catch (Exception e) {
            System.err.println("Error getLayananById:");
            e.printStackTrace();
        }

        return null;
    }
}