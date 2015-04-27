/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * HttpController.java
 */

package com.comp680team2.controller;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;


public class HttpController {

    public String makeGetRequest(String url) {
        String response = "";

        try {

            //Setup timeouts
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
            HttpConnectionParams.setSoTimeout(httpParameters, 15000);

            //Make http request
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(request);

            //Converts response to string
            InputStream stream = httpResponse.getEntity().getContent();
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, "UTF-8");
            response = writer.toString();

        } catch (IOException e) {
            System.out.println("Error executing HttpRequest: " + url);
        }

        return response;
    }
}
