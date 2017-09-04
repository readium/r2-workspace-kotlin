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
    var metadata = Metadata()
    /// Publication.Link.Link to special ressources which are added to the publication.
    var links: MutableList<Link> = mutableListOf()
    /// Links of the spine items of the publication.
    var spine: MutableList<Link> = mutableListOf()
    /// Publication.Link.Link to the ressources of the publication.
    var resources: MutableList<Link> = mutableListOf()
    /// Table of content of the publication.
    var tableOfContents: MutableList<Link> = mutableListOf()
    var landmarks: MutableList<Link> = mutableListOf()
    var listOfAudioFiles: MutableList<Link> = mutableListOf()
    var listOfIllustrations: MutableList<Link> = mutableListOf()
    var listOfTables: MutableList<Link> = mutableListOf()
    var listOfVideos: MutableList<Link> = mutableListOf()
    var pageList: MutableList<Link> = mutableListOf()

    /// Extension point for links that shouldn't show up in the manifest.
    var otherLinks: MutableList<Link> = mutableListOf()
    // TODO: other collections
    // var otherCollections: [publicationCollection]
    var internalData: Map<String, String> = emptyMap()

    var coverLink: Link?  = null
        get() = linkContains("cover")

    val baseUrl: URL? by lazy {
        val selfLink = linkContains("self") ?: null
        val url = selfLink?.let{ URL(selfLink.href)} ?: null
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
        return try {
            resources.first(closure) } catch (e: NullPointerException) {
            try {
                spine.first(closure)
            } catch (e: NullPointerException) {
                try {
                    links.first(closure)
                } catch (e: NullPointerException) {
                    null
                }
            }
        }
    }
}