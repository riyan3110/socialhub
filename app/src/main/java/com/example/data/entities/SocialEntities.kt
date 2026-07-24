package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_feeds")
data class SocialFeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String, // TELEGRAM, WHATSAPP, etc.
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String? = null,
    val content: String,
    val mediaType: String = "TEXT", // TEXT, IMAGE, VIDEO
    val mediaUrl: String? = null,
    val postType: String = "POST", // POST, MESSAGE, STORY, REELS, NOTIFICATION
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isUnread: Boolean = false,
    val isBookmarked: Boolean = false,
    val channelOrGroupName: String? = null,
    val isLiked: Boolean = false
)

@Entity(tableName = "social_accounts")
data class SocialAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String,
    val accountName: String,
    val handle: String,
    val followerCount: String,
    val unreadCount: Int = 0,
    val isConnected: Boolean = true,
    val syncEnabled: Boolean = true,
    val lastSynced: String = "Baru saja"
)

@Entity(tableName = "cross_posts")
data class CrossPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caption: String,
    val mediaUri: String? = null,
    val targetPlatformsCsv: String, // e.g. "TELEGRAM,WHATSAPP,INSTAGRAM"
    val scheduledTime: String? = null,
    val status: String = "PUBLISHED", // DRAFT, SCHEDULED, PUBLISHED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_platforms")
data class FavoritePlatformEntity(
    @PrimaryKey val platformId: String,
    val isFavorite: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_browsing")
data class RecentBrowsingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platformId: String,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)
