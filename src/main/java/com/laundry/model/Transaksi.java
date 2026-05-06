package com.laundry.model;

/**
 * Model untuk tabel 'transaksi'.
 * Kolom namaPelanggan dan namaUser diambil dari JOIN query.
 */
public class Transaksi {
    private int    idTransaksi;
    private String kodeTransaksi;
    private int    idPelanggan;
    private int    idUser;
    private String tglMasuk;
    private String tglEstimasi;
    private String status;
    private String catatan;

    // Hasil JOIN (bukan kolom di tabel transaksi)
    private String namaPelanggan;
    private String namaUser;
    private String ringkasanLayanan; // Ringkasan layanan dari detail_transaksi

    public Transaksi() {}

    // --- Getter & Setter ---
    public int    getIdTransaksi()   { return idTransaksi; }
    public String getKodeTransaksi() { return kodeTransaksi; }
    public int    getIdPelanggan()   { return idPelanggan; }
    public int    getIdUser()        { return idUser; }
    public String getTglMasuk()      { return tglMasuk; }
    public String getTglEstimasi()   { return tglEstimasi; }
    public String getStatus()        { return status; }
    public String getCatatan()       { return catatan; }
    public String getNamaPelanggan()     { return namaPelanggan; }
    public String getNamaUser()          { return namaUser; }
    public String getRingkasanLayanan()  { return ringkasanLayanan; }

    public void setIdTransaksi(int id)      { this.idTransaksi   = id; }
    public void setKodeTransaksi(String k)  { this.kodeTransaksi = k; }
    public void setIdPelanggan(int id)      { this.idPelanggan   = id; }
    public void setIdUser(int id)           { this.idUser        = id; }
    public void setTglMasuk(String t)       { this.tglMasuk      = t; }
    public void setTglEstimasi(String t)    { this.tglEstimasi   = t; }
    public void setStatus(String s)         { this.status        = s; }
    public void setCatatan(String c)        { this.catatan       = c; }
    public void setNamaPelanggan(String n)   { this.namaPelanggan = n; }
    public void setNamaUser(String n)        { this.namaUser      = n; }
    public void setRingkasanLayanan(String r){ this.ringkasanLayanan = r; }
}
