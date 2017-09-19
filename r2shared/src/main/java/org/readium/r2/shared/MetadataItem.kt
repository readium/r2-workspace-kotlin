package org.readium.r2.shared

class MetadataItem{

    var property: String? = null
    var value: String? = null
    var children: MutableList<MetadataItem> = mutableListOf()
}