package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.Pelanggan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel 'pelanggan'.
 * Mendukung operasi: ambil semua, tambah, edit, hapus.
 */
public class PelangganDAO {

    /** Mengambil semua data pelanggan dari database. */
    public List<Pelanggan> getAll() {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan ORDER BY nama";
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Pelanggan(
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama"),
                    rs.getString("no_hp"),
                    rs.getString("alamat")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getAll pelanggan: " + e.getMessage());
        }
        return list;
    }

    /** Menambah pelanggan baru. */
    public boolean insert(Pelanggan p) {
        String sql = "INSERT INTO pelanggan (nama, no_hp, alamat) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getAlamat());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error insert pelanggan: " + e.getMessage());
            return false;
        }
    }

    /** Mengubah data pelanggan. */
    public boolean update(Pelanggan p) {
        String sql = "UPDATE pelanggan SET nama=?, no_hp=?, alamat=? WHERE id_pelanggan=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getAlamat());
            ps.setInt(4, p.getIdPelanggan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error update pelanggan: " + e.getMessage());
            return false;
        }
    }

    /** Menghapus pelanggan berdasarkan ID. */
    public boolean delete(int idPelanggan) {
        String sql = "DELETE FROM pelanggan WHERE id_pelanggan=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error delete pelanggan: " + e.getMessage());
            return false;
        }
    }
}
