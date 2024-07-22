package com.jreverse.jreverse.Utils;

import com.jreverse.jreverse.Bridge.JReverseBridge;
import javafx.scene.control.ProgressBar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
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
    public boolean isDownloadedLatest = false;
    public float currentVersion = -1F;//-2 is for none
    public float latestVersion = 0F;
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
            isDownloadedLatest = false;
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

            float highestversion = 0.0F;
            if(JReversePubList.isEmpty()) {
                for(JReverseVersion version : JReverseDevList) {
                    if(version.version > highestversion) highestversion = version.version;
                }
            }
            else {
                for(JReverseVersion version : JReversePubList) {
                    if(version.version > highestversion) highestversion = version.version;
                }
            }
            if(GetDownloadedVersion() == highestversion) isDownloadedLatest = true;
            latestVersion = highestversion;
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
            // Don't! print the response twice
            //System.out.println(response.toString());

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

            //Check for latest version downloaded
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

    /*
    Returns:
    -3 = No file
    -2 = Corrupt?Missing Data
    -1 = Latest
    others = others
     */
    public float GetDownloadedVersion() {
        //File does not exist
        Path corepath = Paths.get(CoreDLLPath);
        if(!Files.exists(corepath)) return -3F;
        //Get Version Data of file
        float version = JReverseBridge.GetCoreFileVersion(CoreDLLPath);
        return version;
        //Maybe implement Latest?
    }

    public JReverseVersion GetLatestVersion() {
        if(latestVersion == 0F) return new JReverseVersion("Initialize the Version Manager!", 0.0F, "Initialize the Version Manager!", 0, "null", false);
        JReverseVersion retver = GetVersionInfoByNum(latestVersion, false);
        if(retver.name.equals("Version Does Not Exist!")) {
            return GetVersionInfoByNum(latestVersion, true);
        } else {
            return retver;
        }
    }

    public void SwitchVersion(float version) {
        currentVersion = version;
    }

    public void Download(ProgressBar progressBar) {
        String downloadLink = null;
        if(currentVersion == -1F) {
            downloadLink = GetLatestVersion().downloadLink;
        }

        String saveFilePath = CoreDLLPath;
        String fileURL = downloadLink;
        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // Check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // Extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10, disposition.length() - 1);
                    }
                } else {
                    // Extracts file name from URL
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // Opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePathWithFileName = saveFilePath;

                // Opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePathWithFileName);

                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                long totalBytesRead = 0;
                int percentCompleted = 0;
                long fileSize = contentLength;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    outputStream.write(buffer, 0, bytesRead);

                    int percentCompletedNew = (int) (totalBytesRead * 100 / fileSize);

                    if (percentCompletedNew > percentCompleted) {
                        percentCompleted = percentCompletedNew;
                        System.out.print("Progress: " + percentCompleted + "%\r");
                        progressBar.setProgress(percentCompleted);
                    }
                }

                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (IOException e) {
            System.out.println("Error with downloading!");
        }
    }

    public void Download() {
        //Implement DownloadWindowView for progress



        String downloadLink = null;
        if (currentVersion == -1F) {
            downloadLink = GetLatestVersion().downloadLink;
        }

        String saveFilePath = CoreDLLPath;
        String fileURL = downloadLink;
        try {
            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // Check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // Extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10, disposition.length() - 1);
                    }
                } else {
                    // Extracts file name from URL
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // Opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePathWithFileName = saveFilePath;

                // Opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePathWithFileName);

                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                long totalBytesRead = 0;
                int percentCompleted = 0;
                long fileSize = contentLength;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    outputStream.write(buffer, 0, bytesRead);

                    int percentCompletedNew = (int) (totalBytesRead * 100 / fileSize);

                    if (percentCompletedNew > percentCompleted) {
                        percentCompleted = percentCompletedNew;
                        System.out.print("Progress: " + percentCompleted + "%\r");
                    }
                }

                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (IOException e) {
            System.out.println("Error with downloading!");
        }
    }
}
