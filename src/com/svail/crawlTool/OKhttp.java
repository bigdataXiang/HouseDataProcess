package com.svail.crawlTool;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by bigdataxiang on 16-12-13.
 */
public class OKhttp {
    public String fetchUrl(String url,String charset)throws Exception{
        OkHttpClient client = new OkHttpClient.Builder().build();

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();

        byte[] responseBytes=response.body().bytes();
        String responseUrl = new String(responseBytes,charset);

        return responseUrl;
    }
}
