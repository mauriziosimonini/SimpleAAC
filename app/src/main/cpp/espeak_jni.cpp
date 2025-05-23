#include <jni.h>
#include <string>
#include "speak_lib.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_simpleaac_EspeakTTS_nativeEspeakInit(JNIEnv *env, jobject thiz, jstring dataPath) {
    const char *path = env->GetStringUTFChars(dataPath, 0);
    espeak_Initialize(AUDIO_OUTPUT_PLAYBACK, 0, path, 0);
    env->ReleaseStringUTFChars(dataPath, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_simpleaac_EspeakTTS_nativeEspeakSpeak(JNIEnv *env, jobject thiz, jstring text) {
    const char *nativeText = env->GetStringUTFChars(text, 0);
    espeak_Synth(nativeText, strlen(nativeText) + 1, 0, POS_CHARACTER, 0, espeakCHARS_AUTO, NULL, NULL);
    espeak_Synchronize();
    env->ReleaseStringUTFChars(text, nativeText);
}