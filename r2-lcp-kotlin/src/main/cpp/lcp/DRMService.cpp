#include <iostream>

#include <botan/base64.h>
#include <botan/hex.h>

#include "CryptoUtils.h"
#include "DRMService.h"
#include "LicenseChecker.h"

namespace lcp
{
    DRMService::DRMService()
    {
        m_licenseParser = LicenseParser();
        m_licenseCanonicalizer = LicenseCanonicalizer();
    }

    std::string * DRMService::findOneValidPassphrase(
        const std::string & jsonLicense,
        const std::vector<std::string> & hashedPassphraseCollection
    ) {
        // Parse license
        auto license = m_licenseParser.parse(jsonLicense);

        // Check license
        auto licenseChecker = LicenseChecker(
            jsonLicense,
            "",
            license,
            ""
        );

        for (
            std::vector<std::string>::size_type i = 0;
            i < hashedPassphraseCollection.size();
            i++
        ) {
            if (licenseChecker.checkUserKey(hashedPassphraseCollection[i])) {
                return const_cast<std::string*>(&hashedPassphraseCollection[i]);
            }
        }

        return nullptr;
    }

    DRMError DRMService::createContext(
        const std::string & jsonLicense,
        const std::string & hashedPassphrase,
        const std::string & pemCrl,
        DRMContext *& drmContext
    ) {
        // Compute canonical version of json license
        auto canonicalJsonLicense = m_licenseCanonicalizer.canonicalize(
            jsonLicense
        );

        // Parse license
        auto license = m_licenseParser.parse(jsonLicense);

        // Check license
        auto licenseChecker = LicenseChecker(
            jsonLicense,
            canonicalJsonLicense,
            license,
            pemCrl
        );

        // Check certificate signature
        if (!licenseChecker.checkCertificateSignature()) {
            return DRMError(DRMErrorCode::CERTIFICATE_SIGNATURE_INVALID);
        }

        // Check certificate revocation
        if (!licenseChecker.checkCertificateRevocation()) {
            return DRMError(DRMErrorCode::CERTIFICATE_REVOKED);
        }

        // Check signature date
        if (!licenseChecker.checkSignatureDate()) {
            return DRMError(DRMErrorCode::LICENSE_SIGNATURE_DATE_INVALID);
        }

        // Check signature
        if (!licenseChecker.checkSignature()) {
            return DRMError(DRMErrorCode::LICENSE_SIGNATURE_INVALID);
        }

        // Check user key
        if (!licenseChecker.checkUserKey(hashedPassphrase)) {
            return DRMError(DRMErrorCode::USER_KEY_CHECK_INVALID);
        }

        // Check rights
        if (!licenseChecker.checkRights()) {
            return DRMError(DRMErrorCode::LICENSE_OUT_OF_DATE);
        }

        // Initialize drm context
        // Hashed passphrase and encrypted conten key are encoded in hex
        Botan::secure_vector<uint8_t> encryptedContentKey = Botan::base64_decode(
            license.encryption.contentKey.encryptedValue,
            true
        );
        drmContext = new DRMContext(
            hashedPassphrase,
            Botan::hex_encode(encryptedContentKey),
            "" // FIXME: Create a real token
        );

        return DRMError(DRMErrorCode::NONE);
    }

    DRMError DRMService::decrypt(
        const DRMContext & drmContext,
        const std::vector<uint8_t> & encryptedData,
        std::vector<uint8_t> *& decryptedData
    ) {
        // Decode user key HEX representation in binary
        std::vector<uint8_t> userKey = Botan::hex_decode(
            drmContext.hashedPassphrase,
            false
        );

        // Decode HEX representation of encrypted content key
        std::vector<uint8_t> encryptedContentKey = Botan::hex_decode(
            drmContext.encryptedContentKey,
            false
        );

        // Decrypt encrypted content key with user key
        try {
            std::vector<uint8_t> contentKey = CryptoUtils::decrypt(
                encryptedContentKey,
                userKey,
                "AES-256/CBC/NoPadding"
            );

            if (!CryptoUtils::removeAESCBCPadding(contentKey)) {
                // Error while decrypting content
                return DRMError(DRMErrorCode::CONTENT_DECRYPT_ERROR);
            }

            // Decrypt encrypted data with content key
            try {
                decryptedData = new std::vector<uint8_t>(
                    CryptoUtils::decrypt(
                        encryptedData,
                        contentKey,
                        "AES-256/CBC/NoPadding"
                    )
                );
            } catch (const Botan::Invalid_Key_Length & e) {
                return DRMError(DRMErrorCode::CONTENT_DECRYPT_ERROR);
            }
        } catch (const Botan::Invalid_Key_Length & e) {
            return DRMError(DRMErrorCode::CONTENT_KEY_DECRYPT_ERROR);
        }

        return DRMError(DRMErrorCode::NONE);
    }
}
