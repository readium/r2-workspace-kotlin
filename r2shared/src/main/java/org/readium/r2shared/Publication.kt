import org.readium.r2shared.Link

/**
 * Created by cbaumann on 30/08/2017.
 */

 class Publication {
    /// The version of the publication, if the type needs any.
    var version: Double = 0.0
    /// The metadata (title, identifier, contributors, etc.).
    var metadata = Metadata()
    /// org.readium.r2shared.Link to special ressources which are added to the publication.
    var links = arrayOf<Link>()
    /// Links of the spine items of the publication.
    var spine = arrayOf<Link>()
    /// org.readium.r2shared.Link to the ressources of the publication.
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

    fun linkContains(rel: String) : Link? {
        val findLinkWithRel: (Link) -> Boolean = { it.rel.contains(rel) }
        return findLinkInPublicationLinks(findLinkWithRel)
    }

    fun linkEquals(href: String) : Link? {
        val findLinkWithHref: (Link) -> Boolean = { href == it.href }
        return findLinkInPublicationLinks(findLinkWithHref)
    }

    private fun findLinkInPublicationLinks (closure: (Link) -> Boolean) : Link? {
 //       resources.first(closure).let {
 //           if (it != null)
 //               return it
 //       }
 //       spine.first(closure).let {return it}
 //       links.first(closure).let { return it }
        return null
    }
}