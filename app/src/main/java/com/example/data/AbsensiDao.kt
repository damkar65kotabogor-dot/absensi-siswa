package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsensiDao {

    // ==================== 1A. USERS ====================
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Int)

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    // ==================== 1B. KELAS ====================
    @Query("SELECT * FROM kelas ORDER BY id ASC")
    fun getAllKelas(): Flow<List<KelasEntity>>

    @Query("SELECT COUNT(*) FROM kelas")
    suspend fun getKelasCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelas(kelas: KelasEntity)

    @Update
    suspend fun updateKelas(kelas: KelasEntity)

    @Query("DELETE FROM kelas WHERE id = :id")
    suspend fun deleteKelasById(id: Int)

    @Query("DELETE FROM kelas")
    suspend fun clearKelas()

    // ==================== 2. SISWA ====================
    @Query("SELECT * FROM siswa ORDER BY id ASC")
    fun getAllSiswa(): Flow<List<SiswaEntity>>

    @Query("SELECT COUNT(*) FROM siswa")
    suspend fun getSiswaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiswa(siswa: SiswaEntity)

    @Update
    suspend fun updateSiswa(siswa: SiswaEntity)

    @Query("DELETE FROM siswa WHERE id = :id")
    suspend fun deleteSiswaById(id: Int)

    @Query("DELETE FROM siswa")
    suspend fun clearSiswa()

    // ==================== 3. KELOMPOK SISWA PER KELAS ====================
    @Query("SELECT * FROM kelompok ORDER BY id ASC")
    fun getAllKelompok(): Flow<List<KelompokEntity>>

    @Query("SELECT COUNT(*) FROM kelompok")
    suspend fun getKelompokCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelompok(kelompok: KelompokEntity)

    @Update
    suspend fun updateKelompok(kelompok: KelompokEntity)

    @Query("DELETE FROM kelompok WHERE id = :id")
    suspend fun deleteKelompokById(id: Int)

    @Query("DELETE FROM kelompok")
    suspend fun clearKelompok()

    // ==================== 4. ABSENSI SISWA PER KELAS ====================
    @Query("SELECT * FROM absensi ORDER BY tanggal DESC, id DESC")
    fun getAllAbsensi(): Flow<List<AbsensiEntity>>

    @Query("SELECT COUNT(*) FROM absensi")
    suspend fun getAbsensiCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbsensi(absensi: AbsensiEntity)

    @Update
    suspend fun updateAbsensi(absensi: AbsensiEntity)

    @Query("DELETE FROM absensi WHERE id = :id")
    suspend fun deleteAbsensiById(id: Int)

    @Query("DELETE FROM absensi")
    suspend fun clearAbsensi()
}
