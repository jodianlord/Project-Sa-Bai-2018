/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author yu.fu.2015
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.file.Files;
import model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.CompareFaces;
import util.RESTHandler;

public class PatientDAO {

    private static int threads = 10;

    public static boolean addPatient(Patient p) {

        boolean insertSuccess = false;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //Opens a connection
            conn = ConnectionManager.getConnection();

            //Statement to insert information into the database: user_id, password, name, school, edollar
            pstmt = conn.prepareStatement("INSERT INTO patients "
                    + "(village_prefix, name, image, contactNo, gender, travelling_time_to_village, date_of_birth, face_encodings, drug_allergy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

//            System.out.println("imageLength" + fgImage.length);
            //Sets the objects retrieved from the getter methods into the variables
            pstmt.setString(1, p.getVillage());
            pstmt.setString(2, p.getName());
            pstmt.setString(3, p.getPhotoImage());
            pstmt.setString(4, p.getContactNo());
            pstmt.setString(5, p.getGender());
            pstmt.setInt(6, p.getTravellingTimeToClinic());
            pstmt.setString(7, p.getDateOfBirth());
            pstmt.setString(8, p.getFaceEncoding().toString());
            pstmt.setString(9, p.getAllergies());
            System.out.println(pstmt.toString());
            //Executes the update and stores data into database
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("we died");
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int genKey = generatedKeys.getInt(1);
                    p.setPatientId(genKey);

                    //insert into images
                    pstmt = conn.prepareStatement("INSERT INTO patient_pictures (patient_id, picture_blob) VALUES (?, ?)");
                    pstmt.setInt(1, genKey);
                    pstmt.setBinaryStream(2, new FileInputStream(p.getImageFile()));
                    pstmt.executeUpdate();
                    insertSuccess = true;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            //Catches any possible SQL exception
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        System.out.println("user should be in");
        p.getImageFile().delete();
        return insertSuccess;
    }

    public static boolean updatePatientDetails(int patientId, String village, String name, String image, String contactNo, int travellingTime, String dateOfBirth, String allergies, File imageFile, String encoding) {

        boolean updateSuccess = false;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //Opens a connection
            conn = ConnectionManager.getConnection();

            //Statement to insert information into the database: user_id, password, name, school, edollar
            if (encoding == null) {
                pstmt = conn.prepareStatement("UPDATE patients SET name = ?, image = ?, contactNo = ?, travelling_time_to_village = ?, date_of_birth = ?, drug_allergy = ? WHERE id = ? && village_prefix = ?");
                pstmt.setString(1, name);
                pstmt.setString(2, image);
                pstmt.setString(3, contactNo);
                pstmt.setInt(4, travellingTime);
                pstmt.setString(5, dateOfBirth);
                pstmt.setString(6, allergies);
                pstmt.setInt(7, patientId);
                pstmt.setString(8, village);
            } else {
                pstmt = conn.prepareStatement("UPDATE patients SET name = ?, image = ?, contactNo = ?, travelling_time_to_village = ?, date_of_birth = ?, drug_allergy = ?, face_encodings = ? WHERE id = ? && village_prefix = ?");
                pstmt.setString(1, name);
                pstmt.setString(2, image);
                pstmt.setString(3, contactNo);
                pstmt.setInt(4, travellingTime);
                pstmt.setString(5, dateOfBirth);
                pstmt.setString(6, allergies);
                pstmt.setString(7, encoding);
                pstmt.setInt(8, patientId);
                pstmt.setString(9, village);
            }

            //Executes the update and stores data into database
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            } else {
                updateSuccess = true;
            }

            if (imageFile != null) {
                pstmt = conn.prepareStatement("UPDATE patient_pictures SET picture_blob = ? WHERE patient_id = ?");
                pstmt.setBinaryStream(1, new FileInputStream(imageFile));
                pstmt.setInt(2, patientId);
                pstmt.executeUpdate();
            }

            //Catches any possible SQL exception
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }

        return updateSuccess;
    }

    public static void updateImage(Patient p) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //Opens a connection
            conn = ConnectionManager.getConnection();

            pstmt = conn.prepareStatement("UPDATE patients SET image = ? WHERE id = ?;");

