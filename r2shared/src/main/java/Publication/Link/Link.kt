package Publication.Link

import java.sql.Timestamp

/**
 * Created by cbaumann on 30/08/2017.
 */

class Link {

    var href: String? = null
    /// MIME type of resource.
    var typeLink: String? = null
    /// Indicates the relationship between the resource and its containing collection.
    var rel = emptyList<String>().toMutableList()
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
}