package com.silkimen.cordovahttp;

import android.app.Activity;
import android.content.res.AssetManager;

import com.silkimen.http.TLSConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CordovaHttpPluginUtil {

    private static List<String> nocheckDomains;
    private static TLSConfiguration nocheckTLSConfiguration ;

    private static TrustManager[] noOpTrustManagers = new TrustManager[] { new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // intentionally left blank
        }
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // intentionally left blank
        }
    } };

    private static HostnameVerifier noOpVerifier  = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static List<String> getNoCheckDomainsFromFile(final Activity activity) {

        AssetManager assetManager = activity.getAssets();
        List<String> domainList = new ArrayList<String>();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(assetManager.open("www/AdancedHTTPDomainConfig.json"),"UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line ;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray advancedURLJsonArray = jsonObject.getJSONArray("advancedDomain");
            for (int i = 0; i < advancedURLJsonArray.length() ; i++) {
                domainList.add(advancedURLJsonArray.get(i).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if(inputStreamReader != null){
                    inputStreamReader.close();
                }
                if(bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return domainList;
    }

    public static TLSConfiguration getNoCheckTLSConfiguration(){
        if( nocheckTLSConfiguration == null){
            nocheckTLSConfiguration = new TLSConfiguration();
            nocheckTLSConfiguration.setHostnameVerifier(noOpVerifier);
            nocheckTLSConfiguration.setTrustManagers(noOpTrustManagers);
        }
        return nocheckTLSConfiguration;
    }

    public static boolean isNoCheckURL(String url, final Activity activity){

        if( nocheckDomains == null || nocheckDomains.size() == 0){
            nocheckDomains = getNoCheckDomainsFromFile(activity);
        }

        for (String nocheckDomain: nocheckDomains) {
            if( url.contains(nocheckDomain)){
                return true;
            }
        }
        return false;
    }

}
