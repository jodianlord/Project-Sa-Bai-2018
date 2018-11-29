/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.PatientDAO;
import dao.VisitDAO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Patient;
import model.Visit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static servlet.CreatePatientServlet.decodeToImage;
import static servlet.CreatePatientServlet.getJSONObject;
import util.RESTHandler;

/**
 *
 * @author jordy
 */
public class SearchPatientFaceServlet extends HttpServlet {

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
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        String body = getBody(request);
        JSONObject bodyJSON = null;
        try {
            bodyJSON = getJSONObject(body);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String photoImage = (String) bodyJSON.get("picture");

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
            } else {
                verificationEncoding = null;
            }

        } catch (ParseException ex) {
            Logger.getLogger(CreatePatientServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        toEncodeFile.delete();
        Patient p = PatientDAO.getPatientByFace(verificationEncoding);

        if (p == null) {
            session.setAttribute("searchError", "Patient not found!");
            response.sendRedirect("existing_patient.jsp");
            return;
        }

        Visit v = VisitDAO.getPatientLatestVisit(p.getPatientId());

        if (v != null) {

            session.setAttribute("visitRecord", v);
            session.setAttribute("patientRecord", p);

            Visit[] pastVisits = VisitDAO.getVisitByPatientID(p.getPatientId());
            session.setAttribute("pastVisits", pastVisits);

        } else {
            session.setAttribute("searchError", "Patient not found!");
            System.out.println("1235467870");
        }
        System.out.println("we found it!");
        //response.sendRedirect("existing_patient.jsp");
        JSONObject toPrint = new JSONObject();
        toPrint.put("name", p.getName());
        response.setStatus(HttpServletResponse.SC_OK);
        try(PrintWriter out = response.getWriter()){
            out.println(toPrint.toString());
        }
        
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
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
