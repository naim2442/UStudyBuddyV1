package com.example.ustudybuddyv1.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 3, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile TaskDatabase INSTANCE;

    public static TaskDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TaskDatabase.class, "task_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Include migration steps
                            .fallbackToDestructiveMigration()  // This will delete the old DB if migration fails
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Migration from version 1 to version 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the table for version 2 (in case it doesn't exist)
            database.execSQL("CREATE TABLE IF NOT EXISTS `Task` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `completionPercentage` INTEGER NOT NULL DEFAULT 0)");
        }
    };

    // Migration from version 2 to version 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add a new column `newColumn` for version 3
            database.execSQL("ALTER TABLE Task ADD COLUMN newColumn INTEGER NOT NULL DEFAULT 0");
        }
    };
}
