package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.Layanan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel 'layanan'.
 */
public class LayananDAO {

    public List<Layanan> getAll() {
        List<Layanan> list = new ArrayList<>();
        String sql = "SELECT * FROM layanan ORDER BY nama_layanan";
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Layanan(
                    rs.getInt("id_layanan"),
                    rs.getString("nama_layanan"),
                    rs.getString("jenis"),
                    rs.getDouble("harga"),
                    rs.getInt("estimasi_hari")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getAll layanan: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Layanan l) {
        String sql = "INSERT INTO layanan (nama_layanan, jenis, harga, estimasi_hari) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, l.getNamaLayanan());
            ps.setString(2, l.getJenis());
            ps.setDouble(3, l.getHarga());
            ps.setInt(4, l.getEstimasiHari());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error insert layanan: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Layanan l) {
        String sql = "UPDATE layanan SET nama_layanan=?, jenis=?, harga=?, estimasi_hari=? WHERE id_layanan=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, l.getNamaLayanan());
            ps.setString(2, l.getJenis());
            ps.setDouble(3, l.getHarga());
            ps.setInt(4, l.getEstimasiHari());
            ps.setInt(5, l.getIdLayanan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error update layanan: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idLayanan) {
        String sql = "DELETE FROM layanan WHERE id_layanan=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idLayanan);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error delete layanan: " + e.getMessage());
            return false;
        }
    }
}
