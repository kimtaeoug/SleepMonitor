package ted.gun0912.sleep.local.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val roomMigrations: Array<Migration> = arrayOf(
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE sleep_record ADD COLUMN sleepType TEXT NOT NULL DEFAULT 'NIGHT'"
            )
        }
    }
)
