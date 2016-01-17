/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homis.app;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
 
/**
 *
 * @author DarkMatter
 */
public class HttpRequest {
     
    private final String baseUrl = Settings.api;
   
    
    
    public String ServiceGet(String url) {
        String respon = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(baseUrl+url);
            httpGet.setHeader(new BasicHeader("Prama", "no-cache"));
            httpGet.setHeader(new BasicHeader("Cache-Control", "no-cache"));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            respon = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
        }
        return respon;
    }
    
    public String ServicePost(String url, ArrayList nameValuePairs){
        String respon = null;
        System.out.println(baseUrl);
    	try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(baseUrl+url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            HttpResponse res = httpclient.execute(httppost); 
            HttpEntity entity = res.getEntity();
//            entity.getContent();
            respon = EntityUtils.toString(entity);
            entity.consumeContent();
//            
	 return respon;
        }catch(IOException e) {
	 return "error";
        } 
        
    }
   
  
   
    
}
