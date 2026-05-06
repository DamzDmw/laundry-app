package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.DetailTransaksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiDAO {

    public List<DetailTransaksi> getByTransaksi(int idTransaksi) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql =
            "SELECT dt.*, l.nama_layanan, l.jenis, l.harga AS harga_satuan " +
            "FROM detail_transaksi dt " +
            "JOIN layanan l ON dt.id_layanan = l.id_layanan " +
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
                d.setJumlah(rs.getDouble("jumlah"));
                d.setSubtotal(rs.getDouble("subtotal"));
                d.setNamaLayanan(rs.getString("nama_layanan"));
                d.setJenis(rs.getString("jenis"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println("Error getByTransaksi: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(DetailTransaksi d) {
        double harga    = getHargaLayanan(d.getIdLayanan());
        double subtotal = harga * d.getJumlah();
        String sql = "INSERT INTO detail_transaksi (id_transaksi, id_layanan, jumlah, subtotal) VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, d.getIdTransaksi());
            ps.setInt(2, d.getIdLayanan());
            ps.setDouble(3, d.getJumlah());
            ps.setDouble(4, subtotal);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error insert detail: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idDetail) {
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM detail_transaksi WHERE id_detail = ?");
            ps.setInt(1, idDetail);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error delete detail: " + e.getMessage());
            return false;
        }
    }

    private double getHargaLayanan(int idLayanan) {
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT harga FROM layanan WHERE id_layanan = ?");
            ps.setInt(1, idLayanan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("harga");
        } catch (Exception e) {
            System.err.println("Error getHarga: " + e.getMessage());
        }
        return 0;
    }
}