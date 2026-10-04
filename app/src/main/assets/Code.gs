/**
 * ============================================================================
 * APLIKASI ABSENSI SISWA MODERN - GOOGLE APPS SCRIPT (Code.gs)
 * Database: Google Spreadsheet (Otomatis Generate Tabel & 2 Data Dummy/Tabel)
 * ============================================================================
 */

const DB_SCHEMA = {
  Users: {
    headers: ['ID', 'Username', 'Password', 'Nama Lengkap', 'Role'],
    dummy: [
      ['USR-01', 'admin', 'admin123', 'Drs. Hendra Wijaya', 'Admin'],
      ['USR-02', 'guru', 'guru123', 'Rina Kartika, S.Pd', 'Guru']
    ]
  },
  Kelas: {
    headers: ['ID', 'Kode Kelas', 'Nama Kelas', 'Wali Kelas', 'Tahun Ajaran'],
    dummy: [
      ['KLS-01', 'X-IPA-1', 'Kelas X IPA 1', 'Rina Kartika, S.Pd', '2026/2027'],
      ['KLS-02', 'X-IPS-1', 'Kelas X IPS 1', 'Bambang Sutrisno, M.Pd', '2026/2027']
    ]
  },
  Siswa: {
    headers: ['ID', 'NIS', 'Nama Siswa', 'Jenis Kelamin', 'Nama Kelas', 'Alamat'],
    dummy: [
      ['SIS-01', '2026001', 'Ahmad Fauzi', 'Laki-laki', 'Kelas X IPA 1', 'Jl. Merdeka No. 12, Jakarta'],
      ['SIS-02', '2026002', 'Siti Nurhaliza', 'Perempuan', 'Kelas X IPS 1', 'Jl. Sudirman No. 45, Jakarta']
    ]
  },
  Kelompok: {
    headers: ['ID', 'Nama Kelompok', 'Nama Kelas', 'Nama Siswa', 'Peran Kelompok', 'Topik Tugas'],
    dummy: [
      ['KLP-01', 'Kelompok 1 - Sains Terpadu', 'Kelas X IPA 1', 'Ahmad Fauzi', 'Ketua Kelompok', 'Praktikum Biologi Sel & Jaringan'],
      ['KLP-02', 'Kelompok 2 - Ekonomi Nusantara', 'Kelas X IPS 1', 'Siti Nurhaliza', 'Ketua Kelompok', 'Analisis Pasar Modal Indonesia']
    ]
  },
  Absensi: {
    headers: ['ID', 'Tanggal', 'Nama Kelas', 'NIS', 'Nama Siswa', 'Status', 'Keterangan'],
    dummy: [
      ['ABS-01', '2026-10-04', 'Kelas X IPA 1', '2026001', 'Ahmad Fauzi', 'Hadir', 'Hadir tepat waktu'],
      ['ABS-02', '2026-10-04', 'Kelas X IPS 1', '2026002', 'Siti Nurhaliza', 'Izin', 'Izin mengikuti olimpiade tingkat provinsi']
    ]
  }
};

function doGet(e) {
  initDatabase();
  return HtmlService.createTemplateFromFile('Index')
    .evaluate()
    .setTitle('Sistem Informasi Absensi Siswa')
    .setXFrameOptionsMode(HtmlService.XFrameOptionsMode.ALLOWALL)
    .addMetaTag('viewport', 'width=device-width, initial-scale=1, maximum-scale=1');
}

function initDatabase() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheetNames = Object.keys(DB_SCHEMA);

  sheetNames.forEach(function(sheetName) {
    let sheet = ss.getSheetByName(sheetName);
    const config = DB_SCHEMA[sheetName];

    if (!sheet) {
      sheet = ss.insertSheet(sheetName);
    }

    if (sheet.getLastRow() === 0) {
      sheet.appendRow(config.headers);
      const headerRange = sheet.getRange(1, 1, 1, config.headers.length);
      headerRange.setBackground('#0F4C81')
                 .setFontColor('#FFFFFF')
                 .setFontWeight('bold');
      sheet.setFrozenRows(1);

      config.dummy.forEach(function(row) {
        sheet.appendRow(row);
      });

      sheet.autoResizeColumns(1, config.headers.length);
    }
  });

  return { status: 'OK', message: 'Database & 2 data dummy per tabel berhasil disiapkan.' };
}

function resetDatabaseToDummy() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  Object.keys(DB_SCHEMA).forEach(function(sheetName) {
    let sheet = ss.getSheetByName(sheetName);
    if (!sheet) {
      sheet = ss.insertSheet(sheetName);
    } else {
      sheet.clearContents();
    }
    const config = DB_SCHEMA[sheetName];
    sheet.appendRow(config.headers);
    const headerRange = sheet.getRange(1, 1, 1, config.headers.length);
    headerRange.setBackground('#0F4C81')
               .setFontColor('#FFFFFF')
               .setFontWeight('bold');
    sheet.setFrozenRows(1);
    config.dummy.forEach(function(row) {
      sheet.appendRow(row);
    });
  });
  return getAllData();
}

