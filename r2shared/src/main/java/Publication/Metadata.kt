package Publication

import Publication.Metadata.Contributor
import Publication.Metadata.MetadataItem
import Publication.Link.Rendition
import Publication.Metadata.Subject
import java.util.*

/**
 * Created by cbaumann on 30/08/2017.
 */

class Metadata{

    /// The structure used for the serialisation.
    var multilangTitle: String = ""//MultilangString?
    /// The title of the publication.
    var title: String = ""//{
        //get {
          //  return multilangTitle?.singleString ?? ""
        //}
    //}

    var languages: MutableList<String> = mutableListOf<String>()
    var identifier: String? = null
    // Contributors.
    var authors = mutableListOf<Contributor>()
    var translators = mutableListOf<Contributor>()
    var editors = mutableListOf<Contributor>()
    var artists = mutableListOf<Contributor>()
    var illustrators = mutableListOf<Contributor>()
    var letterers = mutableListOf<Contributor>()
    var pencilers = mutableListOf<Contributor>()
    var colorists = mutableListOf<Contributor>()
    var inkers = mutableListOf<Contributor>()
    var narrators = mutableListOf<Contributor>()
    var imprints = mutableListOf<Contributor>()
    var direction = "default"
    var subjects = mutableListOf<Subject>()
    var publishers = mutableListOf<Contributor>()
    var contributors = mutableListOf<Contributor>()
    var modified: Date? = null
    var publicationDate: String? = null
    var description: String? = null
    var rendition = Rendition()
    var source: String? = null
    var epubType = mutableListOf<String>()
    var rights: String? = null
    var otherMetadata = mutableListOf<MetadataItem>()
}