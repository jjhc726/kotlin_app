package com.example.Recyclothes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Recyclothes.data.dao.CharityDao
import com.example.Recyclothes.data.dao.DonationDao
import com.example.Recyclothes.data.dao.DonationPointDao

@Database(entities = [DonationPointEntity::class, DonationEntity::class, CharityEntity::class], version = 2, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun donationPointDao(): DonationPointDao

    abstract fun charityDao(): CharityDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }


    abstract fun donationDao(): DonationDao

}