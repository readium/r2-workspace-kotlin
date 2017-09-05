package org.readium.r2shared

import java.sql.Timestamp

//  A link to a resource
class Link {
    //  The link destination
    var href: String? = null
    /// MIME type of resource.
    var typeLink: String? = null
    /// Indicates the relationship between the resource and its containing collection.
    var rel: MutableList<String> = mutableListOf()
    /// Indicates the height of the linked resource in pixels.
    var height: Int = 0
    /// Indicates the width of the linked resource in pixels.
    var width: Int = 0
    var title: String? = null
    /// Properties associated to the linked resource.
    var properties = Properties()
    /// Indicates the length of the linked resource in seconds.
    var duration: Timestamp? = null
    /// Indicates that the linked resource is a URI template.
    var templated: Boolean? = false
    //  The underlaying nodes in a tree structure of Links
    var children: MutableList<Link> = mutableListOf()
    //  The MediaOverlays associated to the resource of the Link
    var mediaOverlays: MediaOverlays = MediaOverlays()

    fun isEncrypted() : Boolean {
        return !(properties.encryption == null)
    }
}