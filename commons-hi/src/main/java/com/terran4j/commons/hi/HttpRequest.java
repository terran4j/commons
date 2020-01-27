package com.terran4j.commons.hi;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final String url;

    private final Map<String, String> headers = new HashMap<>();

    private final Map<String, String> params = new HashMap<>();

    private StringBuffer content = new StringBuffer();

    private RequestMethod method = RequestMethod.GET;

    private String postBody;

    private String responseBody;

    public RequestMethod getMethod() {
        return method;
    }

    public HttpRequest setMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    private static HttpClient httpClient = ApacheHttpClientBuilder
            .build(1000 * 60 * 10, ApacheHttpClientBuilder.CHARSET);


    public HttpRequest(String url) {
        super();
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public HttpRequest setParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public HttpRequest setParam(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public HttpRequest setContent(String content) {
        this.content = new StringBuffer(content);
        return this;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public HttpRequest setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpRequest setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public static final String toXml(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            sb.append("\t<").append(key).append(">")
                    .append("<![CDATA[").append(value).append("]]>")
                    .append("</").append(key).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    private static final void addParams(URIBuilder uriBuilder, Map<String, String> params) {
        if (params.size() > 0) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                uriBuilder.addParameter(key, value);
            }
        }
    }

    public String execute() throws HttpException {
        HttpUriRequest httpRequest = null;
        try {
            if (method == RequestMethod.GET) {
                URIBuilder uriBuilder = new URIBuilder(url);
                addParams(uriBuilder, params);
                URI uri = uriBuilder.build();
                final HttpGet httpGet = new HttpGet(uri);
                httpRequest = httpGet;
            } else if (method == RequestMethod.POST) {
                final HttpPost httpPost = new HttpPost(url);
                if (this.content.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.content);
                    String content = sb.toString();
                    if (log.isInfoEnabled()) {
                        log.info("Http Post Content:\n{}", content);
                    }
                    ContentType contentType = ContentType.TEXT_PLAIN
                            .withCharset(Charset.forName(ApacheHttpClientBuilder.CHARSET));
                    httpPost.setEntity(new StringEntity(content, contentType));
                    System.out.println(httpPost);
                } else if (MapUtils.isNotEmpty(params)) {
                    ContentType contentType = null;
                    StringBuilder sb = new StringBuilder();
                    if ("XML".equalsIgnoreCase(postBody)) {
                        sb.append(toXml(params));
                        contentType = ContentType.TEXT_XML
                                .withCharset(Charset.forName(ApacheHttpClientBuilder.CHARSET));
                    } else {
                        sb.append(toUrlQuery(params));
                        contentType = ContentType.APPLICATION_FORM_URLENCODED
                                .withCharset(Charset.forName(ApacheHttpClientBuilder.CHARSET));
                    }
                    String content = sb.toString();
                    if (log.isInfoEnabled()) {
                        log.info("Http Post Body:\n{}", content);
                    }
                    httpPost.setEntity(new StringEntity(content, contentType));
                }
                httpRequest = httpPost;
            } else if (method == RequestMethod.PUT) {
                URIBuilder uriBuilder = new URIBuilder(url);
                addParams(uriBuilder, params);
                URI uri = uriBuilder.build();
                final HttpPut httpPut = new HttpPut(uri);
                httpRequest = httpPut;
            } else if (method == RequestMethod.DELETE) {
                URIBuilder uriBuilder = new URIBuilder(url);
                addParams(uriBuilder, params);
                URI uri = uriBuilder.build();
                final HttpDelete httpDelete = new HttpDelete(uri);
                httpRequest = httpDelete;
            }
        } catch (URISyntaxException e) {
            throw new HttpException(HttpErrorCode.URI_SYNTAX_ERROR, e)
                    .put("url", url).put("params", params)
                    .as(HttpException.class);
        }
        if (httpRequest == null) {
            throw new HttpException(HttpErrorCode.UNSUPPORTED_METHOD)
                    .put("method", method)
                    .put("supportedMethods", new RequestMethod[]{
                            RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST
                    })
                    .setMessage("NOT supported method: ${method}")
                    .as(HttpException.class);
        }

        if (headers.size() > 0) {
            Iterator<String> it = headers.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = headers.get(key);
                httpRequest.addHeader(key, value);
            }
        }

        try {
            long t0 = System.currentTimeMillis();
            String response = httpClient.execute(httpRequest,
                    new ApacheHttpClientBuilder.DefaultResponseHandler());
            long t = System.currentTimeMillis() - t0;
            if (log.isInfoEnabled()) {
                log.info("request spend {}ms, response:\n{}", t, response);
            }
            return response;
        } catch (Exception e) {
            log.error("http failed: " + e.getMessage(), e);
            throw new HttpException(HttpErrorCode.HTTP_REQUEST_ERROR, e)
                    .put("curl", toCurl(httpRequest))
                    .as(HttpException.class);
        }
    }

    public static final String toUrlQuery(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            if (!first) {
                sb.append("&");
            }
            sb.append(key).append("=").append(encode(value));
            first = false;
        }
        return sb.toString();
    }


    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, ApacheHttpClientBuilder.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toCurl(HttpUriRequest request) {
        StringBuilder sb = new StringBuilder("curl");
        if (headers.size() > 0) {
            Iterator<String> it = headers.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = headers.get(key);
                sb.append(" -H \"").append(key).append(": ").append(value).append("\"");
            }
        }
        if (request instanceof HttpPost) {
            if (params.size() > 0) {
                sb.append(" -d \"").append(toUrlQuery(params)).append("\"");
            }
        }

        sb.append(" \"").append(url);
        if (request instanceof HttpGet) {
            if (params.size() > 0) {
                sb.append("?").append(toUrlQuery(params));
            }
        }
        sb.append("\"");

        return sb.toString();
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}