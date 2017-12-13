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

#include <string>
#include <vector>

#include "DRMService.h"
#include "lcp.h"

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    // Get jclass with env->FindClass.
    // Register methods with env->RegisterNatives.
    return JNI_VERSION_1_6;
}

JNIEXPORT jobject
JNICALL Java_org_readium_lcp_sdk_Lcp_nativeFindOneValidPassphrase(
        JNIEnv *env,
        jobject jThis,
        jstring jJsonLicense,
        jobjectArray jHashedPassphrases
) {
    std::vector<std::string> hashedPassphraseCollection;

    for (int i=0; i<env->GetArrayLength(jHashedPassphrases); i++) {
        jstring jHashedPassphrase = (jstring) env->GetObjectArrayElement(
                jHashedPassphrases, i
        );
        const char * cHashedPassphrase = env->GetStringUTFChars(
                jHashedPassphrase, NULL
        );
        hashedPassphraseCollection.push_back(std::string(cHashedPassphrase));
        env->ReleaseStringUTFChars(jHashedPassphrase, cHashedPassphrase);
    }

    const char * cJsonLicense = env->GetStringUTFChars(
            jJsonLicense, NULL
    );
    std::string jsonLicense(cJsonLicense);

    // Search for a passphrase that matches the user key check
    auto drmService = lcp::DRMService();
    int errorCode = 0;
    std::string *foundHashedPassphrase;

    try {
        foundHashedPassphrase = drmService.findOneValidPassphrase(
                jsonLicense,
                hashedPassphraseCollection
        );
    } catch (const std::exception& ex) {
        // Unknown error
        errorCode = 500;
    }

    env->ReleaseStringUTFChars(jJsonLicense, cJsonLicense);

    jclass lcpResultclass = env->FindClass(
            "org/readium/lcp/sdk/LcpResult"
    );
    jmethodID lcpResultInitMethod = env->GetMethodID(
            lcpResultclass,
            "<init>",
            "(ILjava/lang/Object;)V"
    );

    if (errorCode == 0 && foundHashedPassphrase == nullptr) {
        errorCode = 12;
    }

    if (errorCode > 0) {
        return env->NewObject(
                lcpResultclass,
                lcpResultInitMethod,
                (jint) errorCode,
                (jobject) nullptr
        );
    }

    // Returns found hashedPassphrase
    jstring jFoundHashedPassphrase = env->NewStringUTF(
            foundHashedPassphrase->c_str()
    );
    return env->NewObject(
            lcpResultclass,
            lcpResultInitMethod,
            (jint) errorCode,
            (jobject) jFoundHashedPassphrase
    );
}

JNIEXPORT jobject
JNICALL Java_org_readium_lcp_sdk_Lcp_nativeCreateContext(
        JNIEnv *env,
        jobject jThis,
        jstring jJsonLicense,
        jstring jHashedPassphrase,
        jstring jPemCrl
) {
    // Convert jni parameters to C++ types
    const char * cJsonLicense = env->GetStringUTFChars(
            jJsonLicense, NULL
    );
    std::string jsonLicense(cJsonLicense);
    const char * cHashedPassphrase = env->GetStringUTFChars(
            jHashedPassphrase, NULL
    );
    std::string hashedPassphrase(cHashedPassphrase);
    const char * cPemCrl = env->GetStringUTFChars(
            jPemCrl, NULL
    );
    std::string pemCrl(cPemCrl);

    // Create context from given license
    auto drmService = lcp::DRMService();
    lcp::DRMContext *drmContext;
    int errorCode = 0;

    try {
        auto error = drmService.createContext(
                jsonLicense,
                hashedPassphrase,
                pemCrl,
                drmContext
        );
        errorCode = (int) error.getCode();
    } catch (const std::exception& ex) {
        // Unknown error
        errorCode = 500;
    }

    // Free memory
    env->ReleaseStringUTFChars(jJsonLicense, cJsonLicense);
    env->ReleaseStringUTFChars(jHashedPassphrase, cHashedPassphrase);

    // Returns result
    jclass lcpResultclass = env->FindClass(
            "org/readium/lcp/sdk/LcpResult"
    );
    jmethodID lcpResultInitMethod = env->GetMethodID(
            lcpResultclass,
            "<init>",
            "(ILjava/lang/Object;)V"
    );

    if (errorCode > 0) {
        return env->NewObject(
                lcpResultclass,
                lcpResultInitMethod,
                (jint) errorCode,
                (jobject) nullptr
        );
    }

    // Build DRMContext java object
    jclass drmContextClass = env->FindClass(
            "org/readium/lcp/sdk/DRMContext"
    );
    jmethodID drmContextInitMethod = env->GetMethodID(
            drmContextClass,
            "<init>",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
    );
    jstring jNewHashedPassphrase = env->NewStringUTF(
            drmContext->hashedPassphrase.c_str()
    );
    jstring jEncryptedContentKey = env->NewStringUTF(
            drmContext->encryptedContentKey.c_str()
    );
    jstring jToken = env->NewStringUTF(
            drmContext->token.c_str()
    );
    jobject jDrmContext = env->NewObject(
            drmContextClass,
            drmContextInitMethod,
            (jobject) jNewHashedPassphrase,
            (jobject) jEncryptedContentKey,
            (jobject) jToken
    );

    // Returns DRM context
    return env->NewObject(
            lcpResultclass,
            lcpResultInitMethod,
            (jint) 0,
            (jobject) jDrmContext
    );
}

