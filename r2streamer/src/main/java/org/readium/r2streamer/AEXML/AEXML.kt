package org.readium.r2streamer.AEXML

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.sql.Time
import java.util.*

class Node(val name: String) {

    var children: MutableList<Node> = mutableListOf()
    var properties: MutableMap<String, String> = mutableMapOf()
    var text: String? = ""

    fun get(name: String) : List<Node> {
        val foundNodes: MutableList<Node> = mutableListOf()
        for (child in children) {
            if (child.name == name)
                foundNodes.add(child)
        }
        return foundNodes
    }

    fun getFirst(name: String) : Node? {
        for (child in children) {
            if (child.name == name)
                return child
        }
        return null
    }

}

class AEXML {

    var nodes: MutableList<Node> = mutableListOf()

    fun get(name: String) : List<Node> {
        val foundNodes: MutableList<Node> = mutableListOf()
        for (node in nodes) {
            if (node.name == name)
                foundNodes.add(node)
        }
        return foundNodes
    }

    fun getFirst(name: String) : Node? {
        for (node in nodes) {
            if (node.name == name)
                return node
        }
        return null
    }


    fun parseXml(stream: InputStream) {
        nodes = mutableListOf()
        val parser = Xml.newPullParser()

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)
        parser.nextTag()
        while (parser.eventType != XmlPullParser.END_DOCUMENT){
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    val node = Node(parser.name)
                    for (i in 0 until parser.attributeCount - 1)
                        node.properties.put(parser.getAttributeName(i), parser.getAttributeValue(i))
                    if (!(nodes.isEmpty()))
                        nodes.last().children.add(node)
                    nodes.add(node)
                } XmlPullParser.END_TAG -> {
                    if (nodes.size > 1)
                        nodes.remove(nodes.last())
                } XmlPullParser.TEXT -> {
                    nodes.last().text += parser.text
                }
            }
            parser.nextToken()
        }
        stream.close()
    }
}