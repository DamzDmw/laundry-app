package com.laundry.model;

/**
 * Model untuk tabel 'users'.
 * Menyimpan data pengguna yang bisa login ke aplikasi.
 */
public class User {
    private int    idUser;
    private String username;
    private String nama;
    private String role; // 'admin' atau 'petugas'

    public User() {}

    public User(int idUser, String username, String nama, String role) {
        this.idUser   = idUser;
        this.username = username;
        this.nama     = nama;
        this.role     = role;
    }

    // --- Getter & Setter ---
    public int    getIdUser()   { return idUser; }
    public String getUsername() { return username; }
    public String getNama()     { return nama; }
    public String getRole()     { return role; }

    public void setIdUser(int idUser)      { this.idUser   = idUser; }
    public void setUsername(String u)      { this.username = u; }
    public void setNama(String nama)       { this.nama     = nama; }
    public void setRole(String role)       { this.role     = role; }
}
