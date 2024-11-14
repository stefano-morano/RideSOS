package com.example.crashsimulator;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HospitalEntity.class}, version = 2)
public abstract class HospitalDatabase extends RoomDatabase {
    private static volatile HospitalDatabase INSTANCE;
    public abstract HospitalDAO hospitalDAO();

    public static HospitalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HospitalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HospitalDatabase.class, "hospitals_db")
                             .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