function loginUser(username, password) {
  initDatabase();
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheet = ss.getSheetByName('Users');
  const data = sheet.getDataRange().getValues();

  const cleanUser = String(username || '').trim();
  const cleanPass = String(password || '').trim();

  for (let i = 1; i < data.length; i++) {
    const row = data[i];
    const dbUser = String(row[1] || '').trim();
    const dbPass = String(row[2] || '').trim();

    if (dbUser === cleanUser && dbPass === cleanPass) {
      return {
        success: true,
        user: {
          id: String(row[0]),
          username: dbUser,
          password: dbPass,
          namaLengkap: String(row[3] || ''),
          role: String(row[4] || 'Admin')
        }
      };
    }
  }

  return {
    success: false,
    message: 'Username atau password tidak sesuai!'
  };
}

function formatCell(val) {
  if (val instanceof Date) {
    return Utilities.formatDate(val, Session.getScriptTimeZone() || 'Asia/Jakarta', 'yyyy-MM-dd');
  }
  return val !== null && val !== undefined ? String(val) : '';
}

function getAllData() {
  initDatabase();
  const ss = SpreadsheetApp.getActiveSpreadsheet();

  function readTable(sheetName, mapFn) {
    const sheet = ss.getSheetByName(sheetName);
    if (!sheet || sheet.getLastRow() <= 1) return [];
    const values = sheet.getDataRange().getValues();
    const result = [];
    for (let i = 1; i < values.length; i++) {
      const row = values[i].map(formatCell);
      if (row[0]) {
        result.push(mapFn(row));
      }
    }
    return result;
  }

  return {
    users: readTable('Users', function(r) {
      return { id: r[0], username: r[1], password: r[2], namaLengkap: r[3], role: r[4] };
    }),
    kelas: readTable('Kelas', function(r) {
      return { id: r[0], kodeKelas: r[1], namaKelas: r[2], waliKelas: r[3], tahunAjaran: r[4] };
    }),
    siswa: readTable('Siswa', function(r) {
      return { id: r[0], nis: r[1], namaSiswa: r[2], jenisKelamin: r[3], namaKelas: r[4], alamat: r[5] };
    }),
    kelompok: readTable('Kelompok', function(r) {
      return { id: r[0], namaKelompok: r[1], namaKelas: r[2], namaSiswa: r[3], peranKelompok: r[4], topikTugas: r[5] };
    }),
    absensi: readTable('Absensi', function(r) {
      return { id: r[0], tanggal: r[1], namaKelas: r[2], nis: r[3], namaSiswa: r[4], status: r[5], keterangan: r[6] };
    })
  };
}

function saveRecord(tableName, payload) {
  initDatabase();
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheet = ss.getSheetByName(tableName);
  if (!sheet) throw new Error('Tabel tidak ditemukan: ' + tableName);

  const prefixMap = { Users: 'USR', Kelas: 'KLS', Siswa: 'SIS', Kelompok: 'KLP', Absensi: 'ABS' };
  let id = payload.id ? String(payload.id).trim() : '';
  const isNew = !id;
  if (isNew) {
    const prefix = prefixMap[tableName] || 'ID';
    id = prefix + '-' + new Date().getTime().toString().slice(-5);
  }

  let rowValues = [];
  if (tableName === 'Users') {
    rowValues = [id, payload.username || '', payload.password || '', payload.namaLengkap || '', payload.role || 'Guru'];
  } else if (tableName === 'Kelas') {
    rowValues = [id, payload.kodeKelas || '', payload.namaKelas || '', payload.waliKelas || '', payload.tahunAjaran || '2026/2027'];
  } else if (tableName === 'Siswa') {
    rowValues = [id, payload.nis || '', payload.namaSiswa || '', payload.jenisKelamin || 'Laki-laki', payload.namaKelas || '', payload.alamat || ''];
  } else if (tableName === 'Kelompok') {
    rowValues = [id, payload.namaKelompok || '', payload.namaKelas || '', payload.namaSiswa || '', payload.peranKelompok || 'Anggota', payload.topikTugas || ''];
  } else if (tableName === 'Absensi') {
    rowValues = [id, "'" + (payload.tanggal || '2026-10-04'), payload.namaKelas || '', payload.nis || '', payload.namaSiswa || '', payload.status || 'Hadir', payload.keterangan || ''];
  }

  if (!isNew) {
    const data = sheet.getDataRange().getValues();
    for (let i = 1; i < data.length; i++) {
      if (String(data[i][0]).trim() === id) {
        sheet.getRange(i + 1, 1, 1, rowValues.length).setValues([rowValues]);
        return getAllData();
      }
    }
  }

  sheet.appendRow(rowValues);
  return getAllData();
}

function deleteRecord(tableName, id) {
  initDatabase();
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sheet = ss.getSheetByName(tableName);
  if (!sheet) throw new Error('Tabel tidak ditemukan: ' + tableName);

  const data = sheet.getDataRange().getValues();
  const targetId = String(id || '').trim();

  for (let i = 1; i < data.length; i++) {
    if (String(data[i][0]).trim() === targetId) {
      sheet.deleteRow(i + 1);
      break;
    }
  }

  return getAllData();
}
