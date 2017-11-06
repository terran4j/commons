package com.terran4j.commons.util.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Cookie Util
 */
public class Cookies {

    /**
     * LOG
     */
	private static final Logger log = LoggerFactory.getLogger(Cookies.class);

    /**
     * key value spliter
     */
    public static final String EQUALS = "=";

    /**
     * cookie secure
     */
    public static final String COOKIE_SECURE = "Secure";

    /**
     * set cookie http only
     */
    public static final String COOKIE_HTTP_ONLY = "HttpOnly";

    /**
     * set cookie identifier in response header
     */
    public static final String SET_COOKIE = "Set-Cookie";

    /**
     * Max-Age
     */
    public static final String MAX_AGE = "Max-Age";

    /**
     * COOKIE_SPLITER
     */
    public static final String COOKIE_SPLITER = "; ";

    /**
     * setCookie
     * 
     * @param response
     *            response
     * @param cookieValues
     *            cookieValues
     */
    public static void setCookie(HttpServletResponse response,
            Map<String, String> cookieValues) {
        setCookie(response, cookieValues, -1);
    }

    /**
     * setCookie
     * 
     * @param response
     *            response
     * @param cookieValues
     *            cookieValues
     */
    public static void setCookie(HttpServletResponse response, String key,
            String value, int maxAge) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        Map<String, String> cookieValues = new HashMap<String, String>();
        cookieValues.put(key, value);
        setCookie(response, cookieValues, maxAge);
    }

    /**
     * set cookie
     * 
     * @param response
     *            HttpServletResponse
     * @param cookieValues
     *            cookieValue map
     * @param maxAge
     *            cookie survive time -1:survived until closing explorer
     *            0:remove this cookie int:the seconds of cookie surviving
     */
    public static void setCookie(HttpServletResponse response,
            Map<String, String> cookieValues, int maxAge) {
    		if (cookieValues == null) {
    			throw new NullPointerException("cookieValues is null.");
    		}
        Iterator<String> it = cookieValues.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key == null) {
                continue;
            }

            String value = cookieValues.get(key);
            if (value != null) {
                Cookie cookieItem = new Cookie(key, value);
                cookieItem.setPath("/");
                cookieItem.setMaxAge(maxAge);
                response.addCookie(cookieItem);
                if (log.isInfoEnabled()) {
                    log.info("addCookie, key = " + key + ", value = " + value);
                }
            } else {
                Cookie cookieItem = new Cookie(key, null);
                cookieItem.setPath("/");
                cookieItem.setMaxAge(0);
                response.addCookie(cookieItem);
                if (log.isInfoEnabled()) {
                    log.info("removeCookie, key = " + key);
                }
            }
        }
    }

    /**
     * remove the cookie named cookieName
     * 
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            cookieName
     */
    public static void removeCookie(HttpServletResponse response,
            String cookieName) {
    		if (StringUtils.isEmpty(cookieName)) {
			throw new NullPointerException("cookieName is empty.");
		}
        Map<String, String> removeCookie = new HashMap<String, String>();
        removeCookie.put(cookieName, null);
        setCookie(response, removeCookie, 0);

    }

    /**
     * get the cookie named cookie name
     * 
     * @param request
     *            HttpServletRequest
     * @param cookieName
     *            cookieName
     * @return cookie value
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {
        if (request == null) {
        		throw new NullPointerException("request is null.");
        }
        if (StringUtils.isEmpty(cookieName)) {
	    		throw new NullPointerException("cookieName is empty.");
	    }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
