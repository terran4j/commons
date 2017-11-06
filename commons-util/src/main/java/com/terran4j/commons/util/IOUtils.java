package com.terran4j.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author wei.jiang
 */
public class IOUtils {

	private final static int DEFAULT_BUFFERSIZE = 1024 * 4;

	private final static int DEFAULT_SLEEP_COUNT = 3;

	public static InputStream getInputStream(Class<?> clazz, String fileName) {
        String path = getClassPath(clazz, fileName);
        InputStream in = clazz.getClassLoader().getResourceAsStream(path);
        return in;
    }

    private static String getClassPath(final Class<?> clazz, String fileName) {
        Package classPackage = clazz.getPackage();
        if (classPackage != null) {
            return classPackage.getName().replace('.', '/') + "/" + fileName;
        } else {
            return fileName;
        }
    }

	public static long copy(InputStream input, OutputStream output) throws IOException {
        // 接口空校验，解决抛出异常，引发异常处理逻辑再次抛出异常而告警的问题
        long count = 0;
        if (input == null || output == null) {
            return count;
        }
        byte[] buffer = new byte[DEFAULT_BUFFERSIZE];
        int n = 0;
        while (true) {
            int read = input.read(buffer);
            if (read < 0) {
                break;
            }
            output.write(buffer, 0, read);
            count += read;
            output.flush();
            n++;
            if (n % DEFAULT_SLEEP_COUNT == 0) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        return count;
    }
	
	public static final byte[] getByteArray(InputStream in) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            copy(in, out);
            byte[] b = out.toByteArray();
            return b;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
