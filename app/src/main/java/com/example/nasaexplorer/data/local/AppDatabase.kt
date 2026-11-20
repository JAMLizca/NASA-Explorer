/** Configura Room y expone las fuentes de datos locales de la aplicación. */
package com.example.nasaexplorer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nasaexplorer.data.local.dao.AssistantHistoryDao
import com.example.nasaexplorer.data.local.dao.FavoriteDao
import com.example.nasaexplorer.data.local.entity.AssistantHistoryEntity
import com.example.nasaexplorer.data.local.entity.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class, AssistantHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun assistantHistoryDao(): AssistantHistoryDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS assistant_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        question TEXT NOT NULL,
                        answer TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Implementa el comportamiento asociado a get database. */
        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nasa_explorer_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
