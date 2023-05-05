package me.rerere.awara.ui.component.player

import android.content.Context
import android.os.storage.StorageManager
import android.util.Log
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

private const val TAG = "PlayerCache"

object PlayerCache {
    private lateinit var cacheFactory: CacheDataSource.Factory

    fun get(context: Context): CacheDataSource.Factory {
        if (!::cacheFactory.isInitialized) {
            val cacheFolder = File(context.cacheDir, "player_cache")
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val maxBytes =
                storageManager.getCacheQuotaBytes(storageManager.getUuidForPath(cacheFolder))
            val cache = SimpleCache(
                cacheFolder,
                LeastRecentlyUsedCacheEvictor(maxBytes),
                StandaloneDatabaseProvider(context)
            )
            val cacheFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                .setCacheKeyFactory { dataSpec ->
                    val filename = dataSpec.uri.getQueryParameter("filename")
                    Log.i(TAG, "rememberPlayerState: ${dataSpec.key} || $filename || ${dataSpec.uri}")
                    dataSpec.key ?: filename ?: dataSpec.uri.toString()
                }
            this.cacheFactory = cacheFactory
        }
        return this.cacheFactory
    }
}