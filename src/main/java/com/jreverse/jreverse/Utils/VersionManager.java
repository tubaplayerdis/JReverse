package com.jreverse.jreverse.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class VersionManager {
    public List<JReverseVersion> JReversePubList = new ArrayList<>();
    public List<JReverseVersion> JReverseDevList = new ArrayList<>();
    public boolean hasInternetConnection = false;
    public float currentVersion = -1F;//-1 is for latest
    public static final String CoreDLLPath = System.getProperty("user.dir")+"/JReverseCore.dll";
    public VersionManager() {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            System.out.println("Internet is connected. Continuing Version Manager");
            hasInternetConnection = true;
        } catch (IOException e) {
            System.out.println("Internet is not connected");
            JReversePubList.add(new JReverseVersion("No Internet Connection", 0.0F, "No Internet Connection", 0, "null", false));
            JReverseDevList.add(new JReverseVersion("No Internet Connection", 0.0F, "No Internet Connection", 0, "null", false));
            hasInternetConnection = false;
            return;
        }
        //Version Stuff
        try{
            String url = "https://script.google.com/macros/s/AKfycby3Rt2cSDaKITW51GlNb-t4UZTAuzL2DllJIC4awcZVQ7kpfSp8fFObkJRlIaS9DwhA/exec";
            // Create a URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Optionally, you can specify the request method
            con.setRequestMethod("GET");

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

            //Json the response
            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println(jsonResponse.toString(2)); // Pretty print JSON

            //Get Dev Versions
            JSONArray devarray = jsonResponse.getJSONArray("dev");
            for (int i = 0; i < devarray.length(); i++) {
                JSONObject verobj = devarray.getJSONObject(i);
                JReverseVersion version = new JReverseVersion(verobj.getString("name"), Float.parseFloat(verobj.getString("version")), verobj.getString("date"), verobj.getInt("size"), verobj.getString("downloadlink"), true);
                JReverseDevList.add(version);
            }

            //Get Pub Versions
            JSONArray pubarray = jsonResponse.getJSONArray("pub");
            for (int i = 0; i < pubarray.length(); i++) {
                JSONObject verobj = pubarray.getJSONObject(i);
                JReverseVersion version = new JReverseVersion(verobj.getString("name"), Float.parseFloat(verobj.getString("version")), verobj.getString("date"), verobj.getInt("size"), verobj.getString("downloadlink"), false);
                JReversePubList.add(version);
            }
        } catch (IOException e) {
            System.out.println("Error Getting Versions!");
        }
    }

    public void refresh() {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            System.out.println("Internet is connected. Continuing Version Manager");
        } catch (IOException e) {
            System.out.println("Internet is not connected");
            JReversePubList.add(new JReverseVersion("No Internet Connection", 0.0F, "No Internet Connection", 0, "null", false));
            JReverseDevList.add(new JReverseVersion("No Internet Connection", 0.0F, "No Internet Connection", 0, "null", false));
        }
        JReverseDevList.clear();
        JReversePubList.clear();
        try{
            String url = "https://script.google.com/macros/s/AKfycby3Rt2cSDaKITW51GlNb-t4UZTAuzL2DllJIC4awcZVQ7kpfSp8fFObkJRlIaS9DwhA/exec";
            // Create a URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Optionally, you can specify the request method
            con.setRequestMethod("GET");

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

            //Json the response
            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println(jsonResponse.toString(2)); // Pretty print JSON

            //Get Dev Versions
            JSONArray devarray = jsonResponse.getJSONArray("dev");
            for (int i = 0; i < devarray.length(); i++) {
                JSONObject verobj = devarray.getJSONObject(i);
                JReverseVersion version = new JReverseVersion(verobj.getString("name"), Float.parseFloat(verobj.getString("version")), verobj.getString("date"), verobj.getInt("size"), verobj.getString("downloadlink"), true);
                JReverseDevList.add(version);
            }

            //Get Pub Versions
            JSONArray pubarray = jsonResponse.getJSONArray("pub");
            for (int i = 0; i < pubarray.length(); i++) {
                JSONObject verobj = pubarray.getJSONObject(i);
                JReverseVersion version = new JReverseVersion(verobj.getString("name"), Float.parseFloat(verobj.getString("version")), verobj.getString("date"), verobj.getInt("size"), verobj.getString("downloadlink"), false);
                JReversePubList.add(version);
            }
        } catch (IOException e) {
            System.out.println("Error Getting Versions!");
        }
    }

    public JReverseVersion GetVersionInfoByNum(float Version, boolean isdev) {
        if(isdev) {
            for (JReverseVersion ver : JReverseDevList) {
                if(ver.version == Version) return ver;
            }
            return new JReverseVersion("Version Does Not Exist!", 0.0F, "Version Does Not Exist!", 0, "null", true);
        }
        for(JReverseVersion ver : JReversePubList) {
            if(ver.version == Version) return ver;
        }
        return new JReverseVersion("Version Does Not Exist!", 0.0F, "Version Does Not Exist!", 0, "null", false);
    }

    public float GetDownloadedVersion() {
        //File does not exist
        if(!Files.exists(Paths.get(CoreDLLPath))) return -2F;
        //Other cases
    }

    public void SwitchVersion(float version) {
        currentVersion = version;
    }

    public String Download() {
        
    }

}
