/** Configura la base de datos local y expone sus fuentes de datos. */
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

    /** Implementa la operación principal de esta sección. */
    abstract fun favoriteDao(): FavoriteDao
    /** Implementa la operación principal de esta sección. */
    abstract fun assistantHistoryDao(): AssistantHistoryDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            /** Implementa la operación principal de esta sección. */
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

        /** Implementa la operación principal de esta sección. */
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
