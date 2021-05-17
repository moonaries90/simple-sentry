package com.sentry.demo.service;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpService {

    private final CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(1000)
            .build();

//    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .connectTimeout(Duration.ofSeconds(30))
//            .retryOnConnectionFailure(false)
//            .build();

//    private final HttpClient httpClient;
//
//    {
//        httpClient = new HttpClient();
//        httpClient.getHttpConnectionManager().getParams().setMaxTotalConnections(1000);
//        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
//    }
//

//    public String testOkHttpClient() throws IOException {
//        Response response = okHttpClient.newCall(
//                new Request.Builder()
//                .url("http://www.baidu.com")
//                .header("123", "456")
//                .build()
//        ).execute();
//        return "--> code: " + response.code()
//                + "<--||| --> \r\nresponse: " + Objects.requireNonNull(response.body()).string() + "<--";
//    }

    public String testClient() throws IOException {
        CloseableHttpResponse response = httpClient.execute(new HttpPost("http://www.baidu.com"));
        return "--> code: " + response.getStatusLine().getStatusCode()
                + "<--||| --> \r\nresponse: " + EntityUtils.toString(response.getEntity(), "UTF-8") + "<--";
    }
}
