#include "DRMContext.h"

namespace lcp
{
    DRMContext::DRMContext(
        const std::string & hashedPassphrase,
        const std::string & encryptedContentKey,
        const std::string & token
    ) {
        this->hashedPassphrase = hashedPassphrase;
        this->encryptedContentKey = encryptedContentKey;
        this->token = token;
    }
}
