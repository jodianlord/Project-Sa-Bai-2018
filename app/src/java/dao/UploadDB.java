/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.ConnectionManager;

/**
 *
 * @author jordy
 */

//note: please copy the connection.properties file to the util package for this to work
public class UploadDB {

    Connection conn = null;
    PreparedStatement pstmt = null;

    public static void main(String[] args) {
        File dir = new File("/home/jordy/Documents/patient-images-backup/patient-images-241217-1641hrs/");
        File[] directoryListing = dir.listFiles();
        uploadDB(directoryListing);
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
                    try{
                        dbID = Integer.parseInt(pID);
                    }catch(NumberFormatException e){
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
