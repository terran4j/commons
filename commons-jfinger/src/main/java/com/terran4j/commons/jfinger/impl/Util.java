package com.terran4j.commons.jfinger.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.terran4j.commons.jfinger.Encoding;

public class Util {
	
	private final static int DEFAULT_BUFFERSIZE = 1024 * 4;

	private final static int DEFAULT_SLEEP_COUNT = 3;

	public String getString(InputStream in) {
		return this.getString(in, Encoding.getDefaultEncoding());
	}

	public String getString(Throwable t) {
		if (t == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PrintWriter pw = new PrintWriter(out);
			t.printStackTrace(pw);
			pw.flush();
			String str = new String(out.toByteArray(), Encoding.UTF8.getName());
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// ignore the error.
			}
		}
	}

	public String getString(Class<?> clazz, String fileName) {
		String path = null;
		ClassLoader loader = null;
		if (clazz == null) {
			path = fileName;
			loader = getClass().getClassLoader();
		} else {
			path = getClassPath(clazz, fileName);
			loader = clazz.getClassLoader();
		}
		
		InputStream in = loader.getResourceAsStream(path);
		if (in == null) {
			return null;
		}
		
		try {
			String str = getString(in);
			if (str == null) {
				str = "";
			}
			return str;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public InputStream toInputStream(String s) {
		if (s == null) {
			return null;
		}

		try {
			InputStream in = new ByteArrayInputStream(s.getBytes(Encoding.UTF8.getName()));
			return in;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getString(InputStream in, Encoding encoding) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		StringBuilder sb = new StringBuilder();
		try {
			if (encoding == null) {
				encoding = Encoding.getDefaultEncoding();
			}
			InputStreamReader inr = new InputStreamReader(in, encoding.getName());
			BufferedReader reader = new BufferedReader(inr);

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// #1005 去掉in.close()语句。

		return sb.toString();

	}
	
	public long copy(InputStream input, OutputStream output) throws IOException {
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

	public String[] split(String str) {
		return split(str, ",", -1);
	}

	public String[] split(String str, String split) {
		return split(str, split, -1);
	}

	public String[] split(String str, String split, int limit) {
		if (str == null) {
			return null;
		}

		str = str.trim();

		if (str.length() == 0) {
			return new String[0];
		}

		if (split == null || split.length() == 0) {
			return new String[] { str };
		}

		List<String> items = new ArrayList<String>();
		String[] strs = null;
		if (limit > 0) {
			strs = str.split(split, limit);
		} else {
			strs = str.split(split);
		}

		if (strs != null && strs.length > 0) {
			for (String s : strs) {
				if (s == null || s.trim().length() == 0) {
					continue;
				}
				items.add(s.trim());
			}
		}

		String[] ss = new String[items.size()];
		return items.toArray(ss);
	}

	public String[] split(String str, String begin, String end) {
		if (str == null || str.trim().length() == 0 || begin == null || begin.trim().length() == 0 || end == null
				|| end.trim().length() == 0) {
			return null;
		}

		List<String> list = new ArrayList<String>();
		int from = 0;
		while (true) {
			int m = str.indexOf(begin, from);
			int n = str.indexOf(end, from);

			if (m >= 0 && m < str.length() && n > m && n < str.length()) {
				String s = str.substring(m, n + end.length());
				list.add(s);
				from = n + end.length();
			} else {
				break;
			}
		}

		String[] rets = new String[list.size()];
		return list.toArray(rets);
	}

	/**
	 * 根据类对象， 获取同包下的文件的资源路径。
	 * 
	 * @param clazz
	 * @param fileName
	 * @return
	 */
	public String getClassPath(final Class<?> clazz, String fileName) {
		Package classPackage = clazz.getPackage();
		if (classPackage != null) {
			return classPackage.getName().replace('.', '/') + "/" + fileName;
		} else {
			return fileName;
		}

	}

	/**
	 * 
	 */
	public String encode(String str) {
		try {
			return URLEncoder.encode(str, Encoding.UTF8.getName());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Unsupported UTF-8 Encoding");
		}
	}

	public String decode(String str) {
		try {
			return URLDecoder.decode(str, Encoding.UTF8.getName());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Unsupported UTF-8 Encoding");
		}
	}
	
	
}
