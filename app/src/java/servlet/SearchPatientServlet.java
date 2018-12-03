/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.PatientDAO;
import dao.VisitDAO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Patient;
import model.Visit;

/**
 *
 * @author tcw
 */
public class SearchPatientServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String inputPatientID = request.getParameter("patientID");
        String pVillage;
        Patient p = null;
        int pNo = -1;
        System.out.println(inputPatientID);
        if (inputPatientID == null) {
            session.setAttribute("searchError", "Patient not found!");
            response.sendRedirect("existing_patient.jsp");
            return;
        }

        Pattern vil_id = Pattern.compile("([a-z])([a-z])([a-z])(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher vil_id_matcher = vil_id.matcher(inputPatientID);

        Pattern name_alph = Pattern.compile("([a-z()' ])+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher name_alph_matcher = name_alph.matcher(inputPatientID);
        
        Pattern num = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher num_matcher = num.matcher(inputPatientID);

        if (vil_id_matcher.matches()) {
            System.out.println("match");
            pVillage = inputPatientID.substring(0, 3);
            try {
                pNo = Integer.parseInt(inputPatientID.substring(3));
            } catch (Exception e) {

            }

            if (pNo == -1) {
                session.setAttribute("searchError", "Patient not found!");
                response.sendRedirect("existing_patient.jsp");
                return;
            }

            p = PatientDAO.getPatientByPatientID(pVillage, pNo);
        }else if(name_alph_matcher.matches()){
            p = PatientDAO.getPatientByName(inputPatientID);
        }else if(num_matcher.matches()){
            try {
                pNo = Integer.parseInt(inputPatientID);
            } catch (Exception e) {
                session.setAttribute("searchError", "Patient not found!");
                response.sendRedirect("existing_patient.jsp");
                return;
            }
            p = PatientDAO.getPatientByPatientID(pNo);
        }

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

        response.sendRedirect("existing_patient.jsp");
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
