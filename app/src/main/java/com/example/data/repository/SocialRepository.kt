package com.example.data.repository

import com.example.data.SocialPlatform
import com.example.data.dao.SocialDao
import com.example.data.entities.CrossPostEntity
import com.example.data.entities.SocialAccountEntity
import com.example.data.entities.SocialFeedEntity
import kotlinx.coroutines.flow.Flow

class SocialRepository(private val socialDao: SocialDao) {

    val allFeeds: Flow<List<SocialFeedEntity>> = socialDao.getAllFeeds()
    val allMessages: Flow<List<SocialFeedEntity>> = socialDao.getMessages()
    val bookmarkedFeeds: Flow<List<SocialFeedEntity>> = socialDao.getBookmarkedFeeds()
    val allAccounts: Flow<List<SocialAccountEntity>> = socialDao.getAllAccounts()
    val allCrossPosts: Flow<List<CrossPostEntity>> = socialDao.getAllCrossPosts()

    fun getFeedsByPlatform(platform: String): Flow<List<SocialFeedEntity>> {
        return socialDao.getFeedsByPlatform(platform)
    }

    suspend fun toggleBookmark(feed: SocialFeedEntity) {
        socialDao.updateBookmark(feed.id, !feed.isBookmarked)
    }

    suspend fun toggleLike(feed: SocialFeedEntity) {
        socialDao.updateLike(feed.id, !feed.isLiked)
    }

    suspend fun markAsRead(feedId: Long) {
        socialDao.markFeedAsRead(feedId)
    }

    suspend fun publishCrossPost(
        caption: String,
        mediaUri: String?,
        selectedPlatforms: List<SocialPlatform>,
        isScheduled: Boolean,
        scheduledTime: String?
    ) {
        val platformsCsv = selectedPlatforms.joinToString(",") { it.id }
        val status = if (isScheduled) "SCHEDULED" else "PUBLISHED"
        
        // 1. Save to cross post history
        val postId = socialDao.insertCrossPost(
            CrossPostEntity(
                caption = caption,
                mediaUri = mediaUri,
                targetPlatformsCsv = platformsCsv,
                scheduledTime = scheduledTime,
                status = status
            )
        )

        // 2. If published immediately, inject simulated feed items for each platform!
        if (!isScheduled) {
            selectedPlatforms.forEach { platform ->
                socialDao.insertFeed(
                    SocialFeedEntity(
                        platform = platform.id,
                        authorName = "Saya (Anda)",
                        authorHandle = "@user_utama",
                        content = caption,
                        mediaType = if (mediaUri != null) "IMAGE" else "TEXT",
                        mediaUrl = mediaUri,
                        postType = "POST",
                        timestamp = System.currentTimeMillis(),
                        likesCount = 1,
                        commentsCount = 0,
                        sharesCount = 0,
                        isUnread = false,
                        isBookmarked = false,
                        channelOrGroupName = "SocialHub Publisher"
                    )
                )
            }
        }
    }

    suspend fun toggleAccountSync(accountId: Long, currentSync: Boolean) {
        socialDao.updateAccountSync(accountId, !currentSync)
    }

