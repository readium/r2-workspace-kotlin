package org.readium.r2.streamer.Fetcher

import android.renderscript.ScriptGroup
import android.util.Log
import org.readium.r2.shared.Encryption
import org.readium.r2.shared.Publication
import java.nio.channels.SeekableByteChannel
import android.provider.SyncStateContract.Helpers.update
import com.mcxiaoke.koi.HASH
import com.mcxiaoke.koi.ext.toHexBytes
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class Decoder {
    var decodableAlgorithms = mapOf<String, String>(
        "fontIdpf" to "http://www.idpf.org/2008/embedding",
        "fontAdobe" to "http://ns.adobe.com/pdf/enc#RC")
    var decoders = mapOf<String, ObfuscationLength>(
            "http://www.idpf.org/2008/embedding" to ObfuscationLength.Idpf,
    "http://ns.adobe.com/pdf/enc#RC" to ObfuscationLength.Adobe
    )

    enum class ObfuscationLength(int: Int){
        Adobe(int = 1024),
        Idpf(int = 1040)
    }

    fun decoding(input: InputStream, publication: Publication, path: String) : InputStream {
        val publicationIdentifier = publication.metadata.identifier
        val link = publication.linkWithHref(path) ?: return input
        val encryption = link.properties?.encryption ?: return input
        val algorithm = encryption.algorithm ?: return input
        val type = decoders[link.properties?.encryption?.algorithm] ?: return input
        if (!decodableAlgorithms.values.contains(algorithm)){
            Log.e("Error", "$path is encrypted, but can't handle it")
            return input
        }
        return decodingFont(input, publicationIdentifier, type)
    }

    fun decodingFont(input: InputStream, pubId: String, length: ObfuscationLength) : ByteArrayInputStream{
        val publicationKey: ByteArray
        when (length){
            ObfuscationLength.Adobe -> publicationKey = getHashKeyAdobe(pubId)
            ObfuscationLength.Idpf -> publicationKey = HASH.sha1(pubId).toHexBytes()
        }
        return ByteArrayInputStream(ByteArray(0))
    }

    fun getHashKeyAdobe(pubId: String) =
            pubId.replace("urn=uuid", "")
                    .replace("-", "")
                    .toHexBytes()

}