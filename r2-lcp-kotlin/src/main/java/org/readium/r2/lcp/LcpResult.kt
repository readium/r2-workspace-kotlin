package org.readium.r2.lcp


data class LcpResult<T> (
    val drmErrorCode: Int,
    val result: T
) {
    val drmError: DRMError
        get() {
            return DRMError.fromCode(drmErrorCode)!!
        }
}