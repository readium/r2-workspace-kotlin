# Global options
cmake_minimum_required(VERSION 3.6.0)
set(CMAKE_VERBOSE_MAKEFILE ON)

set(MODULE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(PROJECT_DIR ${MODULE_DIR})
set(ROOT_DIR ${PROJECT_DIR})
set(VENDOR_DIR ${ROOT_DIR}/vendor)
set(DISTRIBUTION_DIR ${PROJECT_DIR}/dist/${CMAKE_BUILD_TYPE}/${ANDROID_ABI})

# ARM include cpu features
# commented this out, since it looks like is not needed here, but i could be wrong (AM)
#include(AndroidNdkModules)
#android_ndk_import_module_cpufeatures()

if ("${ANDROID_ABI}" STREQUAL "armeabi")
    set(BOTAN_BUILD_DIR "${VENDOR_DIR}/botan/android-armv5")
elseif ("${ANDROID_ABI}" STREQUAL "armeabi-v7a")
    set(BOTAN_BUILD_DIR "${VENDOR_DIR}/botan/android-armv7")
else()
    set(BOTAN_BUILD_DIR "${VENDOR_DIR}/botan/android-x86")
endif()

include_directories(
    # ARM
    # Botan requires sys/auxv.h
    #${ANDROID_NDK}/platforms/android-21/arch-arm/usr/include
    ${BOTAN_BUILD_DIR}/build/include/external
    ${BOTAN_BUILD_DIR}/build/include/
    ${ROOT_DIR}/src/main/cpp/lcp
)

file(GLOB BOTAN_SOURCES ${BOTAN_BUILD_DIR}/*.cpp)
file(GLOB LCP_SOURCES ${ROOT_DIR}/src/main/cpp/lcp/*.cpp)

# Client lib JNI files
file(GLOB LCP_JNI_SOURCES ${MODULE_DIR}/src/main/cpp/*.cpp)

# Extra cmake called before build
if(EXISTS "${EXTRA_CMAKE}")
    include(${EXTRA_CMAKE})
endif()

# Targets to build
add_library("lcp" SHARED
    ${LCP_JNI_SOURCES}
    ${BOTAN_SOURCES}
    ${LCP_SOURCES}
)