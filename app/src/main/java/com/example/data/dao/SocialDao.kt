package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.CrossPostEntity
import com.example.data.entities.FavoritePlatformEntity
import com.example.data.entities.RecentBrowsingEntity
import com.example.data.entities.SocialAccountEntity
import com.example.data.entities.SocialFeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    // Social Feeds
    @Query("SELECT * FROM social_feeds ORDER BY timestamp DESC")
    fun getAllFeeds(): Flow<List<SocialFeedEntity>>

    @Query("SELECT * FROM social_feeds WHERE platform = :platform ORDER BY timestamp DESC")
    fun getFeedsByPlatform(platform: String): Flow<List<SocialFeedEntity>>

    @Query("SELECT * FROM social_feeds WHERE postType = 'MESSAGE' ORDER BY timestamp DESC")
    fun getMessages(): Flow<List<SocialFeedEntity>>

    @Query("SELECT * FROM social_feeds WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedFeeds(): Flow<List<SocialFeedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeds(feeds: List<SocialFeedEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed: SocialFeedEntity): Long

    @Update
    suspend fun updateFeed(feed: SocialFeedEntity)

    @Query("UPDATE social_feeds SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE social_feeds SET isLiked = :isLiked, likesCount = likesCount + (CASE WHEN :isLiked THEN 1 ELSE -1 END) WHERE id = :id")
    suspend fun updateLike(id: Long, isLiked: Boolean)

    @Query("UPDATE social_feeds SET isUnread = 0 WHERE id = :id")
    suspend fun markFeedAsRead(id: Long)

    @Query("DELETE FROM social_feeds WHERE id = :id")
    suspend fun deleteFeed(id: Long)

    @Query("SELECT COUNT(*) FROM social_feeds")
    suspend fun getFeedsCount(): Int

    // Social Accounts
    @Query("SELECT * FROM social_accounts")
    fun getAllAccounts(): Flow<List<SocialAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<SocialAccountEntity>)

    @Query("UPDATE social_accounts SET syncEnabled = :syncEnabled WHERE id = :id")
    suspend fun updateAccountSync(id: Long, syncEnabled: Boolean)

    @Query("SELECT COUNT(*) FROM social_accounts")
    suspend fun getAccountsCount(): Int

    // Cross Posts
    @Query("SELECT * FROM cross_posts ORDER BY createdAt DESC")
    fun getAllCrossPosts(): Flow<List<CrossPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossPost(crossPost: CrossPostEntity): Long

    @Query("DELETE FROM cross_posts WHERE id = :id")
    suspend fun deleteCrossPost(id: Long)

    // Favorites
    @Query("SELECT * FROM favorite_platforms")
    fun getAllFavorites(): Flow<List<FavoritePlatformEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFavorite(favorite: FavoritePlatformEntity)

    @Query("DELETE FROM favorite_platforms WHERE platformId = :platformId")
    suspend fun removeFavorite(platformId: String)

    // Recent Browsing History
    @Query("SELECT * FROM recent_browsing ORDER BY visitedAt DESC LIMIT 20")
    fun getRecentBrowsing(): Flow<List<RecentBrowsingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: RecentBrowsingEntity): Long

    @Query("DELETE FROM recent_browsing")
    suspend fun clearRecentBrowsing()
}
