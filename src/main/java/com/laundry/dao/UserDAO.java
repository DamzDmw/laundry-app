package com.laundry.dao;

import com.laundry.db.DBConnection;
import com.laundry.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO (Data Access Object) untuk tabel 'users'.
 * Berisi operasi database yang berhubungan dengan User.
 */
public class UserDAO {

    /**
     * Memvalidasi login berdasarkan username dan password.
     * @return objek User jika berhasil, null jika gagal
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("nama"),
                    rs.getString("role")
                );
            }
        } catch (Exception e) {
            System.err.println("Error login: " + e.getMessage());
        }
        return null;
    }
}
