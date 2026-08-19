package com.github.libretube.helpers

object ArtistHelper {
    /**
     * Separators that music uploads conventionally put between the artist and the song name.
     * Ordered from most to least common, the fold below stops at whichever matches first.
     */
    private val SEPARATORS = listOf(" - ", " – ", " — ", " | ", " ft. ", " feat. ", " Ft. ")

    /**
     * Guess the artist of a song from its video title.
     *
     * Neither the extractor nor Piped expose the artist YouTube itself knows about, so the
     * title is all there is to go by. Uploads are conventionally titled "Artist - Song", and
     * a title without any separator gives nothing away and is rejected instead of guessed.
     */
    fun guessArtist(title: String): String? {
        val trimmed = title.trim()
        val artist = SEPARATORS
            .fold(trimmed) { current, separator -> current.substringBefore(separator) }
            .trim()

        // nothing was cut off, so the whole title is the song name rather than an artist
        if (artist.length == trimmed.length) return null

        return artist.takeIf { it.length in 2..40 }
    }

    /**
     * Whether the artist published the song on their own channel. If they did, that channel is
     * a better source for more of their work than a search for their name would be.
     */
    fun isOwnUpload(artist: String, uploader: String) = uploader.contains(artist, ignoreCase = true)
}
