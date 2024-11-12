package com.example.crashsimulator;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HospitalDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<HospitalEntity> hospitals);

    @Query("SELECT * FROM hospitals_db")
    List<HospitalEntity> getAllHospitals();

    @Query("SELECT * FROM hospitals_db LIMIT 1 OFFSET :position")
    HospitalEntity getHospitalAtPosition(int position);
}
