// Licensed to the Readium Foundation under one or more contributor license agreements.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation and/or
//    other materials provided with the distribution.
// 3. Neither the name of the organization nor the names of its contributors may be
//    used to endorse or promote products derived from this software without specific
//    prior written permission
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.readium.lcp

class Lcp {
    companion object {
        // Used to load the 'lcp' native library
        init {
            System.loadLibrary("lcp")
        }
    }

    /**
     * Native methods
     */
    external fun nativeFindOneValidPassphrase(
        jsonLicense: String,
        hashedPassphrases: Array<String>
    ): LcpResult<String?>

    external fun nativeCreateContext(
        jsonLicense: String,
        hashedPassphrase: String,
        pemCrl: String
    ): LcpResult<DRMContext?>

    external fun nativeDecrypt(
        drmContext: DRMContext,
        encryptedData: ByteArray
    ): LcpResult<ByteArray?>

    fun findOneValidPassphrase(
        jsonLicense: String,
        hashedPassphrases: Array<String>
    ): String {
        val lcpResult = nativeFindOneValidPassphrase(
            jsonLicense,
            hashedPassphrases
        )

        if (lcpResult.drmError != DRMError.NONE) {
            throw DRMException(lcpResult.drmError)
        }

        return lcpResult.result!!
    }

    fun createContext(
        jsonLicense: String,
        hashedPassphrases: String,
        pemCrl: String
    ): DRMContext {
        val lcpResult = nativeCreateContext(
            jsonLicense,
            hashedPassphrases,
            pemCrl
        )

        if (lcpResult.drmError != DRMError.NONE) {
            throw DRMException(lcpResult.drmError)
        }

        return lcpResult.result!!
    }

    fun decrypt(
        drmContext: DRMContext,
        encryptedData: ByteArray
    ): ByteArray {
        val lcpResult = nativeDecrypt(drmContext, encryptedData)

        if (lcpResult.drmError != DRMError.NONE) {
            throw DRMException(lcpResult.drmError)
        }

        return lcpResult.result!!
    }
}
