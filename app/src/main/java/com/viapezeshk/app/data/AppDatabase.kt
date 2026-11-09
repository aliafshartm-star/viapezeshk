package com.viapezeshk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Disease::class, Drug::class, Medication::class, HealthRecord::class], 
    version = 10, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diseaseDao(): DiseaseDao
    abstract fun drugDao(): DrugDao
    abstract fun medicationDao(): MedicationDao
    abstract fun healthRecordDao(): HealthRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viapezeshk_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        // فقط دیتای بیماری‌ها و داروهای پایه رو لود می‌کنیم
                        // جدول‌های جدید (پنل سلامت) از ابتدا خالی هستند تا کاربر خودش پر کنه
                        database.diseaseDao().insertAll(InitialData.diseases)
                        database.drugDao().insertAll(InitialData.drugs)
                    }
                }
            }
        }
    }
}