JNIEXPORT jobject
JNICALL Java_org_readium_lcp_sdk_Lcp_nativeDecrypt(
        JNIEnv *env,
        jobject jThis,
        jobject jDrmContext,
        jbyteArray jEncryptedData
) {
    // Extract hashedPassphrase, encryptedContentKey and token from DRMContext
    // java object
    jclass drmContextClass = env->FindClass(
            "org/readium/lcp/sdk/DRMContext"
    );

    // HashedPassphrase
    jfieldID drmContextHashedPassphraseField = env->GetFieldID(
            drmContextClass,
            "hashedPassphrase",
            "Ljava/lang/String;"
    );
    jstring jHashedPassphrase = (jstring) env->GetObjectField(
            jDrmContext,
            drmContextHashedPassphraseField
    );
    const char * cHashedPassphrase = env->GetStringUTFChars(
            jHashedPassphrase, NULL
    );
    std::string hashedPassphrase(cHashedPassphrase);

    // Encrypted content key
    jfieldID drmContextEncryptedContentKeyField = env->GetFieldID(
            drmContextClass,
            "encryptedContentKey",
            "Ljava/lang/String;"
    );
    jstring jEncryptedContentKey = (jstring) env->GetObjectField(
            jDrmContext,
            drmContextEncryptedContentKeyField
    );
    const char * cEncryptedContentKey = env->GetStringUTFChars(
            jEncryptedContentKey, NULL
    );
    std::string encryptedContentKey(cEncryptedContentKey);

    // DRM context token
    jfieldID drmContextTokenField = env->GetFieldID(
            drmContextClass,
            "token",
            "Ljava/lang/String;"
    );
    jstring jDrmContextToken = (jstring) env->GetObjectField(
            jDrmContext,
            drmContextTokenField
    );
    const char * cDrmContextToken = env->GetStringUTFChars(
            jDrmContextToken, NULL
    );
    std::string drmContextToken(cDrmContextToken);

    // Get token
    auto drmContext = lcp::DRMContext(
            hashedPassphrase,
            encryptedContentKey,
            drmContextToken
    );

    // Get encrypted data
    jsize encryptedDataSize = env->GetArrayLength(jEncryptedData);
    std::vector<uint8_t> encryptedData(encryptedDataSize);
    jbyte* jEncryptedDataBytes = env->GetByteArrayElements(
            jEncryptedData,
            NULL
    );

    for (int i=0; i<encryptedDataSize; i++) {
        encryptedData[i] = jEncryptedDataBytes[i];
    }

    // Decrypt data
    auto drmService = lcp::DRMService();
    std::vector<uint8_t> *decryptedData;
    int errorCode = 0;

    try {
        auto error = drmService.decrypt(
                drmContext,
                encryptedData,
                decryptedData
        );
        errorCode = (int) error.getCode();
    } catch (const std::exception& ex) {
        // Unknown error
        errorCode = 500;
    }

    // Free memory
    env->ReleaseStringUTFChars(jHashedPassphrase, cHashedPassphrase);
    env->ReleaseStringUTFChars(jEncryptedContentKey, cEncryptedContentKey);
    env->ReleaseStringUTFChars(jDrmContextToken, cDrmContextToken);

    // Returns result
    jclass lcpResultclass = env->FindClass(
            "org/readium/lcp/sdk/LcpResult"
    );
    jmethodID lcpResultInitMethod = env->GetMethodID(
            lcpResultclass,
            "<init>",
            "(ILjava/lang/Object;)V"
    );

    if (errorCode > 0) {
        return env->NewObject(
                lcpResultclass,
                lcpResultInitMethod,
                (jint) errorCode,
                (jobject) nullptr
        );
    }

    jbyteArray jDecryptedData = env->NewByteArray(decryptedData->size());
    env->SetByteArrayRegion(
            jDecryptedData,
            0,
            decryptedData->size(),
            (jbyte*) decryptedData->data()
    );

    // Free pointer
    delete decryptedData;

    // Returns LCP result
    return env->NewObject(
            lcpResultclass,
            lcpResultInitMethod,
            (jint) errorCode,
            (jobject) jDecryptedData
    );
}
