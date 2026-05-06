<?php
// ── Koneksi ───────────────────────────────────────────────────────────────────
$pdo = new PDO("mysql:host=localhost;dbname=sistem_laundry;charset=utf8", "root", "");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// ── Filter ────────────────────────────────────────────────────────────────────
$dari   = $_GET['dari']   ?? date('Y-m-01');
$sampai = $_GET['sampai'] ?? date('Y-m-d');

// ── Query Transaksi ───────────────────────────────────────────────────────────
$stmt = $pdo->prepare("
    SELECT 
        t.id_transaksi,
        t.kode_transaksi,
        p.nama          AS pelanggan,
        t.tgl_masuk,
        t.status,
        COALESCE(pb.total_bayar, 0) AS bayar,
        COALESCE(pb.metode, '-')    AS metode,
        COALESCE(pb.diskon, 0)      AS diskon
    FROM transaksi t
    LEFT JOIN pelanggan  p  ON t.id_pelanggan = p.id_pelanggan
    LEFT JOIN pembayaran pb ON t.id_transaksi = pb.id_transaksi
    WHERE t.tgl_masuk BETWEEN :dari AND :sampai
    ORDER BY t.tgl_masuk DESC
");
$stmt->execute([':dari' => $dari, ':sampai' => $sampai]);
$transaksi = $stmt->fetchAll(PDO::FETCH_ASSOC);

// ── Detail Layanan ─────────────────────────────────────────────────────────────
// Diambil sekaligus lalu dikelompokkan di PHP (lebih efisien dari query di loop)
$detailMap = [];
if (!empty($transaksi)) {
  $ids         = array_column($transaksi, 'id_transaksi');
  $placeholder = implode(',', array_fill(0, count($ids), '?'));
  $stDetail    = $pdo->prepare("
        SELECT dt.id_transaksi, l.nama_layanan, dt.jumlah, dt.subtotal
        FROM detail_transaksi dt
        JOIN layanan l ON dt.id_layanan = l.id_layanan
        WHERE dt.id_transaksi IN ($placeholder)
        ORDER BY dt.id_transaksi, l.nama_layanan
    ");
  $stDetail->execute($ids);
  foreach ($stDetail->fetchAll(PDO::FETCH_ASSOC) as $row) {
    $detailMap[$row['id_transaksi']][] = $row;
  }
}

// ── Statistik ─────────────────────────────────────────────────────────────────
$stStat = $pdo->prepare("
    SELECT 
        COUNT(*)                         AS total,
        SUM(status = 'diproses')         AS diproses,
        SUM(status = 'selesai')          AS selesai,
        COALESCE(SUM(pb.total_bayar), 0) AS pendapatan
    FROM transaksi t
    LEFT JOIN pembayaran pb ON t.id_transaksi = pb.id_transaksi
    WHERE t.tgl_masuk BETWEEN :dari AND :sampai
");
$stStat->execute([':dari' => $dari, ':sampai' => $sampai]);
$stat = $stStat->fetch(PDO::FETCH_ASSOC);

// ── Helper ─────────────────────────────────────────────────────────────────────
function rupiah($n)
{
  return 'Rp ' . number_format($n, 0, ',', '.');
}

// Badge status → warna minimalis
$badgeColor = [
  'diterima' => '#3b82f6',
  'diproses' => '#f59e0b',
  'selesai'  => '#10b981',
  'diambil'  => '#8b5cf6',
];
?>
<!DOCTYPE html>
<html lang="id">

<head>
  <meta charset="UTF-8">
  <title>Laporan Laundry</title>
  <style>
    /* ── Reset ── */
    *,
    *::before,
    *::after {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    /* ── Token warna ── */
    :root {
      --bg: #f8f9fb;
      --surface: #ffffff;
      --border: #e5e7eb;
      --text: #1f2937;
      --muted: #6b7280;
      --accent: #2563eb;
    }

    body {
      font-family: 'Segoe UI', system-ui, sans-serif;
      background: var(--bg);
      color: var(--text);
      font-size: 14px;
    }

    /* ── Header tipis ── */
    .header {
      background: var(--surface);
      border-bottom: 1px solid var(--border);
      padding: 14px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .header-left h1 {
      font-size: 16px;
      font-weight: 700;
    }

    .header-left p {
      font-size: 12px;
      color: var(--muted);
      margin-top: 2px;
    }

    .btn-print {
      background: var(--accent);
      color: #fff;
      border: none;
      padding: 7px 16px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 13px;
      text-decoration: none;
    }

    /* ── Container ── */
    .wrap {
      max-width: 960px;
      margin: auto;
      padding: 20px 16px;
    }

    /* ── Filter ── */
    .filter {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 8px;
      padding: 14px 16px;
      display: flex;
      gap: 10px;
      align-items: flex-end;
      flex-wrap: wrap;
      margin-bottom: 16px;
    }

    .filter label {
      display: block;
      font-size: 11px;
      color: var(--muted);
      margin-bottom: 4px;
    }

    .filter input[type=date] {
      border: 1px solid var(--border);
      border-radius: 6px;
      padding: 6px 10px;
      font-size: 13px;
      color: var(--text);
    }

    .btn-terapkan {
      background: var(--accent);
      color: #fff;
      border: none;
      padding: 7px 16px;
      border-radius: 6px;
      font-size: 13px;
      cursor: pointer;
    }

    .btn-reset {
      background: none;
      border: 1px solid var(--border);
      padding: 7px 14px;
      border-radius: 6px;
      font-size: 13px;
      cursor: pointer;
      color: var(--muted);
      text-decoration: none;
    }

    /* ── Stat bar (3 angka saja, ringan) ── */
    .stat-bar {
      display: flex;
      gap: 10px;
      margin-bottom: 16px;
      flex-wrap: wrap;
    }

    .stat-item {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 8px;
      padding: 12px 18px;
      flex: 1;
      min-width: 130px;
    }

    .stat-item .s-label {
      font-size: 11px;
      color: var(--muted);
    }

    .stat-item .s-val {
      font-size: 22px;
      font-weight: 700;
      margin-top: 2px;
    }

    .stat-item.green .s-val {
      color: #10b981;
    }

    .stat-item.blue .s-val {
      color: var(--accent);
    }

    /* ── Tabel ── */
    .table-wrap {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 8px;
      overflow: hidden;
    }

    .table-head {
      padding: 12px 16px;
      border-bottom: 1px solid var(--border);
      font-size: 13px;
      font-weight: 600;
      color: var(--muted);
    }

    table {
      width: 100%;
      border-collapse: collapse;
      font-size: 13px;
    }

    thead th {
      background: #f3f4f6;
      padding: 9px 14px;
      text-align: left;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: .04em;
      color: var(--muted);
      border-bottom: 1px solid var(--border);
    }

    tbody tr {
      border-bottom: 1px solid var(--border);
    }

    tbody tr:last-child {
      border-bottom: none;
    }

    tbody tr:hover {
      background: #fafafa;
    }

    td {
      padding: 10px 14px;
      vertical-align: top;
    }

    /* ── Badge status ── */
    .badge {
      display: inline-block;
      padding: 2px 9px;
      border-radius: 99px;
      font-size: 11px;
      font-weight: 600;
      color: #fff;
      white-space: nowrap;
    }

    /* ── Detail layanan inline ── */
    /*
      Detail layanan ditampilkan langsung (tanpa toggle)
      dalam bentuk teks ringkas, bukan tabel lagi.
      Ini yang paling banyak mengurangi "ramai" di tampilan.
    */
    .layanan-list {
      margin-top: 2px;
    }

    .layanan-item {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: var(--muted);
      gap: 8px;
    }

    .layanan-item+.layanan-item {
      margin-top: 2px;
    }

    .layanan-name {
      flex: 1;
    }

    .layanan-sub {
      font-weight: 600;
      color: var(--text);
    }

    /* ── Bayar ── */
    .bayar-amount {
      font-weight: 700;
    }

    .bayar-meta {
      font-size: 11px;
      color: var(--muted);
      margin-top: 1px;
    }

    .belum-bayar {
      font-size: 12px;
      color: #ef4444;
    }

    .kosong {
      text-align: center;
      padding: 40px;
      color: var(--muted);
    }

    .footer {
      text-align: center;
      font-size: 11px;
      color: #c4c4c4;
      margin-top: 20px;
    }

    /* ── Print ── */
    @media print {
      .no-print {
        display: none !important;
      }

      body,
      .wrap {
        background: #fff;
      }

      .table-wrap,
      .stat-item {
        border: 1px solid #ddd;
        box-shadow: none;
      }

      .header {
        border-bottom: 1px solid #ddd;
      }

      /* Paksa warna badge tercetak */
      .badge {
        -webkit-print-color-adjust: exact;
        print-color-adjust: exact;
      }
    }
  </style>
</head>

<body>

  <!-- Header -->
  <div class="header">
    <div class="header-left">
      <h1>Laporan Laundry</h1>
      <p>Periode <?= $dari ?> — <?= $sampai ?> &nbsp;·&nbsp; Dicetak <?= date('d/m/Y H:i') ?></p>
    </div>
    <a href="javascript:window.print()" class="btn-print no-print">Cetak / PDF</a>
  </div>

  <div class="wrap">

    <!-- Filter -->
    <form method="GET" class="filter no-print">
      <div>
        <label>Dari</label>
        <input type="date" name="dari" value="<?= htmlspecialchars($dari) ?>">
      </div>
      <div>
        <label>Sampai</label>
        <input type="date" name="sampai" value="<?= htmlspecialchars($sampai) ?>">
      </div>
      <button class="btn-terapkan">Tampilkan</button>
      <a href="laporan.php" class="btn-reset">Reset</a>
    </form>

    <!-- Statistik: hanya 3 angka penting -->
    <div class="stat-bar">
      <div class="stat-item blue">
        <div class="s-label">Total Transaksi</div>
        <div class="s-val"><?= $stat['total'] ?></div>
      </div>
      <div class="stat-item">
        <div class="s-label">Sedang Diproses</div>
        <div class="s-val"><?= $stat['diproses'] ?></div>
      </div>
      <div class="stat-item">
        <div class="s-label">Selesai</div>
        <div class="s-val"><?= $stat['selesai'] ?></div>
      </div>
      <div class="stat-item green">
        <div class="s-label">Total Pendapatan</div>
        <div class="s-val" style="font-size:16px;margin-top:5px"><?= rupiah($stat['pendapatan']) ?></div>
      </div>
    </div>

    <!-- Tabel Transaksi -->
    <div class="table-wrap">
      <div class="table-head">
        Detail Transaksi &nbsp;<span style="font-weight:400">(<?= count($transaksi) ?> data)</span>
      </div>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Kode</th>
            <th>Pelanggan</th>
            <th>Tgl Masuk</th>
            <th>Status</th>
            <th>Layanan</th>
            <th>Pembayaran</th>
          </tr>
        </thead>
        <tbody>
          <?php if (empty($transaksi)): ?>
            <tr>
              <td class="kosong" colspan="7">Tidak ada data untuk periode ini.</td>
            </tr>
          <?php else: ?>
            <?php foreach ($transaksi as $i => $t):
              $details = $detailMap[$t['id_transaksi']] ?? [];
              $color   = $badgeColor[$t['status']] ?? '#9ca3af';
            ?>
              <tr>
                <td style="color:var(--muted)"><?= $i + 1 ?></td>
                <td><strong><?= htmlspecialchars($t['kode_transaksi']) ?></strong></td>
                <td><?= htmlspecialchars($t['pelanggan'] ?? '-') ?></td>
                <td style="white-space:nowrap"><?= $t['tgl_masuk'] ?></td>
                <td>
                  <span class="badge" style="background:<?= $color ?>">
                    <?= $t['status'] ?>
                  </span>
                </td>

                <!-- Layanan: ringkas tanpa tabel nested, tanpa toggle -->
                <td>
                  <?php if (empty($details)): ?>
                    <span style="color:var(--muted);font-size:12px">—</span>
                  <?php else: ?>
                    <div class="layanan-list">
                      <?php foreach ($details as $d): ?>
                        <div class="layanan-item">
                          <span class="layanan-name">
                            <?= htmlspecialchars($d['nama_layanan']) ?>
                            <span style="color:#d1d5db">×<?= $d['jumlah'] ?></span>
                          </span>
                          <span class="layanan-sub"><?= rupiah($d['subtotal']) ?></span>
                        </div>
                      <?php endforeach; ?>
                    </div>
                  <?php endif; ?>
                </td>

                <!-- Pembayaran -->
                <td>
                  <?php if ($t['bayar'] > 0): ?>
                    <div class="bayar-amount"><?= rupiah($t['bayar']) ?></div>
                    <div class="bayar-meta">
                      <?= $t['metode'] ?>
                      <?php if ($t['diskon'] > 0): ?>
                        &nbsp;· Diskon <?= rupiah($t['diskon']) ?>
                      <?php endif; ?>
                    </div>
                  <?php else: ?>
                    <span class="belum-bayar">Belum bayar</span>
                  <?php endif; ?>
                </td>
              </tr>
            <?php endforeach; ?>
          <?php endif; ?>
        </tbody>
      </table>
    </div>

    <p class="footer">Sistem Laundry &nbsp;·&nbsp; <?= date('Y') ?></p>
  </div>

</body>

</html>