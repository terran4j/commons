package com.terran4j.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Files {

    private static final Logger log = LoggerFactory.getLogger(Files.class);
    
    public static String readFile(File file) {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            return Strings.getString(input);
        } catch (Exception e) {
            String msg = String.format("readFile failed, path = %s", file);
            throw new RuntimeException(msg, e);
        }
    }

    public static void writeFile(String content, File file) {
        if (content == null) {
            throw new NullPointerException("writeFile, but content is null.");
        }
        if (file == null) {
            throw new NullPointerException("writeFile, but file is null.");
        }

        if (!file.exists() | file.isDirectory()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                String msg = String.format("createNewFile faild, path = %s", file);
                throw new RuntimeException(msg, e);
            }
        }

        InputStream input = null;
        OutputStream output = null;
        try {
            input = Strings.toInputStream(content);
            output = new FileOutputStream(file);
            long fileSize = IOUtils.copy(input, output);
            log.info("writeFile success, path = {}, size = {}", file.getAbsolutePath(), fileSize);
        } catch (Exception e) {
            String msg = String.format("writeFile faild, path = %s, content:\n%s", file, content);
            throw new RuntimeException(msg, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore.
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // ignore.
                }
            }
        }

    }
}
