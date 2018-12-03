/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.PatientDAO;
import dao.VisitDAO;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class SearchPatientLatestVisitServlet extends HttpServlet {

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

        /* TODO output your page here. You may use following sample code. */
        HttpSession session = request.getSession();

        Patient p = null;

        String patientIdInput = request.getParameter("patientID");
        int visitIdInput = 0;
        try {
            visitIdInput = Integer.parseInt(request.getParameter("visitID"));
        } catch (Exception e) {

        }

        String source = request.getParameter("source");

        String pVillage = "";
        int pNo = -1;

        if (patientIdInput == null) {
            session.setAttribute("visitError", "Patient/Visit not found!");
            if (source.equals("consult")) {
                response.sendRedirect("new_consult.jsp");
            } else if (source.equals("postreferral")) {
                response.sendRedirect("new_postreferral.jsp");
            } else {
                response.sendRedirect("new_vitals.jsp");
            }
            return;
        }

        Pattern vil_id = Pattern.compile("([a-z])([a-z])([a-z])(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher vil_id_matcher = vil_id.matcher(patientIdInput);

        Pattern name_alph = Pattern.compile("([a-z()' ])+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher name_alph_matcher = name_alph.matcher(patientIdInput);

        Pattern num = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher num_matcher = num.matcher(patientIdInput);

        if (vil_id_matcher.matches()) {
            System.out.println("match");
            pVillage = patientIdInput.substring(0, 3);
            try {
                pNo = Integer.parseInt(patientIdInput.substring(3));
            } catch (Exception e) {

            }

            if (pNo == -1) {
                session.setAttribute("visitError", "Patient/Visit not found!");
                if (source.equals("consult")) {
                    response.sendRedirect("new_consult.jsp");
                } else if (source.equals("postreferral")) {
                    response.sendRedirect("new_postreferral.jsp");
                } else {
                    response.sendRedirect("new_vitals.jsp");
                }
                return;
            }

            p = PatientDAO.getPatientByPatientID(pVillage, pNo);
        } else if (name_alph_matcher.matches()) {
            p = PatientDAO.getPatientByName(patientIdInput);
        } else if (num_matcher.matches()) {
            try {
                pNo = Integer.parseInt(patientIdInput);
            } catch (Exception e) {
                session.setAttribute("visitError", "Patient/Visit not found!");
                if (source.equals("consult")) {
                    response.sendRedirect("new_consult.jsp");
                } else if (source.equals("postreferral")) {
                    response.sendRedirect("new_postreferral.jsp");
                } else {
                    response.sendRedirect("new_vitals.jsp");
                }
                return;
            }
            p = PatientDAO.getPatientByPatientID(pNo);
        }
        
        if (p == null) {
            session.setAttribute("visitError", "Patient/Visit not found!");
            if (source.equals("consult")) {
                response.sendRedirect("new_consult.jsp");
            } else if (source.equals("postreferral")) {
                response.sendRedirect("new_postreferral.jsp");
            } else {
                response.sendRedirect("new_vitals.jsp");
            }
//                out.println("87654");
            return;
        }

        Visit v = VisitDAO.getPatientLatestVisit(p.getPatientId());
        //Visit v = VisitDAO.getVisitByVisitID(visitIdInput);

        if (v != null) {

            session.setAttribute("visitRecord", v);
            session.setAttribute("patientRecord", p);

            if (source.equals("consult")) {
                Visit[] pastVisits = VisitDAO.getVisitByPatientID(p.getPatientId());
                session.setAttribute("pastVisits", pastVisits);
                response.sendRedirect("new_consult.jsp");
            } else if (source.equals("postreferral")) {
                Visit[] pastVisits = VisitDAO.getVisitByPatientID(p.getPatientId());
                session.setAttribute("pastVisits", pastVisits);
                response.sendRedirect("new_postreferral.jsp");
            } else {
                response.sendRedirect("new_vitals.jsp");
            }

        } else {
            session.setAttribute("visitError", "Patient/Visit not found!");
            if (source.equals("consult")) {
                response.sendRedirect("new_consult.jsp");
            } else if (source.equals("postreferral")) {
                response.sendRedirect("new_postreferral.jsp");
            } else {
                response.sendRedirect("new_vitals.jsp");
            }
        }
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
