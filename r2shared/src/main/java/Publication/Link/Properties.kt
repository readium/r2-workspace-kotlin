package Publication.Link

/**
 * Created by cbaumann on 31/08/2017.
 */

class Properties{
    /// Suggested orientation for the device when displaying the linked resource.
    var orientation: String? = null
    /// Indicates how the linked resource should be displayed in a reading
    /// environment that displays synthetic spreads.
    var page: String? = null
    /// Indentifies content contained in the linked resource, that cannot be
    /// strictly identified using a media type.
    var contains = emptyList<String>().toMutableList()
    /// Location of a media-overlay for the resource referenced in the Link Object.
    var mediaOverlay: String? = null
    /// Indicates that a resource is encrypted/obfuscated and provides relevant
    /// information for decryption.
    var encryption: Encryption? = null
    /// Hint about the nature of the layout for the linked resources.
    var layout: String? = null
    /// Suggested method for handling overflow while displaying the linked resource.
    var overflow: String? = null
    /// Indicates the condition to be met for the linked resource to be rendered
    /// within a synthetic spread.
    var spread: String? = null

}