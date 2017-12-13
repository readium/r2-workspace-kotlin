package org.readium.lcp

data class DRMContext (
    // Hashed passphrase in hex used to generate the user key
    val hashedPassphrase: String,


    // Hex key, encoded with user key, used to decrypt content
    val encryptedContentKey: String,

    // Token that certifies the validity of this DRM context
    val token: String
)