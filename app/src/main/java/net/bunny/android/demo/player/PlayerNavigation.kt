package net.bunny.android.demo.player

import android.content.Context
import android.content.pm.PackageManager
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.bunny.android.demo.ui.AppState
import net.bunny.tv.ui.BunnyTVPlayerActivity
import java.net.URLEncoder

const val PLAYER_ROUTE = "player"
const val VIDEO_ID = "videoId"
const val LIBRARY_ID = "libraryId"
const val TOKEN = "token"
const val EXPIRES = "expires"

// Helper function to check if running on TV
private fun Context.isRunningOnTV(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

// Extension function for NavController
fun NavController.navigateToPlayer(
    videoId: String,
    libraryId: Long?,
    videoTitle: String? = null,
    token: String? = null,
    expires: Long? = null,
) {
    val context = this.context

    if (context.isRunningOnTV()) {
        // Use TV player - try to launch TV player activity if available
        try {
            BunnyTVPlayerActivity.start(
                context = context,
                videoId = videoId,
                libraryId = libraryId ?: -1L,
                videoTitle = videoTitle
            )
        } catch (e: Exception) {
            // TV player not available, fall back to mobile navigation
            val encodedVideoId = URLEncoder.encode(videoId, "UTF-8")
            val libSegment = libraryId ?: -1L
            val tokenSegment = token?.let { URLEncoder.encode(it, "UTF-8") } ?: ""
            val expiresSegment = expires ?: -1L
            navigate("$PLAYER_ROUTE/$encodedVideoId/$libSegment/$tokenSegment/$expiresSegment")
        }
    } else {
        // Use mobile player (existing navigation)
        val encodedVideoId = URLEncoder.encode(videoId, "UTF-8")
        val libSegment = libraryId ?: -1L
        val tokenSegment = token?.let { URLEncoder.encode(it, "UTF-8") } ?: ""
        val expiresSegment = expires ?: -1L
        navigate("$PLAYER_ROUTE/$encodedVideoId/$libSegment/$tokenSegment/$expiresSegment")
    }
}

fun NavGraphBuilder.playerScreen(appState: AppState) {
    composable(
        route = "$PLAYER_ROUTE/{$VIDEO_ID}/{$LIBRARY_ID}/{$TOKEN}/{$EXPIRES}",
        arguments = listOf(
            navArgument(VIDEO_ID) {
                type = NavType.StringType
            },
            navArgument(TOKEN) {
                type = NavType.StringType
                defaultValue = ""
                nullable = false
            },
            navArgument(EXPIRES) {
                type = NavType.LongType
                defaultValue = -1L
                nullable = false
            },
            navArgument(LIBRARY_ID) {
                type = NavType.LongType
                defaultValue = -1L     // must be non-null
                nullable = false   // we're not really "nullable" at Nav‐level
            }
        )
    ) { backStack ->
        val videoId = "2faa7ca3-4611-4944-b088-5a2d188a5bbd"
        val libraryId = 523274.toLong()
        val token = "52ed6ad660eac0506d7ef42c628b0a641d272c7f211d7f6f01db6528bcd3df30"
        val expires = 1768378183.toLong()
        PlayerRoute(appState, videoId, libraryId, token, expires)
    }
}