package org.readium.r2.streamer.Fetcher

import com.mcxiaoke.koi.ext.appendTo
import org.readium.r2.shared.Publication
import org.readium.r2.shared.RenditionLayout
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

interface ContentFilters{
    var decoder: Decoder

    fun apply(input: InputStream, publication: Publication, path: String) : InputStream {
        return input
    }

    fun apply(input: ByteArray, publication: Publication, path: String) : ByteArray {
        return input
    }
}

fun URL.removeLastComponent() : URL{
    var str = this.toString()
    val i = str.lastIndexOf('/', 0, true)
    if (i != -1)
        str = str.substring(0, i)
    return URL(str)
}

class ContentFiltersEpub: ContentFilters {

    override var decoder = Decoder()

    override fun apply(input: InputStream, publication: Publication, path: String): InputStream {
        var decodedInputStream = decoder.decoding(input, publication, path)
        val link = publication.linkWithHref("/" + path)
        val baseUrl = publication.baseUrl()?.removeLastComponent()
        if ((link?.typeLink == "application/xhtml+xml" || link?.typeLink == "text/html")
                && baseUrl != null){
            if (publication.metadata.rendition.layout == RenditionLayout.reflowable && link.properties?.layout == null
                    || link.properties?.layout == "reflowable"){
                decodedInputStream = injectReflowableHtml(decodedInputStream, baseUrl) as ByteArrayInputStream
            } else {
                decodedInputStream = injectFixedLayohtHtml(decodedInputStream, baseUrl) as ByteArrayInputStream
            }
        }
        return decodedInputStream
    }

    override fun apply(input: ByteArray, publication: Publication, path: String): ByteArray {
        val inputStream = ByteArrayInputStream(input)
        var decodedInputStream = decoder.decoding(inputStream, publication, path)
        val link = publication.linkWithHref(path)
        val baseUrl = publication.baseUrl()?.removeLastComponent()
        if ((link?.typeLink == "application/xhtml+xml" || link?.typeLink == "text/html")
                && baseUrl != null){
            if (publication.metadata.rendition.layout == RenditionLayout.reflowable && link.properties?.layout == null
                    || link.properties?.layout == "reflowable"){
                decodedInputStream = injectReflowableHtml(decodedInputStream, baseUrl) as ByteArrayInputStream
            } else {
                decodedInputStream = injectFixedLayohtHtml(decodedInputStream, baseUrl) as ByteArrayInputStream
            }
        }
        return decodedInputStream.readBytes()
    }

    private fun injectReflowableHtml(stream: InputStream, baseUrl: URL) : InputStream {
        val data = stream.readBytes()
        var resourceHtml = String(data) //UTF-8
        val endHeadIndex = resourceHtml.indexOf("</head>", 0, false)
        if (endHeadIndex == -1)
            return stream
        val includes = mutableListOf<String>()

        includes.add("<meta name=\"viewport\" content=\"width=device-width, height=device-height, initial-scale=1.0;\"/>\n")
        includes.add(getHtmlLink("/styles/scroll.css"))
        includes.add(getHtmlLink("/styles/user_settings.css"))
        includes.add(getHtmlLink("/styles/html5patch.css"))
        includes.add(getHtmlLink("/styles/pagination.css"))
        includes.add(getHtmlLink("/styles/safeguards.css"))
        includes.add(getHtmlLink("/styles/readiumCSS-base.css"))
        includes.add(getHtmlLink("/styles/readiumCSS-default.css"))
        includes.add(getHtmlLink("/styles/readiumCSS-before.css"))
        includes.add(getHtmlLink("/styles/readiumCSS-after.css"))
        includes.add(getHtmlScript("/scripts/touchHandling.js"))
        includes.add(getHtmlScript("/scripts/utils.js"))
        for (element in includes){
            resourceHtml = StringBuilder(resourceHtml).insert(endHeadIndex, element).toString()
        }
        return ByteArrayInputStream(resourceHtml.toByteArray())
    }

    private fun injectFixedLayohtHtml(stream: InputStream, baseUrl: URL) : InputStream {
        val data = stream.readBytes()
        var resourceHtml = String(data) //UTF-8
        val endHeadIndex = resourceHtml.indexOf("</head>", 0, false)
        if (endHeadIndex == -1)
            return stream
        val includes = mutableListOf<String>()
        val url = baseUrl.toString()
        includes.add("<meta name=\"viewport\" content=\"width=1024; height=768; left=50%; top=50%; bottom=auto; right=auto; transform=translate(-50%, -50%);\"/>\n")
        includes.add(getHtmlScript(url + "scripts/touchHandling.js"))
        includes.add(getHtmlScript(url + "scripts/utils.js"))
        for (element in includes){
            resourceHtml = StringBuilder(resourceHtml).insert(endHeadIndex, element).toString()
        }
        return ByteArrayInputStream(resourceHtml.toByteArray())
    }

    fun getHtmlLink(ressourceName: String) : String {
        val prefix = "<link rel=\"stylesheet\" type=\"text/css\" href=\""
        val suffix = "\"/>\n"
        return prefix + ressourceName + suffix
    }

    fun getHtmlScript(ressourceName: String) : String {
        val prefix = "<script type=\"text/javascript\" src=\""
        val suffix = "\"></script>\n"

        return prefix + ressourceName + suffix
    }

}

class ContentFiltersCbz : ContentFilters {
    override var decoder: Decoder = Decoder()
}

