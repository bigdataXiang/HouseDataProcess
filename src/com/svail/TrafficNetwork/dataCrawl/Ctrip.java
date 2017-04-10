package com.svail.TrafficNetwork.dataCrawl;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
/**
 * Created by lhq-mint on 17-4-6.
 */
//"fromCity":"上海","toCity":"北京","fromDate":"2017-04-07","contentType":"json"
public class Ctrip {
    public static void main(String[] args) throws IOException{
        String url ="http://m.ctrip.com/restapi/busphp/h5service/index.php?param=/api/home&method=product.getBusListPage&v=1.0&ref=ctrip.h5&partner=ctrip.h5&clientType=Android--h5&version=1000&_fxpcqlniredt=09031032410334778795";
        StringEntity entity = new StringEntity("{"+"\"fromCity\""+":"+"\"上海\""+","+"\"toCity\""+":"+"\"北京\""+","+"\"fromDate\""+":"+"\"2017-04-07\""+","+"\"contentType\""+":"+"\"json\""+"}","UTF-8");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
//        System.out.println(EntityUtils.toString(entity));
        ResponseHandler<String> rh = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws IOException {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity entity = response.getEntity();
                if (statusLine.getStatusCode() >= 300) {
                    throw new HttpResponseException(
                            statusLine.getStatusCode(),
                            statusLine.getReasonPhrase());
                }
                if (entity == null) {
                    throw new ClientProtocolException("Response contains no content");
                }
//                System.out.println(statusLine.getStatusCode());
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Charset.forName("UTF-8")));
                String res="";
                String data = reader.readLine();
                while(data != null){
                    res += data;
                    data = reader.readLine();
                }
                return res;
            }
        };
        String response = httpclient.execute(httppost, rh);
        System.out.println(StringEscapeUtils.unescapeJava(response));
    }
}
