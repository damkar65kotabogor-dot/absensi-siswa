package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AbsensiDatabase
import com.example.data.AbsensiRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Absensi Siswa", appName)
    }

    @Test
    fun `auto seed 2 dummy data rows per table and plain text login`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AbsensiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = AbsensiRepository(db.absensiDao())

        repo.ensureDummyDataSeeded()

        assertEquals(2, repo.allUsers.first().size)
        assertEquals(2, repo.allKelas.first().size)
        assertEquals(2, repo.allSiswa.first().size)
        assertEquals(2, repo.allKelompok.first().size)
        assertEquals(2, repo.allAbsensi.first().size)

        val adminUser = repo.login("admin", "admin123")
        assertNotNull(adminUser)
        assertEquals("Admin", adminUser?.role)

        db.close()
    }
}
