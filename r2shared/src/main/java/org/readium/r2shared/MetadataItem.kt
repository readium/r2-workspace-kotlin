package org.readium.r2shared

class MetadataItem{

    var property: String? = null
    var value: String? = null
    var children: MutableList<MetadataItem> = mutableListOf()
}