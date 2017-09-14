package org.readium.r2.streamer.Containers

import org.readium.r2shared.RootFile
import java.io.File

class ContainerEpubDirectory(path: String) : EpubContainer, DirectoryContainer {

    override var successCreated: Boolean = false
    lateinit override var rootFile: RootFile

    init {
        if (File(path).exists())
            successCreated = true
        rootFile = RootFile(rootPath = path, version = null)
    }

    override fun data(relativePath: String): ByteArray {
        return super.data(relativePath)
    }
}