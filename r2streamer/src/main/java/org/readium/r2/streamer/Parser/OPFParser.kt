package org.readium.r2.streamer.Parser

import org.readium.r2shared.Link
import org.readium.r2shared.Metadata
import org.readium.r2shared.Publication
import org.readium.r2.streamer.AEXML.AEXML
import org.readium.r2.streamer.AEXML.Node
import org.readium.r2.streamer.Containers.Container

class OPFParser {

    val smilp = SMILParser()
    var rootFilePath: String? = ""

    fun parseOpf(document: AEXML, container: Container, epubVersion: Double) : Publication {
        val publication = Publication()

        rootFilePath = container.rootFile.rootFilePath
        publication.version = epubVersion
        publication.internalData["type"] = "epub"
        publication.internalData["rootfile"] = rootFilePath!!
        parseMetadata(document, publication)
        parseRessources(document.getFirst("package")!!.getFirst("manifest")!!, publication)

        return publication
    }

    private fun parseMetadata(document: AEXML, publication: Publication) {
        val metadata = Metadata()
        val mp = MetadataParser()
        val metadataElement: Node? = document
                .getFirst("package")
                ?.getFirst("metadata") ?: throw Exception("No metadata")
        metadata.multilangTitle = try {
            mp.mainTitle(metadataElement!!)
        } catch (e: Exception) {
            throw e
        }
        metadata.identifier = mp.uniqueIdentifier(metadataElement,
                document.getFirst("package")!!.properties)
        metadata.description = metadataElement.getFirst("dc:description")?.text
        metadata.publicationDate = metadataElement.getFirst("dc:date")?.text
        metadata.modified = mp.modifiedDate(metadataElement)
        metadata.source = metadataElement.getFirst("dc:sources")?.text
        mp.subject(metadataElement)?.let { metadata.subjects.add(it) }
        metadata.languages = metadataElement.get("dc:language").map { it.text!! }.toMutableList()
        metadata.rights = metadataElement.get("dc:rights").map { it.text!! }.joinToString { " " }
        mp.parseContributors(metadataElement, metadata, publication.version)
        document.getFirst("package")!!.getFirst("spine")!!.properties["page-progression-direction"]?.let {
            metadata.direction = it
        }
        mp.parseRenditionProperties(metadataElement, metadata)
        metadata.otherMetadata = mp.parseMediaDurations(metadataElement, metadata.otherMetadata)
        publication.metadata = metadata
    }

    private fun parseRessources(manifest: Node, publication: Publication){
        val manifestItems = manifest.get("item")
        if (manifestItems.isEmpty())
            return
        for (item in manifestItems){
            val id = item.properties["id"] ?: continue
            val link = linkFromManifest(item)

            // TODO: SMIL for MediaOverlays
//            if (link.typeLink == "application/smil+xml") {
//                val duration = publication.metadata.otherMetadata.first{ it.property == id }
//                        .value?.let { link.duration = Double(smilp.smilTimeToSeconds(duration)) }
//            }
            publication.resources.add(link)
        }
    }

    fun linkFromManifest(item: Node) : Link {
        val link = Link()

        link.title = item.properties["id"]
        link.href = item.properties["href"]
        link.typeLink = item.properties["media-type"]
        item.properties["properties"]?.let {
            val properties = it.split("\\s+")
            with(properties){
                if (contains("nav"))
                    link.rel.add("contents")
                if (contains("cover-image"))
                    link.rel.add("cover")
            }
        }
        return link
    }

}