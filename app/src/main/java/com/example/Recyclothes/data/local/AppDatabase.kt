package com.example.Recyclothes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Recyclothes.data.dao.CharityDao
import com.example.Recyclothes.data.dao.DonationDao
import com.example.Recyclothes.data.dao.DonationPointDao
import com.example.Recyclothes.data.dao.DraftDao
import com.example.Recyclothes.data.dao.FavoriteCharityDao
import com.example.Recyclothes.data.dao.PickupRequestDao

@Database(
    entities = [
        DonationPointEntity::class,
        DonationEntity::class,
        CharityEntity::class,
        PickupRequestEntity::class,
        DraftDonationEntity::class,
        FavoriteCharityEntity::class,
        FavoriteOpEntity::class
    ],
    version = 3,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun charityDao(): CharityDao

    abstract fun favoriteCharityDao(): FavoriteCharityDao

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


    abstract fun donationPointDao(): DonationPointDao

    abstract fun donationDao(): DonationDao
    abstract fun pickupRequestDao(): PickupRequestDao
    abstract fun draftDao(): DraftDao
}
