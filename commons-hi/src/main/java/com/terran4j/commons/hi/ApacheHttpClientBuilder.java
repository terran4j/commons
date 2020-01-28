package com.terran4j.commons.hi;

import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ApacheHttpClientBuilder {

    private static final Logger log = LoggerFactory.getLogger(ApacheHttpClientBuilder.class);

    public static final String CHARSET = "UTF-8";

    public static final HttpClient build(int timeout, String charset) {
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true).build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout).setSocketTimeout(timeout)
                .build();
        List<Header> defaultHeaders = Lists.newArrayList();
        defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, charset));

        return HttpClients.custom().setDefaultHeaders(defaultHeaders)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultSocketConfig(socketConfig)
                .build();
    }


    public static class DefaultResponseHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(HttpResponse httpResponse) throws IOException {
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() >= 300) {
                log.error("Http request failed, statusCode = {}, reasonPhrase = {}",
                        statusLine.getStatusCode(), statusLine.getReasonPhrase());
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }

            if (httpResponse.getEntity() == null) {
                throw new ClientProtocolException("Response contains no content");
            }

            // get charset
            Charset charset = Charset.forName(CHARSET);
            // 有时第三方API返回的 contentType 也不一定对。
//            ContentType responseContentType = ContentType.get(httpResponse.getEntity());
//            if (responseContentType != null) {
//                charset = responseContentType.getCharset();
//            }

            String content = EntityUtils.toString(httpResponse.getEntity(), charset);
            return content;
        }
    }

}
