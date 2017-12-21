#include <iostream>
#include <chrono>

#include "json11.hpp"

#include "LicenseParser.h"
#include "DateUtils.h"

namespace lcp
{
    License LicenseParser::parse(const std::string jsonLicense)
    {
        // Parse json
        std::string err;
        const auto jsonObject = json11::Json::parse(jsonLicense, err);

        if (!err.empty()) {
            throw std::logic_error("Unable to parse license: " + err);
        }

        // Deserialize json to License object
        License license = License();
        parseCore(jsonObject, license);
        return license;
    }

    void LicenseParser::parseCore(
        json11::Json jsonObject,
        License & license
    ) {
        if (jsonObject["id"].is_null()) {
            throw std::logic_error("id value cannot not be null");
        }

        if (jsonObject["provider"].is_null()) {
            throw std::logic_error("provider value cannot not be null");
        }

        if (jsonObject["issued"].is_null()) {
            throw std::logic_error("issued value cannot not be null");
        }

        if (jsonObject["encryption"].is_null()) {
            throw std::logic_error("encryption value cannot not be null");
        }

        license.id = jsonObject["id"].string_value();
        license.provider = jsonObject["provider"].string_value();
        license.issued = jsonObject["issued"].string_value();

        // Test issued date
        DateUtils::convertISO8601DateToTimePoint(license.issued);

        if (!jsonObject["updated"].is_null()) {
            license.updated = jsonObject["updated"].string_value();

            // Test updated date
            DateUtils::convertISO8601DateToTimePoint(license.updated);
        }

        parseSignature(jsonObject["signature"], license);
        parseEncryption(jsonObject["encryption"], license);
        parseRights(jsonObject["rights"], license);
    }

    void LicenseParser::parseEncryption(
        json11::Json jsonObject,
        License & license
    ) {
        if (jsonObject["profile"].is_null()) {
            throw std::logic_error("encryption/profile value cannot not be null");
        }

        if (jsonObject["content_key"].is_null()) {
            throw std::logic_error("encryption/content_key value cannot not be null");
        }

        if (jsonObject["user_key"].is_null()) {
            throw std::logic_error("encryption/user_key value cannot not be null");
        }

        license.encryption.profile = jsonObject["profile"].string_value();

        parseUserKey(jsonObject["user_key"], license);
        parseContentKey(jsonObject["content_key"], license);
    }

    void LicenseParser::parseUserKey(
        json11::Json jsonObject,
        License & license
    ) {
        if (jsonObject["key_check"].is_null()) {
            throw std::logic_error("encryption/user_key/key_check value cannot not be null");
        }

        if (jsonObject["algorithm"].is_null()) {
            throw std::logic_error("encryption/user_key/algorithm value cannot not be null");
        }

        if (jsonObject["text_hint"].is_null()) {
            throw std::logic_error("encryption/user_key/text_hint value cannot not be null");
        }

        license.encryption.userKey.keyCheck = jsonObject["key_check"].string_value();
        license.encryption.userKey.algorithm = jsonObject["algorithm"].string_value();
        license.encryption.userKey.textHint = jsonObject["text_hint"].string_value();
    }

    void LicenseParser::parseContentKey(
        json11::Json jsonObject,
        License & license
    ) {
        if (jsonObject["encrypted_value"].is_null()) {
            throw std::logic_error("encryption/content_key/encrypted_value value cannot not be null");
        }

        if (jsonObject["algorithm"].is_null()) {
            throw std::logic_error("encryption/content_key/algorithm value cannot not be null");
        }

        license.encryption.contentKey.encryptedValue = jsonObject["encrypted_value"].string_value();
        license.encryption.contentKey.algorithm = jsonObject["algorithm"].string_value();
    }

    void LicenseParser::parseSignature(
        json11::Json jsonObject,
        License & license
    ) {
        if (jsonObject["algorithm"].is_null()) {
            throw std::logic_error("signature/algorithm value cannot not be null");
        }

        if (jsonObject["certificate"].is_null()) {
            throw std::logic_error("signature/certificate value cannot not be null");
        }

        if (jsonObject["value"].is_null()) {
            throw std::logic_error("signature/value cannot not be null");
        }

        license.signature.algorithm = jsonObject["algorithm"].string_value();
        license.signature.certificate = jsonObject["certificate"].string_value();
        license.signature.value = jsonObject["value"].string_value();
    }

    void LicenseParser::parseRights(
        json11::Json jsonObject,
        License & license
    ) {
        // Parse start date
        if (!jsonObject["start"].is_null()) {
            license.rights.start = jsonObject["start"].string_value();

            // Test start date
            DateUtils::convertISO8601DateToTimePoint(license.rights.start);
        }

        // Parse end date
        if (!jsonObject["end"].is_null()) {
            license.rights.end = jsonObject["end"].string_value();

            // Test end date
            DateUtils::convertISO8601DateToTimePoint(license.rights.end);
        }
    }
}
