package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.SocialDao
import com.example.data.entities.CrossPostEntity
import com.example.data.entities.FavoritePlatformEntity
import com.example.data.entities.RecentBrowsingEntity
import com.example.data.entities.SocialAccountEntity
import com.example.data.entities.SocialFeedEntity

@Database(
    entities = [
        SocialFeedEntity::class,
        SocialAccountEntity::class,
        CrossPostEntity::class,
        FavoritePlatformEntity::class,
        RecentBrowsingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SocialDatabase : RoomDatabase() {
    abstract fun socialDao(): SocialDao

    companion object {
        @Volatile
        private var INSTANCE: SocialDatabase? = null

        fun getInstance(context: Context): SocialDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SocialDatabase::class.java,
                    "social_hub_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
