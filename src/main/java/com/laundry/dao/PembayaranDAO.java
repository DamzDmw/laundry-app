package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.Pembayaran;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel 'pembayaran'.
 */
public class PembayaranDAO {

    public List<Pembayaran> getAll() {
        List<Pembayaran> list = new ArrayList<>();
        String sql =
            "SELECT pb.*, t.kode_transaksi, p.nama AS nama_pelanggan " +
            "FROM pembayaran pb " +
            "JOIN transaksi t ON pb.id_transaksi = t.id_transaksi " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
            "ORDER BY pb.tgl_bayar DESC";
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) {
                Pembayaran pb = new Pembayaran();
                pb.setIdPembayaran(rs.getInt("id_pembayaran"));
                pb.setIdTransaksi(rs.getInt("id_transaksi"));
                pb.setMetode(rs.getString("metode"));
                pb.setTotalBayar(rs.getDouble("total_bayar"));
                pb.setDiskon(rs.getDouble("diskon"));
                pb.setTglBayar(rs.getString("tgl_bayar"));
                pb.setKodeTransaksi(rs.getString("kode_transaksi"));
                pb.setNamaPelanggan(rs.getString("nama_pelanggan"));
                list.add(pb);
            }
        } catch (Exception e) {
            System.err.println("Error getAll pembayaran: " + e.getMessage());
        }
        return list;
    }

    /** Menyimpan pembayaran baru. */
    public boolean insert(Pembayaran pb) {
        String sql = "INSERT INTO pembayaran (id_transaksi, metode, total_bayar, diskon) VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, pb.getIdTransaksi());
            ps.setString(2, pb.getMetode());
            ps.setDouble(3, pb.getTotalBayar());
            ps.setDouble(4, pb.getDiskon());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error insert pembayaran: " + e.getMessage());
            return false;
        }
    }

    /** Cek apakah transaksi sudah dibayar. */
    public boolean sudahDibayar(int idTransaksi) {
        String sql = "SELECT COUNT(*) FROM pembayaran WHERE id_transaksi=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            System.err.println("Error cek pembayaran: " + e.getMessage());
        }
        return false;
    }
}
