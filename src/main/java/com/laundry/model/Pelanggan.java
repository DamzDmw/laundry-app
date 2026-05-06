package com.laundry.model;

/**
 * Model untuk tabel 'pelanggan'.
 */
public class Pelanggan {
    private int    idPelanggan;
    private String nama;
    private String noHp;
    private String alamat;

    public Pelanggan() {}

    public Pelanggan(int idPelanggan, String nama, String noHp, String alamat) {
        this.idPelanggan = idPelanggan;
        this.nama        = nama;
        this.noHp        = noHp;
        this.alamat      = alamat;
    }

    // --- Getter & Setter ---
    public int    getIdPelanggan() { return idPelanggan; }
    public String getNama()        { return nama; }
    public String getNoHp()        { return noHp; }
    public String getAlamat()      { return alamat; }

    public void setIdPelanggan(int id)  { this.idPelanggan = id; }
    public void setNama(String nama)    { this.nama        = nama; }
    public void setNoHp(String noHp)   { this.noHp        = noHp; }
    public void setAlamat(String a)     { this.alamat      = a; }

    // Ditampilkan di ComboBox
    @Override
    public String toString() {
        return nama + " (" + noHp + ")";
    }
}
