#include <iostream>

#include "json11.hpp"

#include "LicenseCanonicalizer.h"

namespace lcp
{
    std::string LicenseCanonicalizer::canonicalize(const std::string &jsonLicense)
    {
        std::string err;
        const auto jsonObject = json11::Json::parse(jsonLicense, err);

        if (!err.empty()) {
            throw std::logic_error("Unable to parse license: " + err);
        }

        // Remove signature before computing canonical form of license
        auto jsonMap = jsonObject.object_items();
        jsonMap.erase("signature");

        json11::Json newJsonObject(jsonMap);

        // Dump to string
        return newJsonObject.dump();
    }
}
