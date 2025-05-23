package com.yourpackage.simpleaac;

import android.content.Context;
import java.io.*;

public class EspeakDataCopier {
    public static void copyEspeakDataIfNeeded(Context context) throws IOException {
        File targetDir = new File(context.getFilesDir(), "espeak-data");
        if (targetDir.exists()) return; // Already copied

        copyAssetDir(context, "espeak-data", targetDir);
    }

    private static void copyAssetDir(Context context, String assetDir, File outDir) throws IOException {
        outDir.mkdirs();
        String[] files = context.getAssets().list(assetDir);
        if (files == null) return;

        for (String file : files) {
            String assetPath = assetDir + "/" + file;
            if (context.getAssets().list(assetPath).length > 0) {
                // Directory
                copyAssetDir(context, assetPath, new File(outDir, file));
            } else {
                // File
                try (InputStream in = context.getAssets().open(assetPath);
                     OutputStream out = new FileOutputStream(new File(outDir, file))) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                }
            }
        }
    }
}