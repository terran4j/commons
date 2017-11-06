package com.terran4j.commons.util.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class IPAddresses {

	private static final Logger log = LoggerFactory.getLogger(IPAddresses.class);

	/**
	 * 获取本地ip地址，有可能会有多个地址, 若有多个网卡则会搜集多个网卡的ip地址
	 */
	public static Set<InetAddress> resolveLocalAddresses() {
		Set<InetAddress> addrs = new HashSet<InetAddress>();
		Enumeration<NetworkInterface> ns = null;
		try {
			ns = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// ignored...
		}
		while (ns != null && ns.hasMoreElements()) {
			NetworkInterface n = ns.nextElement();
			Enumeration<InetAddress> is = n.getInetAddresses();
			while (is.hasMoreElements()) {
				InetAddress i = is.nextElement();
				if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
						&& !isSpecialIp(i.getHostAddress()))
					addrs.add(i);
			}
		}
		return addrs;
	}

	public static String resolveLocalIp() {
		Set<InetAddress> addrs = resolveLocalAddresses();
		for (InetAddress addr : addrs) {
			return addr.getHostAddress();
		}
		return "";
	}

	public static Set<String> resolveLocalIps() {
		Set<InetAddress> addrs = resolveLocalAddresses();
		Set<String> ret = new HashSet<String>();
		for (InetAddress addr : addrs)
			ret.add(addr.getHostAddress());
		return ret;
	}

	private static boolean isSpecialIp(String ip) {
		if (ip.contains(":"))
			return true;
		if (ip.startsWith("127."))
			return true;
		if (ip.startsWith("169.254."))
			return true;
		if (ip.equals("255.255.255.255"))
			return true;
		return false;
	}

	public static String getLocalHostName() {
		String hostname = System.getenv("HOSTNAME");
		if (StringUtils.isEmpty(hostname)) {
			try {
				InputStream in;
				Process pro = Runtime.getRuntime().exec("hostname");
				pro.waitFor();
				in = pro.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				hostname = read.readLine();
			} catch (IOException e) {
				log.error("getLocalHostName IOException");
			} catch (InterruptedException e) {
				log.error("getLocalHostName InterruptedException");
			}
		}
		return hostname;
	}
	
	/**
	 * 将ip转换为定长8个字符的16进制表示形式：255.255.255.255 -> FFFFFFFF
	 * @param ip
	 * @return
	 */
	public static String hex2IP(String ip) {
		StringBuilder sb = new StringBuilder();
		for (String seg : ip.split("\\.")) {
			String h = Integer.toHexString(Integer.parseInt(seg));
			if (h.length() == 1)
				sb.append("0");
			sb.append(h);
		}
		return sb.toString();
	}
	
}
