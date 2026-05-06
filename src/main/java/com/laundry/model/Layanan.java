package com.laundry.model;

/**
 * Model untuk tabel 'layanan'.
 */
public class Layanan {
    private int    idLayanan;
    private String namaLayanan;
    private String jenis;        // kiloan / satuan / express
    private double harga;
    private int    estimasiHari;

    public Layanan() {}

    public Layanan(int idLayanan, String namaLayanan, String jenis, double harga, int estimasiHari) {
        this.idLayanan    = idLayanan;
        this.namaLayanan  = namaLayanan;
        this.jenis        = jenis;
        this.harga        = harga;
        this.estimasiHari = estimasiHari;
    }

    // --- Getter & Setter ---
    public int    getIdLayanan()    { return idLayanan; }
    public String getNamaLayanan()  { return namaLayanan; }
    public String getJenis()        { return jenis; }
    public double getHarga()        { return harga; }
    public int    getEstimasiHari() { return estimasiHari; }

    public void setIdLayanan(int id)       { this.idLayanan    = id; }
    public void setNamaLayanan(String n)   { this.namaLayanan  = n; }
    public void setJenis(String j)         { this.jenis        = j; }
    public void setHarga(double h)         { this.harga        = h; }
    public void setEstimasiHari(int e)     { this.estimasiHari = e; }

    @Override
    public String toString() {
        return namaLayanan + " - Rp" + (int) harga + "/" + jenis;
    }
}
