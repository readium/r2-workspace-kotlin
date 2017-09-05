package org.readium.r2shared

enum class RenditionLayout{
    reflowable,
    fixed
}

enum class RenditionFlow{
    paginated,
    continuous,
    document,
    fixed
}

enum class RenditionOrientation{
    auto,
    landscape,
    portrait
}

enum class RenditionSpread{
    auto,
    landscape,
    portrait,
    both,
    none
}

class Rendition{
    var flow: RenditionFlow? = null
    var spread: RenditionSpread? = null
    var layout: RenditionLayout? = null
    var viewport: String? = null
    var orientation: RenditionOrientation? = null
    fun isEmpty() : Boolean {
        if (layout != null
                || flow != null
                || spread != null
                || viewport != null
                || orientation != null)
            return false
        return true
    }
}