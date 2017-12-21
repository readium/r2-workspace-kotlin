#ifndef __CRYPTO_UTILS_H__
#define __CRYPTO_UTILS_H__

#include <string>
#include <vector>

#include <botan/block_cipher.h>

namespace lcp
{
    class CryptoUtils
    {
    public:
        /**
            Decrypt content

            @param encrypted Encrypted content
            @param key Key used to decrypt content
            @param cipherMode Cipher mode (e.g. "AES-256/CBC/NoPadding")
            @return Decrypted content
        */
        static std::vector<uint8_t> decrypt(
            const std::vector<uint8_t> &encrypted,
            const std::vector<uint8_t> &key,
            const std::string &cipherMode
        );

        static std::vector<uint8_t> decrypt(
            const Botan::secure_vector<uint8_t> &encrypted,
            const Botan::secure_vector<uint8_t> &key,
            const std::string &cipherMode
        );

        /**
            Convert Botan::secure_vector to std::vector

            @param secured Secured vector
            @return converted std vector
        */
        static std::vector<uint8_t> unsecureVector(
            const Botan::secure_vector<uint8_t> &secured
        );

        /**
            Remove AES/CBC paddinf

            @param data Decrypted content to resize
            @return true if no error detected
        */
        static bool removeAESCBCPadding(std::vector<uint8_t> &data);
    };
}

#endif //__CRYPTO_UTILS_H__
