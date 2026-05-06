package com.laundry.model;

/**
 * Model untuk tabel 'pembayaran'.
 */
public class Pembayaran {
    private int    idPembayaran;
    private int    idTransaksi;
    private String metode;       // tunai / transfer / qris
    private double totalBayar;
    private double diskon;
    private String tglBayar;

    // Hasil JOIN
    private String kodeTransaksi;
    private String namaPelanggan;

    public Pembayaran() {}

    // --- Getter & Setter ---
    public int    getIdPembayaran()  { return idPembayaran; }
    public int    getIdTransaksi()   { return idTransaksi; }
    public String getMetode()        { return metode; }
    public double getTotalBayar()    { return totalBayar; }
    public double getDiskon()        { return diskon; }
    public String getTglBayar()      { return tglBayar; }
    public String getKodeTransaksi() { return kodeTransaksi; }
    public String getNamaPelanggan() { return namaPelanggan; }

    public void setIdPembayaran(int id)    { this.idPembayaran  = id; }
    public void setIdTransaksi(int id)     { this.idTransaksi   = id; }
    public void setMetode(String m)        { this.metode        = m; }
    public void setTotalBayar(double t)    { this.totalBayar    = t; }
    public void setDiskon(double d)        { this.diskon        = d; }
    public void setTglBayar(String t)      { this.tglBayar      = t; }
    public void setKodeTransaksi(String k) { this.kodeTransaksi = k; }
    public void setNamaPelanggan(String n) { this.namaPelanggan = n; }
}
