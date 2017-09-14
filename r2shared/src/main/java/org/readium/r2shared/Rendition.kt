package org.readium.r2shared

enum class RenditionLayout(val ini: String){
    reflowable("reflowable"),
    fixed("fixed")
}

enum class RenditionFlow(val ini: String){
    paginated("paginated"),
    continuous("continuous"),
    document("document"),
    fixed("fixed")
}

enum class RenditionOrientation(val ini: String){
    auto("auto"),
    landscape("landscape"),
    portrait("portrait")
}

enum class RenditionSpread(val ini: String){
    auto("auto"),
    landscape("landscape"),
    portrait("portrait"),
    both("both"),
    none("none")
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