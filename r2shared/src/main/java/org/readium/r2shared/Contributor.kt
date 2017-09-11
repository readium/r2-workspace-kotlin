package org.readium.r2shared

class Contributor{

    var multilangName:MultilangString = MultilangString()

    var name: String? = null
        get() = multilangName.singleString

    var sortAs: String? = null
    var identifier: String? = null
    var roles: MutableList<String> = mutableListOf()

}