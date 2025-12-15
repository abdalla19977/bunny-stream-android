package net.bunny.api

import android.content.Context
import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import net.bunny.api.api.ManageCollectionsApi
import net.bunny.api.api.ManageVideosApi
import net.bunny.api.ktor.initHttpClient
import net.bunny.api.progress.DefaultProgressRepository
import net.bunny.api.settings.data.DefaultSettingsRepository
import net.bunny.api.settings.domain.model.PlayerSettings
import org.openapitools.client.infrastructure.ApiClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class BunnyStreamApi private constructor(
    context: Context,
    accessKey: String?,
) : StreamApi {

    companion object {
        private const val TUS_PREFS_FILE = "tusPrefs"

        const val baseApi = BuildConfig.BASE_API

        lateinit var cdnHostname: String
            private set

        var libraryId: Long = -1
            private set

        @Volatile
        private var instance: StreamApi? = null

        fun initialize(context: Context, accessKey: String?, libraryId: Long) {
            instance = BunnyStreamApi(
                context.applicationContext,
                accessKey,
            )

            this.libraryId = libraryId
            accessKey?.let {
                ApiClient.apiKey["AccessKey"] = it
            }
        }

        fun getInstance(): StreamApi {
            return instance!!
        }

        fun isInitialized(): Boolean {
            return instance != null
        }

        fun release() {
            instance = null
        }
    }

    override val collectionsApi = ManageCollectionsApi(baseApi)

    // OkHttp client that injects Referer for the /play endpoint
    private val okHttpClientWithReferer: OkHttpClient = ApiClient.defaultClient
        .newBuilder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest = chain.request()
            val path = originalRequest.url.encodedPath

            val isPlayEndpoint = path.endsWith("/play")
            val requestBuilder = originalRequest.newBuilder()

            if (isPlayEndpoint) {
                requestBuilder.header("Referer", "https://iframe.mediadelivery.net/")
            }

            chain.proceed(requestBuilder.build())
        })
        .build()

    override val videosApi = ManageVideosApi(baseApi, okHttpClientWithReferer)

    private val prefs = context.getSharedPreferences(TUS_PREFS_FILE, Context.MODE_PRIVATE)

    private val ktorClient = initHttpClient(accessKey)

    override val progressRepository = DefaultProgressRepository(
        httpClient = ktorClient,
        coroutineDispatcher = Dispatchers.IO
    )

    override val settingsRepository = DefaultSettingsRepository(
        httpClient = ktorClient,
        coroutineDispatcher = Dispatchers.IO
    )

    override suspend fun fetchPlayerSettings(libraryId: Long, videoId: String): Either<String, PlayerSettings> {
        return settingsRepository.fetchSettings(libraryId, videoId)
    }

    override suspend fun fetchPlayerSettingsWithToken(
        libraryId: Long,
        videoId: String,
        token: String,
        expires: Long
    ): Either<String, PlayerSettings> {
        return settingsRepository.fetchSettingsWithToken(libraryId, videoId, token, expires)
    }
}
