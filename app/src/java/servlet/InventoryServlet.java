/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.InventoryDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Drug;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author jodia
 */
public class InventoryServlet extends HttpServlet {

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
        response.setContentType("application/json");

        ArrayList<Drug> drugList = InventoryDAO.getInventory();
        JSONArray drugArr = new JSONArray();
        for (Drug drug : drugList) {
            String medName = drug.getMedicine_name();
            int quantity = drug.getQuantity();
            JSONObject thisDrug = new JSONObject();
            thisDrug.put(medName, quantity);
            drugArr.add(thisDrug);
        }

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println(drugArr.toJSONString());
        }
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
        response.setContentType("application/json");
        String body = getBody(request);
        JSONObject bodyJSON = null;
        try {
            bodyJSON = getJSONObject(body);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        JSONArray insertArray = (JSONArray) bodyJSON.get("insert");
        JSONArray changeArray = (JSONArray) bodyJSON.get("change");
        System.out.println("Doing post");
        if (insertArray != null && !insertArray.isEmpty()) {
            for (Object obj : insertArray) {
                JSONObject record = (JSONObject) obj;
                String id = (String) record.get("id");
                String medName = (String) record.get("name");
                String quantity = (String) record.get("quantity");
                Drug dr = new Drug(Integer.parseInt(id), medName, Integer.parseInt(quantity));
                if (!InventoryDAO.addDrug(dr)) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

            }
        }
        if (changeArray != null && !changeArray.isEmpty()) {
            for (Object obj : changeArray) {
                JSONObject record = (JSONObject) obj;
                String id = (String) record.get("id");
                String medName = (String) record.get("name");
                String quantity = (String) record.get("quantity");
                Drug dr = new Drug(Integer.parseInt(id), medName, Integer.parseInt(quantity));
                if (!InventoryDAO.changeQuantity(dr)) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }
        }

        if (!InventoryDAO.reorderInventory()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            out.println("{\"status\" : \"ok\"}");
        }
        response.setStatus(HttpServletResponse.SC_OK);
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
}
