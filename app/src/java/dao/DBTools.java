/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import servlet.CreatePatientServlet;
import static servlet.CreatePatientServlet.getJSONObject;
import util.ConnectionManager;
import util.RESTHandler;

/**
 *
 * @author jordy
 */
//note: please copy the connection.properties file to the util package for this to work
public class DBTools {

    Connection conn = null;
    PreparedStatement pstmt = null;

    public static void main(String[] args) {
        //File dir = new File("/home/jordy/Documents/patient-images-backup/patient-images-241217-1641hrs/");
        //File[] directoryListing = dir.listFiles();
        //generateEncodings(directoryListing);
        getFiles("/home/jordy/Desktop/test/");
    }

    public static void changeToJPEG(File[] fileArr) {
        if (fileArr != null) {
            for (File child : fileArr) {
                String fileName = child.getName();
                String fileNoExt = fileName.substring(0, fileName.indexOf('.'));
                String jpegString = fileNoExt + ".jpeg";
                child.renameTo(new File("/home/jordy/Documents/patient-images-backup/patient-images-241217-1641hrs/" + jpegString));
            }
        }
    }

    public static void getFiles(String directory) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("SELECT patients.id, patients.village_prefix, patient_pictures.picture_blob FROM patients inner join patient_pictures on patients.id=patient_pictures.patient_id");
            rs = pstmt.executeQuery();
            while (rs.next()) {

                String village = rs.getString("village_prefix");
                int id = rs.getInt("id");

                File imgFile = new File(directory + village + id + ".jpeg");
                FileOutputStream output = new FileOutputStream(imgFile);

                InputStream input = rs.getBinaryStream("picture_blob");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dao.ConnectionManager.close(conn, pstmt, rs);
        }

    }

    public static void generateEncodings(File[] fileArr) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        for (File child : fileArr) {
            String fileName = child.getName();
            if (fileName.contains("jpeg")) {
                Map<String, File> dataMap = new HashMap<String, File>();
                dataMap.put("image", child);
                JSONObject verificationEncoding = null;
                try {
                    String verificationEncodingString = RESTHandler.sendMultipartPost(RESTHandler.facialURL + "getencoding", dataMap);
                    if (verificationEncodingString != null && verificationEncodingString.length() > 0) {
                        verificationEncoding = getJSONObject(verificationEncodingString);
                        System.out.println(verificationEncoding.toString());
                    } else {
                        verificationEncoding = null;
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(CreatePatientServlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    conn = ConnectionManager.getConnection();
                    pstmt = conn.prepareStatement("UPDATE patients SET face_encodings = ? where id = ?");
                    String pID = fileName.substring(fileName.indexOf('V') + 1, fileName.indexOf('.'));
                    int dbID = Integer.parseInt(pID);
                    pstmt.setString(1, verificationEncoding.toString());
                    pstmt.setInt(2, dbID);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    continue;
                } catch (NumberFormatException e) {
                    continue;
                } catch (NullPointerException e) {
                    continue;
                } finally {
                    dao.ConnectionManager.close(conn, pstmt, rs);
                }
            }
        }
    }

    public static void uploadDB(File[] fileArr) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            for (File child : fileArr) {
                String fileName = child.getName();
                if (fileName.contains("jpeg")) {
                    pstmt = conn.prepareStatement("INSERT INTO patient_pictures (patient_id, picture_blob) VALUES (?, ?)");
                    String pID = fileName.substring(fileName.indexOf('V') + 1, fileName.indexOf('.'));
                    System.out.println(pID);
                    int dbID = 0;
                    try {
                        dbID = Integer.parseInt(pID);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    pstmt.setInt(1, dbID);
                    pstmt.setBinaryStream(2, new FileInputStream(child));
                    pstmt.executeUpdate();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dao.ConnectionManager.close(conn, pstmt, rs);
        }
    }
}
