#include <iostream>
#include <chrono>

#include <botan/hash.h>
#include <botan/base64.h>
#include <botan/rsa.h>
#include <botan/ecdsa.h>
#include <botan/hex.h>
#include <botan/data_src.h>
#include <botan/cipher_mode.h>
#include <botan/block_cipher.h>
#include <botan/x509cert.h>
#include <botan/x509_key.h>
#include <botan/x509_crl.h>
#include <botan/pubkey.h>
#include <botan/pk_ops.h>
#include <botan/asn1_time.h>
#include "json11.hpp"

#include "LicenseChecker.h"
#include "LicenseParser.h"
#include "LicenseCanonicalizer.h"
#include "CryptoUtils.h"
#include "CA.h"
#include "DateUtils.h"

namespace lcp
{
    bool LicenseChecker::checkUserKey(const std::string & hexUserKey) {
        // Decode user key HEX representation in binary
        std::vector<uint8_t> userKey = Botan::hex_decode(hexUserKey, false);

        // Decode key check
        Botan::secure_vector<uint8_t> block = Botan::base64_decode(
            m_license.encryption.userKey.keyCheck,
            true
        );

        // Decrypt key check using user key
        std::vector<uint8_t> decryptedIdData = CryptoUtils::decrypt(
            CryptoUtils::unsecureVector(block),
            userKey,
            "AES-256/CBC/NoPadding");

        // Remove padding from decryptedIdData
        if (!CryptoUtils::removeAESCBCPadding(decryptedIdData)) {
            return false;
        }

        // Compare decrypted id to the one embedded in license
        std::string decryptedId(decryptedIdData.begin(), decryptedIdData.end());
        return (decryptedId == m_license.id);
    }

    bool LicenseChecker::checkCertificateSignature()
    {
        // Get CA and license X509 certificates
        auto cert = getLicenseX509Certificate();
        auto caCert = getCAX509Certificate();

        // Check certificate chain
        return cert.check_signature(caCert.subject_public_key());
    }

    bool LicenseChecker::checkCertificateRevocation()
    {
        // Check CRL
        auto cert = getLicenseX509Certificate();
        Botan::DataSource_Memory pemCrlDataSource(m_pemCrl);
        Botan::X509_CRL crl(pemCrlDataSource);
        return !crl.is_revoked(cert);
    }

    bool LicenseChecker::checkSignatureDate()
    {
        // Get license date : updated date if defined otherwise use issued date
        std::string licenseDate = m_license.issued;

        if (!m_license.updated.empty()) {
            licenseDate = m_license.updated;
        }

        // Convert license date to this format YYMMDDHHmmssZ
        // to make it comparable with certificate dates
        std::string formatedLicenseDate = Botan::X509_Time(
            DateUtils::convertISO8601DateToTimePoint(licenseDate)
        ).to_string();

        // Compare to certificate dates
        auto cert = getLicenseX509Certificate();

        return (formatedLicenseDate >= cert.start_time() &&
            formatedLicenseDate <= cert.end_time()
        );
    }

    bool LicenseChecker::checkRights()
    {
        // Get now date to format YYMMDDHHmmssZ
        std::string nowDate = Botan::X509_Time(
            std::chrono::system_clock::now()
        ).to_string();

        if (!m_license.rights.start.empty()) {
            // Convert license start date to format YYMMDDHHmmssZ
            std::string startDate = Botan::X509_Time(
                DateUtils::convertISO8601DateToTimePoint(m_license.rights.start)
            ).to_string();

            if (nowDate < startDate) {
                return false;
            }
        }

        if (!m_license.rights.end.empty()) {
            // Convert license end date to format YYMMDDHHmmssZ
            std::string endDate = Botan::X509_Time(
                DateUtils::convertISO8601DateToTimePoint(m_license.rights.end)
            ).to_string();

            if (nowDate > endDate) {
                return false;
            }
        }

        return true;
    }

    bool LicenseChecker::checkSignature()
    {
        // Extract signature
        Botan::secure_vector<uint8_t> signature = Botan::base64_decode(
            m_license.signature.value,
            true
        );

        // Extract public key from certificate
        auto cert = getLicenseX509Certificate();
        Botan::Public_Key * pubKey = cert.subject_public_key();

        if (pubKey->algo_name() == "ECDSA") {
            // ECDSA public key
            Botan::ECDSA_PublicKey ecdsaPubKey(
                pubKey->algorithm_identifier(),
                pubKey->public_key_bits()
            );

            // Verify signature
            Botan::PK_Verifier verifier(ecdsaPubKey, "EMSA1(SHA-256)");
            verifier.update(m_canonicalJsonLicense);
            return verifier.check_signature(signature);
        }

        // Default use case
        // RSA public key
        Botan::RSA_PublicKey rsaPubKey(
            pubKey->algorithm_identifier(),
            pubKey->public_key_bits()
        );

        // Verify signature
        Botan::PK_Verifier verifier(rsaPubKey, "EMSA3(SHA-256)");
        verifier.update(m_canonicalJsonLicense);
        return verifier.check_signature(signature);
    }

    Botan::X509_Certificate LicenseChecker::getCAX509Certificate()
    {
        auto licenseCert = getLicenseX509Certificate();

        if (licenseCert.subject_public_key()->algo_name() == "ECDSA") {
            // Get ECDSA certificate
            Botan::DataSource_Memory caPemCert(CA_ECDSA_PEM);
            return Botan::X509_Certificate(caPemCert);
        }

        // Default use case
        // RSA certificate
        Botan::DataSource_Memory caPemCert(CA_RSA_PEM);
        return Botan::X509_Certificate(caPemCert);
    }

    Botan::X509_Certificate LicenseChecker::getLicenseX509Certificate()
    {
        // Certificate is not in PEM format
        // Convert it to pem format by adding a begin header and an end footer
        Botan::DataSource_Memory licensePemCert(
            "-----BEGIN CERTIFICATE-----\n" +
            m_license.signature.certificate +
            "\n-----END CERTIFICATE-----"
        );

        // Get X509 certificate
        return Botan::X509_Certificate(licensePemCert);
    }
}
