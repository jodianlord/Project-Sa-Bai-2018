/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import model.Patient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author jodia
 */
public class CompareFaces implements Callable<Patient> {

    List<Patient> patientList;
    JSONObject compareImages = new JSONObject();

    public CompareFaces(List<Patient> patientList, JSONObject toCompare) {
        this.patientList = patientList;
        compareImages.put("first_encoding", toCompare.get("encoding"));
    }

    public Patient call() {
        HttpURLConnection con;
        try {
            URL myurl = new URL("http://127.0.0.1:5000/compareimages");

            for (Patient p : patientList) {
                con = (HttpURLConnection) myurl.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                JSONObject faceJSON = p.getFaceEncoding();
                if (faceJSON != null) {
                    compareImages.put("second_encoding", faceJSON.get("encoding"));
                    try {
                        String url = "http://127.0.0.1:5000/compareimages";
                        byte[] postData = compareImages.toString().getBytes(StandardCharsets.UTF_8);

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
                        JSONObject postJSON = getJSONObject(content.toString());
                        con.disconnect();
                        if (postJSON.get("match").equals("true")) {
                            return p;
                        }
                    } catch (ParseException e) {
                        continue;
                    }

                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject getJSONObject(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonString);
    }
}
