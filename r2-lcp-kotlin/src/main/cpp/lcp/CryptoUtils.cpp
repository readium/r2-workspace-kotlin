#include <iostream>
#include <botan/data_src.h>
#include <botan/cipher_mode.h>
#include <botan/block_cipher.h>

#include "CryptoUtils.h"

namespace lcp
{
    std::vector<uint8_t> CryptoUtils::decrypt(
        const std::vector<uint8_t> &encrypted,
        const std::vector<uint8_t> &key,
        const std::string &cipherMode
    ) {
        std::unique_ptr<Botan::Cipher_Mode> dec(Botan::get_cipher_mode(
            cipherMode, Botan::DECRYPTION));
        dec->set_key(key);
        std::vector<uint8_t> iv(
            encrypted.begin(),
            encrypted.begin() + dec->default_nonce_length()
        );
        Botan::secure_vector<uint8_t> data(
            encrypted.begin() + dec->default_nonce_length(),
            encrypted.end()
        );
        dec->start(iv);
        dec->finish(data);

        return std::vector<uint8_t>(data.begin(), data.end());
    }

    std::vector<uint8_t> CryptoUtils::decrypt(
        const Botan::secure_vector<uint8_t> &encrypted,
        const Botan::secure_vector<uint8_t> &key,
        const std::string &cipherMode
    ) {
        return CryptoUtils::decrypt(
            unsecureVector(encrypted),
            unsecureVector(key),
            cipherMode
        );
    }

    std::vector<uint8_t> CryptoUtils::unsecureVector(
        const Botan::secure_vector<uint8_t> &secured
    ) {
        return std::vector<uint8_t>(secured.begin(), secured.end());
    }

    bool CryptoUtils::removeAESCBCPadding(std::vector<uint8_t> &data)
    {
        // Remove padding from data
        int padding = (int) data[data.size()-1];

        if (padding > (int) data.size()) {
            // padding could not be greater than data size
            return false;
        }

        // Resize decrypted data considering the padding
        data.resize(data.size()-padding);
        return true;
    }
}
