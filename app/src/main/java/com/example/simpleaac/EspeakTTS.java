package com.example.simpleaac;

import android.content.Context;

public class EspeakTTS {
    static {
        System.loadLibrary("espeak");
        System.loadLibrary("espeak_jni");
    }

    public native void nativeEspeakInit(String dataPath);
    public native void nativeEspeakSpeak(String text);

    public void init(Context context) {
        String dataPath = context.getFilesDir().getAbsolutePath() + "/espeak-data";
        nativeEspeakInit(dataPath);
    }

    public void speak(String text) {
        nativeEspeakSpeak(text);
    }
}