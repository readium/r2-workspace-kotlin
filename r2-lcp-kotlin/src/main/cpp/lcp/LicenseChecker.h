#ifndef __LICENSE_CHECKER_H__
#define __LICENSE_CHECKER_H__

#include <string>

#include <botan/x509cert.h>

#include "License.h"
#include "LicenseCanonicalizer.h"
#include "LicenseParser.h"

namespace lcp
{
    class LicenseChecker
    {
    public:
        LicenseChecker(
            const std::string &jsonLicense,
            const std::string &canonicalJsonLicense,
            const License &license,
            const std::string & pemCrl
        ) :
            m_jsonLicense(jsonLicense),
            m_canonicalJsonLicense(canonicalJsonLicense),
            m_license(license),
            m_pemCrl(pemCrl)
        {}

        /**
            Check if user key is valid

            @param hexUserKey User key (hashed passphrase) in hexadecimal
            @return True if user key is valid otherwise false
        */
        bool checkUserKey(const std::string & hexUserKey);

        /**
            Check CRL

            @return True if certificate is not revoked
        */
        bool checkCertificateRevocation();

         /**
            Check certificate signature:
            License update or issued date must match the certificate start
            and end dates

            @return True if certificate is signed by CA
        */
        bool checkCertificateSignature();

        /**
            Check license signature date:
            Is the license is signed by a non expired certificate?

            @return True if license has been signed by a non expired certificate
        */
        bool checkSignatureDate();


        /**
            Check if license signature is valid

            @param string serialized license
            @return True if license signature is valid
        */
        bool checkSignature();

        /**
            Check if license is not expired

            @return True if license is not expired
        */
        bool checkRights();

    private:
        /**
            Returns an X509 certificate from license

            @return certificate
        */
        Botan::X509_Certificate getLicenseX509Certificate();

        /**
            Returns the CA X509 certificate

            @return Botan::X509_Certificate X509 certificate
        */
        Botan::X509_Certificate getCAX509Certificate();

        std::string m_jsonLicense;
        std::string m_canonicalJsonLicense;
        License m_license;
        std::string m_pemCrl;
    };
}

#endif //__LICENSE_CHECKER_H__
