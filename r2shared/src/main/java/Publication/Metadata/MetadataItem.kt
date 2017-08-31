package Publication.Metadata

/**
 * Created by cbaumann on 31/08/2017.
 */
class MetadataItem{

    var property: String? = null
    var value: String? = null
    var children = emptyList<MetadataItem>().toMutableList()
}