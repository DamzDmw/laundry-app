package com.laundry.model;

public class DetailTransaksi {

    private int idDetail;
    private int idTransaksi;
    private int idLayanan;

    // ================= FIX =================

    private double beratKg;

    private double jumlah;

    private double subtotal;

    // Dari JOIN layanan — untuk tampilan di tabel

    private String namaLayanan;

    private String jenis;

    private double hargaSatuan;

    public DetailTransaksi() {
    }

    // ================= GETTER =================

    public int getIdDetail() {
        return idDetail;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public int getIdLayanan() {
        return idLayanan;
    }

    // ================= FIX =================

    public double getBeratKg() {
        return beratKg;
    }

    public double getJumlah() {
        return jumlah;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getNamaLayanan() {
        return namaLayanan;
    }

    public String getJenis() {
        return jenis;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    // ================= SETTER =================

    public void setIdDetail(int v) {
        this.idDetail = v;
    }

    public void setIdTransaksi(int v) {
        this.idTransaksi = v;
    }

    public void setIdLayanan(int v) {
        this.idLayanan = v;
    }

    // ================= FIX =================

    public void setBeratKg(double v) {
        this.beratKg = v;
    }

    public void setJumlah(double v) {
        this.jumlah = v;
    }

    public void setSubtotal(double v) {
        this.subtotal = v;
    }

    public void setNamaLayanan(String v) {
        this.namaLayanan = v;
    }

    public void setJenis(String v) {
        this.jenis = v;
    }

    public void setHargaSatuan(double v) {
        this.hargaSatuan = v;
    }
}