    suspend fun seedInitialDataIfNeeded() {
        if (socialDao.getAccountsCount() == 0) {
            val accounts = listOf(
                SocialAccountEntity(
                    platform = SocialPlatform.TELEGRAM.id,
                    accountName = "Tech Indo Community",
                    handle = "@tech_indo_chat",
                    followerCount = "14.2K Anggota",
                    unreadCount = 5,
                    isConnected = true,
                    syncEnabled = true,
                    lastSynced = "1 mnt yang lalu"
                ),
                SocialAccountEntity(
                    platform = SocialPlatform.WHATSAPP.id,
                    accountName = "Budi Santoso & Keluarga",
                    handle = "+62 812-3456-7890",
                    followerCount = "38 Kontak Aktif",
                    unreadCount = 3,
                    isConnected = true,
                    syncEnabled = true,
                    lastSynced = "Baru saja"
                ),
                SocialAccountEntity(
                    platform = SocialPlatform.FACEBOOK.id,
                    accountName = "Komunitas Pengembang Web ID",
                    handle = "fb.com/groups/devindo",
                    followerCount = "8.5K Anggota",
                    unreadCount = 2,
                    isConnected = true,
                    syncEnabled = true,
                    lastSynced = "5 mnt yang lalu"
                ),
                SocialAccountEntity(
                    platform = SocialPlatform.INSTAGRAM.id,
                    accountName = "Rian Pratama Official",
                    handle = "@rian_tech_id",
                    followerCount = "24.8K Pengikut",
                    unreadCount = 8,
                    isConnected = true,
                    syncEnabled = true,
                    lastSynced = "Baru saja"
                ),
                SocialAccountEntity(
                    platform = SocialPlatform.TWITTER_X.id,
                    accountName = "Rian Tech Updates",
                    handle = "@rian_tweets",
                    followerCount = "11.3K Pengikut",
                    unreadCount = 4,
                    isConnected = true,
                    syncEnabled = true,
                    lastSynced = "2 mnt yang lalu"
                ),
                SocialAccountEntity(
                    platform = SocialPlatform.TIKTOK.id,
                    accountName = "Rian Creative Clips",
                    handle = "@rian_tiktok_id",
                    followerCount = "45.1K Pengikut",
                    unreadCount = 12,
                    isConnected = true,
                    syncEnabled = true,
                    lastSynced = "10 mnt yang lalu"
                )
            )
            socialDao.insertAccounts(accounts)
        }

        if (socialDao.getFeedsCount() == 0) {
            val now = System.currentTimeMillis()
            val feeds = listOf(
                // Telegram Feed & Chat
                SocialFeedEntity(
                    platform = SocialPlatform.TELEGRAM.id,
                    authorName = "Channel Dev Indonesia",
                    authorHandle = "@dev_id_channel",
                    content = "🚀 Pengumuman: Update Kotlin 2.2 resmi diluncurkan! Performa compiler meningkat hingga 35% pada proyek Android besar.",
                    postType = "POST",
                    timestamp = now - 1000 * 60 * 12,
                    likesCount = 342,
                    commentsCount = 45,
                    sharesCount = 89,
                    isUnread = true,
                    channelOrGroupName = "Dev Indonesia Official"
                ),
                SocialFeedEntity(
                    platform = SocialPlatform.TELEGRAM.id,
                    authorName = "Siti Rahma",
                    authorHandle = "@siti_rahma_tg",
                    content = "Halo Rian, apakah dokumen pendaftaran event webinar besok sudah siap dikirimkan via Telegram?",
                    postType = "MESSAGE",
                    timestamp = now - 1000 * 60 * 25,
                    likesCount = 0,
                    commentsCount = 0,
                    sharesCount = 0,
                    isUnread = true,
                    channelOrGroupName = "Chat Pribadi"
                ),

                // WhatsApp Messages & Status
                SocialFeedEntity(
                    platform = SocialPlatform.WHATSAPP.id,
                    authorName = "Budi Santoso",
                    authorHandle = "+62 812-9988-7766",
                    content = "Mas, jadwal meeting tim pemasaran dimajukan ke jam 14:00 WIB ya di ruang rapat utama.",
                    postType = "MESSAGE",
                    timestamp = now - 1000 * 60 * 8,
                    likesCount = 0,
                    commentsCount = 0,
                    sharesCount = 0,
                    isUnread = true,
                    channelOrGroupName = "Grup Tim Proyek"
                ),
                SocialFeedEntity(
                    platform = SocialPlatform.WHATSAPP.id,
                    authorName = "Dewi Lestari",
                    authorHandle = "+62 856-1122-3344",
                    content = "Status WhatsApp: 'Ngopi sore sambil nyelesaiin laporan akhir bulan ☕✨'",
                    postType = "STORY",
                    timestamp = now - 1000 * 60 * 40,
                    likesCount = 12,
                    commentsCount = 3,
                    sharesCount = 0,
                    isUnread = false,
                    channelOrGroupName = "Status kontak"
                ),

                // Instagram Post & Reels
                SocialFeedEntity(
                    platform = SocialPlatform.INSTAGRAM.id,
                    authorName = "Design Studio ID",
                    authorHandle = "@designstudio.id",
                    content = "5 Tips Desain UI/UX Android Modern di Tahun 2026! Mana yang paling sering kalian gunakan di Jetpack Compose? #uiux #androiddev #design",
                    postType = "POST",
                    timestamp = now - 1000 * 60 * 55,
                    likesCount = 1250,
                    commentsCount = 88,
                    sharesCount = 310,
                    isUnread = false,
                    channelOrGroupName = "Instagram Feed"
                ),
                SocialFeedEntity(
                    platform = SocialPlatform.INSTAGRAM.id,
                    authorName = "Anisa Fitri",
                    authorHandle = "@anisa_fitria",
                    content = "Reels terbaru: 'Cara cepat setup dashboard multi-sosmed di Android dengan Kotlin Jetpack Compose 🔥'",
                    postType = "REELS",
                    timestamp = now - 1000 * 60 * 90,
                    likesCount = 3400,
                    commentsCount = 210,
                    sharesCount = 890,
                    isUnread = true,
                    channelOrGroupName = "Instagram Reels"
                ),

                // Twitter / X Posts
                SocialFeedEntity(
                    platform = SocialPlatform.TWITTER_X.id,
                    authorName = "Andi Tech News",
                    authorHandle = "@andi_technews",
                    content = "Aplikasi penyatu sosial media (SocialHub) menjadi tren utama tahun ini karena menghemat waktu pengguna hingga 50%! Pengalaman tanpa batas dari satu aplikasi.",
                    postType = "POST",
                    timestamp = now - 1000 * 60 * 15,
                    likesCount = 890,
                    commentsCount = 120,
                    sharesCount = 450,
                    isUnread = true,
                    channelOrGroupName = "X Feed"
                ),
                SocialFeedEntity(
                    platform = SocialPlatform.TWITTER_X.id,
                    authorName = "Rina Melati",
                    authorHandle = "@rina_melati",
                    content = "DM X: 'Hai Rian! Tolong cek retweet postingan kampanye baru kita di X ya, terima kasih!'",
                    postType = "MESSAGE",
                    timestamp = now - 1000 * 60 * 120,
                    likesCount = 0,
                    commentsCount = 0,
                    sharesCount = 0,
                    isUnread = true,
                    channelOrGroupName = "Direct Message"
                ),

                // Facebook Post & Groups
                SocialFeedEntity(
                    platform = SocialPlatform.FACEBOOK.id,
                    authorName = "Grup Programmer Indonesia",
                    authorHandle = "fb.com/groups/programmer.id",
                    content = "Ada yang pernah coba bikin aplikasi pengelola sosial media serba ada di Android? Minta rekomendasi arsitektur database lokal terbaik dong!",
                    postType = "POST",
                    timestamp = now - 1000 * 60 * 180,
                    likesCount = 560,
                    commentsCount = 134,
                    sharesCount = 67,
                    isUnread = false,
                    channelOrGroupName = "Facebook Group"
                ),

                // TikTok Video
                SocialFeedEntity(
                    platform = SocialPlatform.TIKTOK.id,
                    authorName = "Gadget & Coding ID",
                    authorHandle = "@gadgetcoding.id",
                    content = "Bikin aplikasi launcher sosial media dalam 5 menit! Tonton tutorial lengkapnya #coding #android #tech #viral",
                    postType = "REELS",
                    timestamp = now - 1000 * 60 * 30,
                    likesCount = 18400,
                    commentsCount = 920,
                    sharesCount = 3100,
                    isUnread = true,
                    channelOrGroupName = "TikTok FYP"
                )
            )
            socialDao.insertFeeds(feeds)
        }
    }
}
