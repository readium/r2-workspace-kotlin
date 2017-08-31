package Publication.Metadata

/**
 * Created by cbaumann on 31/08/2017.
 */

class Contributor{

    var multilangName = MultilangString()
    var name: String? = null
        get() = multilangName.singleString
    var sortAs: String? = null
    var identifier: String? = null
    var roles = emptyList<String>().toMutableList()

}