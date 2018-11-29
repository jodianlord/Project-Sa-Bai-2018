/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dao.FingerprintDAO;
import dao.PatientDAO;
import dao.VisitDAO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Patient;
import model.Visit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.BASE64Decoder;
import util.FingerprintClass;
import util.RESTHandler;

/**
 *
 * @author tcw
 */
public class CreatePatientServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            boolean patientCorrect = false;
            boolean fingerprintCorrect = false;
            boolean visitCorrect = false;
            boolean photoCorrect = false;

//            int patientID = Integer.parseInt(request.getParameter("patientID"));
            System.out.println("Create patient servlet");
            String village = request.getParameter("village");
            String name = request.getParameter("name");
            String contactNo = request.getParameter("contactNo");
            String gender = request.getParameter("gender");
            String dateOfBirth = request.getParameter("dateOfBirth");
            String travellingTimeToClinic = request.getParameter("travellingTimeToClinic");
            String photoImage = request.getParameter("photoImage");

            System.out.println("photo: " + photoImage);

            //get facial encodings
            //ServletContext servletContext = this.getServletConfig().getServletContext();
            BufferedImage toEncode = decodeToImage(photoImage.substring(photoImage.indexOf(',') + 1, photoImage.length()));
            File toEncodeFile = new File("/home/sabai/image.jpeg");
            ImageIO.write(toEncode, "jpeg", toEncodeFile);
            Map<String, File> dataMap = new HashMap<String, File>();
            dataMap.put("image", toEncodeFile);
            String verificationEncodingString = RESTHandler.sendMultipartPost(RESTHandler.facialURL + "getencoding", dataMap);
            JSONObject verificationEncoding = null;
            try {
                if (verificationEncodingString != null && verificationEncodingString.length() > 0) {
                    verificationEncoding = getJSONObject(verificationEncodingString);
                    System.out.println(verificationEncoding.toString());
                }else{
                    verificationEncoding = null;
                }

            } catch (ParseException ex) {
                Logger.getLogger(CreatePatientServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            toEncodeFile.delete();

            Gson gs = new GsonBuilder().setPrettyPrinting().create();

            JsonObject nullFingerprintJsonObject = new JsonObject();
            nullFingerprintJsonObject.addProperty("status", "null");

            if (FingerprintClass.fingerprintOne == null) {
                System.out.println("one is null");
                nullFingerprintJsonObject.addProperty("oneNull", "fingerprint one is null");
            }

            if (FingerprintClass.fingerprintTwo == null) {
                System.out.println("two is null");
                nullFingerprintJsonObject.addProperty("twoNull", "fingerprint two is null");
            }

//                nullFingerprintJsonObject.addProperty("oneNull", "fingerprint one is null");
//                nullFingerprintJsonObject.addProperty("twoNull", "fingerprint two is null");
            if ((nullFingerprintJsonObject.get("oneNull") != null || nullFingerprintJsonObject.get("twoNull") != null) && verificationEncoding == null) {
                System.out.println("something is fucky wucky");
                out.print(gs.toJson(nullFingerprintJsonObject));
                out.close();
                return;
            }

            Patient p = new Patient(village, 0, name, contactNo, gender, dateOfBirth, Integer.parseInt(travellingTimeToClinic), 0, null, verificationEncoding);

            PatientDAO.addPatient(p);

            patientCorrect = true;

            try {
                p.setFingerprintOne(FingerprintClass.fingerprintOne);
                p.setFingerprintTwo(FingerprintClass.fingerprintTwo);
                FingerprintDAO.addFingerprint(p.getPatientId(), p.getFingerprintOne().getFingerprintValue(), p.getFingerprintOne().getFingerprintSize(), p.getFingerprintOne().getFingerprintImage());
                FingerprintDAO.addFingerprint(p.getPatientId(), p.getFingerprintTwo().getFingerprintValue(), p.getFingerprintTwo().getFingerprintSize(), p.getFingerprintTwo().getFingerprintImage());
                fingerprintCorrect = true;
            } catch (Exception e) {
                System.out.println("fg error");
            }

            if (photoImage != null) {

                byte[] photoImageByte = Base64.getDecoder().decode(photoImage.replace("data:image/jpeg;base64,", ""));

                ServletContext servletContext = this.getServletConfig().getServletContext();
                System.out.println("contextPath = " + servletContext.getContextPath());
                System.out.println("RealPath = " + servletContext.getRealPath("/"));
                System.out.println("user.dir = " + System.getProperty("user.dir"));

//                try (OutputStream stream = new FileOutputStream(new File(servletContext.getRealPath("/") + "../../web/patient-images/" + p.getVillage() + p.getPatientId() + ".png"))) {
//                    stream.write(photoImageByte);
//                }
                try (OutputStream stream = new FileOutputStream(new File("/home/sabai/" + p.getVillage() + p.getPatientId() + ".png"))) {
                    stream.write(photoImageByte);
                }

            }

            p.setPhotoImage(p.getVillage() + p.getPatientId() + ".png");

            PatientDAO.updateImage(p);

            photoCorrect = true;

            System.out.println(p.getPatientId());
            System.out.println(village);
            System.out.println(name);
            System.out.println(gender);
            System.out.println(dateOfBirth);
//            System.out.println(photoImage.replace("data:image/jpeg;base64,", ""));

            VisitDAO visitDAO = new VisitDAO();

            int visitId = visitDAO.countTotalVisits() + 1;
            System.out.println(visitId);
            Date date = new Date();
            String visitDate = date.toString();

            Visit visit = new Visit(visitId, p.getPatientId(), visitDate);
            boolean successful = visitDAO.insertData(visitId, p.getPatientId(), visitDate);
            if (successful) {
                System.out.println("successful registered patient and created a new visit");
            }

            JsonObject jo = new JsonObject();
            jo.addProperty("status", "success");
            jo.addProperty("newID", p.getVillage() + p.getPatientId());
            out.print(gs.toJson(jo));
            out.close();
        }
    }

    public static BufferedImage decodeToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static JSONObject getJSONObject(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonString);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
