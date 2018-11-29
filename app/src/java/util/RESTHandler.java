/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Jordy
 */
public class RESTHandler {

    private static HttpURLConnection con;
    private static final String PROPS_FILENAME = "dreamfactory.properties";
    private static String apiKey;
    public static String facialURL = "http://127.0.0.1:5000/";

    public static String sendDeleteRequest(String requestURL) {
        return null;
    }
    
    public static void main(String[] args){
        String requestURL = facialURL += "getencoding";
        HashMap<String, File> dataMap = new HashMap<String, File>();
        dataMap.put("image", new File("C:\\Users\\jodia\\Desktop\\download.jpg"));
        try {
            sendMultipartPost(requestURL, dataMap);
        } catch (IOException ex) {
            Logger.getLogger(RESTHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readAPIKey(String keyType) {
        InputStream is = null;
        try {
            // Retrieve properties from connection.properties via the CLASSPATH
            // WEB-INF/classes is on the CLASSPATH
            is = ConnectionManager.class.getResourceAsStream(PROPS_FILENAME);
            Properties props = new Properties();
            props.load(is);

            // load database connection details
            apiKey = props.getProperty(keyType);

        } catch (Exception ex) {
            // unable to load properties file
            String message = "Unable to load '" + PROPS_FILENAME + "'.";

            System.out.println(message);
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, message, ex);
            throw new RuntimeException(message, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConnectionManager.class.getName()).log(Level.WARNING, "Unable to close dreamfactory.properties", ex);
                }
            }
        }
    }

    public static String sendMultipartPost(String requestURL, Map<String, File> dataFile) throws IOException {
        MultipartUtility multipart = new MultipartUtility(requestURL, "UTF-8");
        for (String key : dataFile.keySet()) {
            File value = dataFile.get(key);
            multipart.addFilePart(key, value);
        }

        List<String> response = multipart.finish();
        for (String line : response) {
            return line;
            //System.out.println(line);
            //System.out.println("divide");
        }
        return ":";
    }

    public static String sendPostRequest(String requestURL, JSONObject body, String keyType) throws MalformedURLException,
            ProtocolException, IOException {
        byte[] postData = body.toString().getBytes(StandardCharsets.UTF_8);
        try {
            URL myurl = new URL(requestURL);
            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            if (keyType != null && keyType.length() != 0 && keyType.equals("df.key")) {
                readAPIKey(keyType);
                con.setRequestProperty("X-DreamFactory-API-Key", apiKey);
            }

            if (keyType != null && keyType.length() != 0 && keyType.equals("bc.key")) {
                readAPIKey(keyType);
                con.setRequestProperty("X-Blockchain-Key", apiKey);
            }

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());

            return content.toString();
        } finally {
            con.disconnect();
        }
    }

    public static String sendGetRequest(String requestURL, String keyType) throws MalformedURLException, IOException {
        URL obj = new URL(requestURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        if (keyType != null && keyType.length() != 0 && keyType.equals("df.key")) {
            readAPIKey(keyType);
            con.setRequestProperty("X-DreamFactory-API-Key", apiKey);
        }

        if (keyType != null && keyType.length() != 0 && keyType.equals("bc.key")) {
            readAPIKey(keyType);
            con.setRequestProperty("X-Blockchain-Key", apiKey);
        }

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + requestURL);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String result = response.toString();
        //System.out.println(result);
        return result;
    }
}
