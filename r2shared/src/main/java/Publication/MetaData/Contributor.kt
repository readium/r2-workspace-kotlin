package Publication.MetaData

class Contributor{

    var multilangName = MultilangString()
    var name: String? = null
        get() = multilangName.singleString
    var sortAs: String? = null
    var identifier: String? = null
    var roles: MutableList<String> = mutableListOf()

}