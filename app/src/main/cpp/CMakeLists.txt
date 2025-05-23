cmake_minimum_required(VERSION 3.4.1)

add_library(espeak SHARED IMPORTED)
set_target_properties(espeak PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/../jniLibs/armeabi-v7a/libespeak.so)

add_library(espeak_jni SHARED espeak_jni.cpp)

target_include_directories(espeak_jni PRIVATE
    ${CMAKE_SOURCE_DIR}
    ${CMAKE_SOURCE_DIR}/../cpp)

target_link_libraries(espeak_jni espeak log)