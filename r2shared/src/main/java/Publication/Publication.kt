package Publication

import Publication.Link.Link
import java.net.URL

/**
 * Created by cbaumann on 30/08/2017.
 */

 class Publication {
    /// The version of the publication, if the type needs any.
    var version: Double = 0.0
    /// The metadata (title, identifier, contributors, etc.).
    var metadata = Publication.Metadata()
    /// Publication.Link.Link to special ressources which are added to the publication.
    var links = arrayOf<Link>().toMutableList()
    /// Links of the spine items of the publication.
    var spine = arrayOf<Link>()
    /// Publication.Link.Link to the ressources of the publication.
    var resources = arrayOf<Link>()
    /// Table of content of the publication.
    var tableOfContents = arrayOf<Link>()
    var landmarks = arrayOf<Link>()
    var listOfAudioFiles = arrayOf<Link>()
    var listOfIllustrations = arrayOf<Link>()
    var listOfTables = arrayOf<Link>()
    var listOfVideos = arrayOf<Link>()
    var pageList = arrayOf<Link>()

    /// Extension point for links that shouldn't show up in the manifest.
    var otherLinks = arrayOf<Link>()
    // TODO: other collections
    // var otherCollections: [publicationCollection]
    var internalData = emptyMap<String, String>()

    var coverLink: Link?  = null
        get() = linkContains("cover")

    //  Issue : What if linkContains -> null ?
    val baseUrl: URL? by lazy {
        val url = URL(linkContains("href")?.href)
        val index = url.toString().lastIndexOf('/')
        URL(url.toString().substring(0, index))
    }

    //  To see later : build the manifest
    val manifest: String by lazy {
        //var jsonString = manifest.to
        "manifest"
    }

    val manifestDictionnary = mapOf<String, Any>()

    fun spineLink(href: String) : Link? {
        val findLinkWithRel: (Link) -> Boolean = { href == it.href }
        return findLinkInPublicationLinks(findLinkWithRel)
    }

    fun linkContains(rel: String) : Link? {
        val findLinkWithRel: (Link) -> Boolean = { it.rel.contains(rel) }
        return findLinkInPublicationLinks(findLinkWithRel)
    }

    fun linkEquals(href: String) : Link? {
        val findLinkWithHref: (Link) -> Boolean = { href == it.href }
        return findLinkInPublicationLinks(findLinkWithHref)
    }

    fun uriTo(link: Link?) : URL? {
        val linkHref = link?.href
        val publicationBaseUrl = baseUrl
        if (link != null && linkHref != null && publicationBaseUrl != null)
            return null
        //  Issue : ???
        val trimmedBaseUrlString = publicationBaseUrl.toString().trim('/')
        return URL(trimmedBaseUrlString + "/" + linkHref)
    }

    fun addSelfLink(endPoint: String, baseURL: URL){
        val publicationUrl: URL
        val link = Link()
        val manifestPath = "$endPoint/manifest.json"

        publicationUrl = URL(baseURL.toString() + "/" + manifestPath)
        link.href = publicationUrl.toString()
        link.typeLink = "application/webpub+json"
        link.rel.add("self")
        links.add(link)
    }

    private fun findLinkInPublicationLinks (closure: (Link) -> Boolean) : Link? {
        return try { resources.first(closure) } catch (e: NullPointerException) {
            try { spine.first(closure)
            } catch (e: NullPointerException) {
                try { links.first(closure)
                } catch (e: NullPointerException) {
                    null
                }
            }
        }
    }
}