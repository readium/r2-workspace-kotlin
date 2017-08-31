package Publication.MediaOverlays

import java.net.URL

/**
 * Created by cbaumann on 31/08/2017.
 */

class Clip{
    var relativeUrl: URL? = null
    var fragmentId: String? = null
    var start: Double? = null
    var end: Double? = null
    var duration: Double? = null
}

class MediaOverlayNodeError(message: String) : Exception(message)

class MediaOverlayNode (var text: String? = null, var audio: String? = null) {

    var role = emptyList<String>().toMutableList()
    var children = emptyList<MediaOverlayNode>().toMutableList()

    fun fragmentId() : String? {
        val text = this.text ?: return null
        return text.split('#').last()
    }

    fun clip() : Clip {
        var newClip = Clip()

        val audioString = this.audio ?: throw MediaOverlayNodeError("audio")
        val audioFileString = audioString.split('#').first()
        val audioFileUrl = URL(audioFileString)

        newClip.relativeUrl = audioFileUrl
        val times = audioString.split('#').last()
        newClip = parseTimer(times, newClip)
        newClip.fragmentId = fragmentId()
        return newClip
    }

    private fun parseTimer(times: String, clip: Clip) : Clip{
        var times = times.removeRange(0, 2)
        val start = times.split(',').first()
        val end = times.split(',').last()
        val startTimer = start?.toDoubleOrNull() ?: throw MediaOverlayNodeError("timersParsing")
        val endTimer = end?.toDoubleOrNull() ?: throw MediaOverlayNodeError("timerParsing")
        clip.start = startTimer
        clip.end = endTimer
        clip.duration = endTimer - startTimer
        return clip
    }

}