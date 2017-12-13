package org.readium.lcp


enum class DRMError(val code: Int) {
    // No error
    NONE(0),

    /**
    WARNING ERRORS > 10
     **/

    // License is out of date (check start and end date)
    LICENSE_OUT_OF_DATE(11),

    // No passhrase matches the license
    LICENSE_NO_PASSHPHRASE_MATCHED(12),

    /**
    CRITICAL ERRORS > 100
     **/

    // Certificate has been revoked in the CRL
    CERTIFICATE_REVOKED(101),

    // Certificate has not been signed by CA
    CERTIFICATE_SIGNATURE_INVALID(102),

    // License has been issued by an expired certificate
    LICENSE_SIGNATURE_DATE_INVALID(111),

    // License signature does not match
    LICENSE_SIGNATURE_INVALID(112),

    // The drm context is invalid
    CONTEXT_INVALID(121),

    // Unable to decrypt encrypted content key from user key
    CONTENT_KEY_DECRYPT_ERROR(131),

    // User key check invalid
    USER_KEY_CHECK_INVALID(141),

    // Unable to decrypt encrypted content from content key
    CONTENT_DECRYPT_ERROR(151),

    // Unknown
    UNKNOWN(500)
    ;

    companion object {
        private val codeMappping = DRMError.values().associateBy(DRMError::code)
        fun fromCode(code: Int) = codeMappping[code]
    }
}