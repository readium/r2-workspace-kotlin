package org.readium.r2.streamer.AEXML

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class AEXML {

    var nodes: MutableList<Node> = mutableListOf()

    fun get(name: String) = try { nodes.filter{it.name == name} } catch(e: Exception) { null }

    fun getFirst(name: String) = try { nodes.first{it.name == name} } catch(e: Exception) { null }

    fun root() = try { nodes.first() } catch (e: Exception) { null }

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
                    for (i in 0 until parser.attributeCount)
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