package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        KelasEntity::class,
        SiswaEntity::class,
        KelompokEntity::class,
        AbsensiEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AbsensiDatabase : RoomDatabase() {
    abstract fun absensiDao(): AbsensiDao

    companion object {
        @Volatile
        private var INSTANCE: AbsensiDatabase? = null

        fun getDatabase(context: Context): AbsensiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AbsensiDatabase::class.java,
                    "absensi_siswa_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
