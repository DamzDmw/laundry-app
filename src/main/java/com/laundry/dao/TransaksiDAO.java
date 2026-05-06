package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.Transaksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel 'transaksi'.
 * Query JOIN dengan pelanggan dan users untuk tampil lengkap di tabel.
 */
public class TransaksiDAO {

    private static final String SELECT_ALL =
        "SELECT t.*, p.nama AS nama_pelanggan, u.nama AS nama_user, " +
        "GROUP_CONCAT(l.nama_layanan ORDER BY l.nama_layanan SEPARATOR ', ') AS ringkasan_layanan " +
        "FROM transaksi t " +
        "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
        "LEFT JOIN users u ON t.id_user = u.id_user " +
        "LEFT JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi " +
        "LEFT JOIN layanan l ON dt.id_layanan = l.id_layanan " +
        "GROUP BY t.id_transaksi, p.nama, u.nama " +
        "ORDER BY t.created_at DESC";

    /** Ambil semua transaksi beserta nama pelanggan dan nama petugas. */
    public List<Transaksi> getAll() {
        List<Transaksi> list = new ArrayList<>();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(SELECT_ALL);
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setKodeTransaksi(rs.getString("kode_transaksi"));
                t.setIdPelanggan(rs.getInt("id_pelanggan"));
                t.setIdUser(rs.getInt("id_user"));
                t.setTglMasuk(rs.getString("tgl_masuk"));
                t.setTglEstimasi(rs.getString("tgl_estimasi"));
                t.setStatus(rs.getString("status"));
                t.setCatatan(rs.getString("catatan"));
                t.setNamaPelanggan(rs.getString("nama_pelanggan"));
                t.setNamaUser(rs.getString("nama_user"));
                t.setRingkasanLayanan(rs.getString("ringkasan_layanan"));
                list.add(t);
            }
        } catch (Exception e) {
            System.err.println("Error getAll transaksi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Membuat kode transaksi otomatis: LDR-XXX
     */
    private String generateKode() {
        String sql = "SELECT COUNT(*) FROM transaksi";
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            if (rs.next()) {
                int no = rs.getInt(1) + 1;
                return String.format("LDR-%03d", no);
            }
        } catch (Exception e) {
            System.err.println("Error generate kode: " + e.getMessage());
        }
        return "LDR-001";
    }

    /** Menambah transaksi baru dan mengembalikan id yang digenerate. */
    public int insertGetId(Transaksi t) {
        String kode = generateKode();
        String sql  = "INSERT INTO transaksi (kode_transaksi, id_pelanggan, id_user, " +
                      "tgl_masuk, tgl_estimasi, status, catatan) VALUES (?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, kode);
            ps.setInt(2, t.getIdPelanggan());
            ps.setInt(3, t.getIdUser());
            ps.setString(4, t.getTglMasuk());
            ps.setString(5, t.getTglEstimasi());
            ps.setString(6, "diterima");
            ps.setString(7, t.getCatatan());
            if (ps.executeUpdate() > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error insertGetId transaksi: " + e.getMessage());
        }
        return -1;
    }

    /** Menambah transaksi baru dengan kode otomatis. */
    public boolean insert(Transaksi t) {
        String kode = generateKode();
        String sql  = "INSERT INTO transaksi (kode_transaksi, id_pelanggan, id_user, " +
                      "tgl_masuk, tgl_estimasi, status, catatan) VALUES (?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, kode);
            ps.setInt(2, t.getIdPelanggan());
            ps.setInt(3, t.getIdUser());
            ps.setString(4, t.getTglMasuk());
            ps.setString(5, t.getTglEstimasi());
            ps.setString(6, "diterima");
            ps.setString(7, t.getCatatan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error insert transaksi: " + e.getMessage());
            return false;
        }
    }

    /** Mengubah status transaksi (diterima / diproses / selesai / diambil). */
    public boolean updateStatus(int idTransaksi, String status) {
        String sql = "UPDATE transaksi SET status=? WHERE id_transaksi=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, idTransaksi);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updateStatus: " + e.getMessage());
            return false;
        }
    }

    /** Menghapus transaksi (dan detail-nya karena ON DELETE CASCADE). */
    public boolean delete(int idTransaksi) {
        String sql = "DELETE FROM transaksi WHERE id_transaksi=?";
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, idTransaksi);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error delete transaksi: " + e.getMessage());
            return false;
        }
    }
}
