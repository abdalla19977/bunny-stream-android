package net.bunny.bunnystreamplayer.ui

import net.bunny.bunnystreamplayer.model.PlayerIconSet

interface BunnyPlayer {

    /**
     * Apply custom icons to video player interface
     */
    var iconSet: PlayerIconSet

    /**
     * Plays a video and fetches additional info, e.g. chapters, moments and subtitles
     *
     * @param videoId Video ID
     */
    fun playVideoWithToken(
        videoId: String,
        libraryId: Long?,
        videoTitle: String,
        token: String,
        expires: Long
    )

    /**
     * Pauses video
     */
    fun pause()

    /**
     * Resumes playing video
     */
    fun play()

    /**
     * Get current position of video
     */
    fun getCurrentPosition(): Long

    /**
     * Get duration of video
     */
    fun getDuration(): Long

    /**
     * Get progress of video
     */
    fun getProgress(): Float
}