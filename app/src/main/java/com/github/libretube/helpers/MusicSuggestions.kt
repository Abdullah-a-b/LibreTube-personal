package com.github.libretube.helpers

import com.github.libretube.api.obj.StreamItem

/**
 * Picks the suggestions shown next to a song.
 *
 * A signed out client gets whatever is popular in its region mixed into the suggestions, which
 * has no place next to music. Neither the extractor nor Piped expose the category of a suggested
 * video, so the length and the "Artist - Song" title convention are all there is to tell the two
 * apart.
 */
object MusicSuggestions {
    /** how many works of the artist lead the list before other artists follow */
    const val LEAD = 6

    /** shortest and longest a suggestion may run to still pass as a song */
    private const val MIN_SECONDS = 60L
    private const val MAX_SECONDS = 15 * 60L

    fun isMusic(category: String) = category.equals("Music", ignoreCase = true)

    /**
     * The artist of a song. Songs are often uploaded by somebody else, so the title is asked
     * first and the uploader only serves as a fallback.
     */
    fun artistOf(title: String, uploader: String) = ArtistHelper.guessArtist(title) ?: uploader

    /**
     * The songs among [related], split into the artist that is playing and everybody else.
     */
    fun split(
        related: List<StreamItem>,
        artist: String
    ): Pair<List<StreamItem>, List<StreamItem>> =
        related.filter { it.looksLikeMusic() }.partition { it.mentions(artist) }

    /**
     * Lead with the artist that is playing, then let the list drift to everybody else.
     */
    fun order(
        sameArtist: List<StreamItem>,
        works: List<StreamItem>,
        otherArtists: List<StreamItem>
    ) = sameArtist + works.take(LEAD) + otherArtists + works.drop(LEAD)

    private fun StreamItem.mentions(artist: String) =
        uploaderName?.contains(artist, ignoreCase = true) == true ||
                title?.contains(artist, ignoreCase = true) == true

    private fun StreamItem.looksLikeMusic(): Boolean {
        if (isShort || isLive) return false

        val seconds = duration ?: return false
        if (seconds !in MIN_SECONDS..MAX_SECONDS) return false

        return ArtistHelper.guessArtist(title.orEmpty()) != null
    }
}