//            System.out.println("imageLength" + fgImage.length);
            //Sets the objects retrieved from the getter methods into the variables
            pstmt.setString(1, p.getPhotoImage());
            pstmt.setInt(2, p.getPatientId());

            //Executes the update and stores data into database
            pstmt.executeUpdate();

            //Catches any possible SQL exception
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    public static JSONObject getJSONObject(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonString);
    }

    public static Patient getPatientByFace(JSONObject faceEncoding) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        ArrayList<Patient> patientList = new ArrayList<Patient>();
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from patients where face_encodings is not null");
            rs = stmt.executeQuery();

            while (rs.next()) {
                String village = rs.getString("village_prefix");
                int patientId = rs.getInt("id");
                String name = rs.getString("name");
                String contactNo = rs.getString("contactNo");
                String gender = rs.getString("gender");
                String dateOfBirth = rs.getString("date_of_birth");
                int travellingTimeToClinic = rs.getInt("travelling_time_to_village");
                int parentId = rs.getInt("parent");
                String allergy = rs.getString("drug_allergy");
                String encoding = rs.getString("face_encodings");
                String imageString = rs.getString("image");
                JSONObject faceJSON = getJSONObject(encoding);
                Patient temp = new Patient(village, patientId, name, contactNo, gender, dateOfBirth, travellingTimeToClinic, parentId, allergy, faceJSON, null);
                temp.setPhotoImage(imageString);
                patientList.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

        ExecutorService executor = Executors.newFixedThreadPool(50);
        List<Future<Patient>> list = new ArrayList<Future<Patient>>();

        int listSize = patientList.size() / threads;
        int currentMarker = 0;
        for (int i = 0; i < threads; i++) {
            List<Patient> qList;
            if (i == threads - 1) {
                qList = patientList.subList(currentMarker, patientList.size());
            } else {
                qList = patientList.subList(currentMarker, currentMarker + listSize);
            }
            Future<Patient> fut = executor.submit(new CompareFaces(qList, faceEncoding));
            list.add(fut);
            currentMarker += listSize;
        }

        /*
        List<Patient> q1 = patientList.subList(0, patientList.size() / 2);
        List<Patient> q2 = patientList.subList(patientList.size() / 2, patientList.size());
        Collections.reverse(q2);
        //List<Patient> q3 = patientList.subList(patientList.size() / 2, patientList.size() / 2 + patientList.size() / 4);
        //List<Patient> q4 = patientList.subList(patientList.size() / 2 + patientList.size() / 4, patientList.size());

        Future<Patient> future1 = executor.submit(new CompareFaces(q1, faceEncoding));
        Future<Patient> future2 = executor.submit(new CompareFaces(q2, faceEncoding));
        //Future<Patient> future3 = executor.submit(new CompareFaces(q3, faceEncoding));
        //Future<Patient> future4 = executor.submit(new CompareFaces(q4, faceEncoding));
        list.add(future1);
        list.add(future2);
        //list.add(future3);
        //list.add(future4);
         */
        for (Future<Patient> fut : list) {
            try {
                Patient toReturn = fut.get();
                if (toReturn != null) {
                    conn = ConnectionManager.getConnection();
                    stmt = conn.prepareStatement("select * from patient_pictures where patient_id = ?");
                    stmt.setInt(1, toReturn.getPatientId());
                    ResultSet rr = stmt.executeQuery();

                    //File imgFile = new File("patientImg.jpeg");
                    File imgFile = File.createTempFile("patientImg", ".jpeg");
                    FileOutputStream output = new FileOutputStream(imgFile);
                    imgFile.deleteOnExit();
                    if (rr.next()) {
                        InputStream input = rr.getBinaryStream("picture_blob");
                        byte[] buffer = new byte[1024];
                        while (input.read(buffer) > 0) {
                            output.write(buffer);
                        }
                    } else {
                        imgFile = null;
                    }
                    toReturn.setImageFile(imgFile);
                    return toReturn;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        executor.shutdown();
        return null;
    }

    public static Patient getPatientByName(String search) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String[] spaceSplit = search.split("\\s+");
            String toSearch = "%";
            for (String s : spaceSplit) {
                toSearch += s;
                toSearch += "%";
            }
            toSearch += "%";
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM patients WHERE name LIKE ?");
            stmt.setString(1, toSearch);
            System.out.println(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String village = rs.getString("village_prefix");
                int patientId = rs.getInt("id");
                String name = rs.getString("name");
                String contactNo = rs.getString("contactNo");
                String gender = rs.getString("gender");
                String dateOfBirth = rs.getString("date_of_birth");
                int travellingTimeToClinic = rs.getInt("travelling_time_to_village");
                int parentId = rs.getInt("parent");
                String allergy = rs.getString("drug_allergy");
                String encoding = rs.getString("face_encodings");
                JSONObject encodedObj;
                if (encoding == null || encoding.length() == 0) {
                    encodedObj = null;
                } else {
                    encodedObj = getJSONObject(encoding);
                }

                stmt = conn.prepareStatement("select * from patient_pictures where patient_id = ?");
                stmt.setInt(1, patientId);
                ResultSet rr = stmt.executeQuery();

                //File imgFile = new File("patientImg.jpeg");
                File imgFile = File.createTempFile("patientImg", ".jpeg");
                imgFile.deleteOnExit();
                FileOutputStream output = new FileOutputStream(imgFile);

                if (rr.next()) {
                    InputStream input = rr.getBinaryStream("picture_blob");
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        output.write(buffer);
                    }
                } else {
                    imgFile = null;
                }

//                allergies.add(allergy);
//                while (rs.next()) {
//                    allergy = rs.getInt(7);
//                    allergies.add(allergy);
//                }
                Patient p = new Patient(village, patientId, name, contactNo, gender, dateOfBirth, travellingTimeToClinic, parentId, allergy, encodedObj, imgFile);
                p.setPhotoImage(rs.getString("image"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }

    public static Patient getPatientByPatientID(String pVillage, int pNo) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from patients where id = ? and village_prefix = ?");
            stmt.setInt(1, pNo);
            stmt.setString(2, pVillage);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String village = rs.getString("village_prefix");
                int patientId = rs.getInt("id");
                String name = rs.getString("name");
                String contactNo = rs.getString("contactNo");
                String gender = rs.getString("gender");
                String dateOfBirth = rs.getString("date_of_birth");
                int travellingTimeToClinic = rs.getInt("travelling_time_to_village");
                int parentId = rs.getInt("parent");
                String allergy = rs.getString("drug_allergy");
                String encoding = rs.getString("face_encodings");
                JSONObject encodedObj;
                if (encoding == null || encoding.length() == 0) {
                    encodedObj = null;
                } else {
                    encodedObj = getJSONObject(encoding);
                }

                stmt = conn.prepareStatement("select * from patient_pictures where patient_id = ?");
                stmt.setInt(1, pNo);
                ResultSet rr = stmt.executeQuery();

                //File imgFile = new File("patientImg.jpeg");
                File imgFile = File.createTempFile("patientImg", ".jpeg");
                imgFile.deleteOnExit();
                FileOutputStream output = new FileOutputStream(imgFile);

                if (rr.next()) {
                    InputStream input = rr.getBinaryStream("picture_blob");
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        output.write(buffer);
                    }
                } else {
                    imgFile = null;
                }

//                allergies.add(allergy);
//                while (rs.next()) {
//                    allergy = rs.getInt(7);
//                    allergies.add(allergy);
//                }
                Patient p = new Patient(village, patientId, name, contactNo, gender, dateOfBirth, travellingTimeToClinic, parentId, allergy, encodedObj, imgFile);
                p.setPhotoImage(rs.getString("image"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }

    Connection conn;
    ResultSet rs;
    PreparedStatement stmt;

    public static Patient getPatientByPatientID(String patientID) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from patients where id = ?");
            stmt.setString(1, patientID);
            rs = stmt.executeQuery();

            ArrayList<Integer> allergies = new ArrayList<>();
            if (rs.next()) {
                String village = rs.getString("village_prefix");
                int patientId = rs.getInt("id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String dateOfBirth = rs.getString("date_of_birth");
                int parentId = rs.getInt("parent");
                String allergy = rs.getString("drug_allergy");
                String encoding = rs.getString("face_encodings");
                JSONObject encodedObj;
                if (encoding == null || encoding.length() == 0) {
                    encodedObj = null;
                } else {
                    encodedObj = getJSONObject(encoding);
                }
//                allergies.add(allergy);
//                while (rs.next()) {
//                    allergy = rs.getInt(7);
//                    allergies.add(allergy);
//                }

                stmt = conn.prepareStatement("select * from patient_pictures where patient_id = ?");
                stmt.setInt(1, patientId);
                ResultSet rr = stmt.executeQuery();

                //File imgFile = new File("patientImg.jpeg");
                File imgFile = File.createTempFile("patientImg", ".jpeg");
                imgFile.deleteOnExit();
                FileOutputStream output = new FileOutputStream(imgFile);

                if (rr.next()) {
                    InputStream input = rs.getBinaryStream("picture_blob");
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        output.write(buffer);
                    }
                } else {
                    imgFile = null;
                }

                Patient p = new Patient(village, patientId, name, gender, dateOfBirth, parentId, allergy, encodedObj, imgFile);
                p.setPhotoImage(rs.getString("image"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }

    public static Patient getPatientByPatientID(int patientID) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from patients where id = ?");
            stmt.setInt(1, patientID);
            rs = stmt.executeQuery();

            ArrayList<Integer> allergies = new ArrayList<>();
            if (rs.next()) {
                String village = rs.getString("village_prefix");
                int patientId = rs.getInt("id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String dateOfBirth = rs.getString("date_of_birth");
                int parentId = rs.getInt("parent");
                String allergy = rs.getString("drug_allergy");
                String encoding = rs.getString("face_encodings");
                JSONObject encodedObj;
                if (encoding == null || encoding.length() == 0) {
                    encodedObj = null;
                } else {
                    encodedObj = getJSONObject(encoding);
                }
//                allergies.add(allergy);
//                while (rs.next()) {
//                    allergy = rs.getInt(7);
//                    allergies.add(allergy);
//                }

                stmt = conn.prepareStatement("select * from patient_pictures where patient_id = ?");
                stmt.setInt(1, patientId);
                ResultSet rr = stmt.executeQuery();

                //File imgFile = new File("patientImg.jpeg");
                File imgFile = File.createTempFile("patientImg", "jpeg");
                FileOutputStream output = new FileOutputStream(imgFile);
                imgFile.deleteOnExit();
                if (rr.next()) {
                    InputStream input = rr.getBinaryStream("picture_blob");
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        output.write(buffer);
                    }
                } else {
                    imgFile = null;
                }

                Patient p = new Patient(village, patientId, name, gender, dateOfBirth, parentId, allergy, encodedObj, imgFile);
                p.setPhotoImage(rs.getString("image"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }

}
