package com.jreverse.jreverse.Tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileListerTest {
    public static void main(String[] args) throws IOException {
        // The URL of the Google Apps Script web app
        String url = "https://script.google.com/macros/s/AKfycby3Rt2cSDaKITW51GlNb-t4UZTAuzL2DllJIC4awcZVQ7kpfSp8fFObkJRlIaS9DwhA/exec";

        // Create a URL object
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Optionally, you can specify the request method
        con.setRequestMethod("GET");

        // If you need to send a POST request
        // con.setRequestMethod("POST");
        // con.setDoOutput(true);
        // String postParams = "param1=value1&param2=value2";
        // OutputStream os = con.getOutputStream();
        // os.write(postParams.getBytes());
        // os.flush();
        // os.close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response
        System.out.println(response.toString());
    }
}
