package com.laundry.ui;

/**
 * Kelas pembantu untuk menyimpan warna dan style JavaFX secara terpusat.
 * Agar tampilan konsisten di semua halaman.
 */
public class StyleHelper {

    // ── Palet Warna ──────────────────────────────────────────────
    public static final String BIRU_TUA  = "#1a3a5c";   // sidebar
    public static final String BIRU      = "#1e6091";   // tombol utama
    public static final String HIJAU     = "#27ae60";   // sukses
    public static final String MERAH     = "#e74c3c";   // hapus / bahaya
    public static final String KUNING    = "#f39c12";   // peringatan
    public static final String BG        = "#f0f4f8";   // latar belakang
    public static final String TEXT_DARK = "#2c3e50";   // teks gelap

    // ── Style Tombol ─────────────────────────────────────────────
    public static String btnBiru() {
        return "-fx-background-color:" + BIRU + ";-fx-text-fill:white;" +
               "-fx-font-size:13px;-fx-padding:8 20;-fx-cursor:hand;" +
               "-fx-background-radius:6;-fx-font-weight:bold;";
    }

    public static String btnHijau() {
        return "-fx-background-color:" + HIJAU + ";-fx-text-fill:white;" +
               "-fx-font-size:13px;-fx-padding:8 20;-fx-cursor:hand;" +
               "-fx-background-radius:6;-fx-font-weight:bold;";
    }

    public static String btnMerah() {
        return "-fx-background-color:" + MERAH + ";-fx-text-fill:white;" +
               "-fx-font-size:13px;-fx-padding:8 20;-fx-cursor:hand;" +
               "-fx-background-radius:6;-fx-font-weight:bold;";
    }

    public static String btnKuning() {
        return "-fx-background-color:" + KUNING + ";-fx-text-fill:white;" +
               "-fx-font-size:13px;-fx-padding:8 20;-fx-cursor:hand;" +
               "-fx-background-radius:6;-fx-font-weight:bold;";
    }

    public static String btnUngu() {
    return "-fx-background-color:#8e44ad;-fx-text-fill:white;" +
           "-fx-font-size:13px;-fx-padding:8 20;-fx-cursor:hand;" +
           "-fx-background-radius:6;-fx-font-weight:bold;";
}

    public static String btnSidebar(boolean aktif) {
        String bg = aktif ? "#2980b9" : "transparent";
        return "-fx-background-color:" + bg + ";-fx-text-fill:white;" +
               "-fx-font-size:14px;-fx-padding:12 20;-fx-cursor:hand;" +
               "-fx-alignment:CENTER_LEFT;-fx-background-radius:8;-fx-min-width:200;";
    }

    // ── Style Komponen ────────────────────────────────────────────
    public static String card() {
        return "-fx-background-color:white;-fx-background-radius:10;" +
               "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);-fx-padding:20;";
    }

    public static String field() {
        return "-fx-background-color:white;-fx-border-color:#dce1e7;" +
               "-fx-border-radius:6;-fx-background-radius:6;-fx-padding:8;-fx-font-size:13px;";
    }

    public static String judulHalaman() {
        return "-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + TEXT_DARK + ";";
    }

    public static String subJudul() {
        return "-fx-font-size:13px;-fx-text-fill:#7f8c8d;";
    }
}
