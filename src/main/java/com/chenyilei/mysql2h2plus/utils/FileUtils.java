package com.chenyilei.mysql2h2plus.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public abstract class FileUtils {

    public static final int BUFFER_SIZE = 4096;

    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    public static String copyToString(InputStream in, Charset charset) {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        try (InputStream stream = new BufferedInputStream(in);
             InputStreamReader reader = new InputStreamReader(stream, charset)) {
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String copyToString(File in, Charset charset) {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        try (FileInputStream stream = new FileInputStream(in);
             InputStreamReader reader = new InputStreamReader(stream, charset)) {
            char[] buffer = new char[4096];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void copyToFile(byte[] in, File file) {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            outputStream.write(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
