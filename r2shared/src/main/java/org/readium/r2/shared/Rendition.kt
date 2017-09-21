package org.readium.r2.shared

import org.json.JSONObject

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
        return (layout == null
                && flow == null
                && spread == null
                && viewport == null
                && orientation == null)
    }

    fun getJSON() : JSONObject{
        val obj = JSONObject()
        obj.putOpt("flow", flow?.toString())
        obj.putOpt("spread", spread?.toString())
        obj.putOpt("layout", layout?.toString())
        obj.putOpt("viewport", viewport)
        obj.putOpt("orientation", orientation?.toString())
        return obj
    }

}