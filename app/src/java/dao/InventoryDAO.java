/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Drug;
import model.Order;
import util.DateUtility;

/**
 *
 * @author Kwtam
 */
public class InventoryDAO {
    public static ArrayList<Drug> getInventory(){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Drug> drugList = new ArrayList();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("Select * from inventory order by id");
            rs = stmt.executeQuery();

            while (rs.next()) {
                String medicine = rs.getString("medicine_name");
                int quantity = rs.getInt("quantity");
                int id = rs.getInt("id");
                
                drugList.add(new Drug(id, medicine, quantity));
            }
            //Returns the converted array to the caller of method
            return drugList;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static int getDrugQuantity(String name){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("Select * from inventory where medicine_name = ?");
            stmt.setString(1, name);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantity");
            }
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return 0;
    }
    
    public static boolean changeQuantity(Drug dr){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("UPDATE inventory SET quantity = ? WHERE id = ?");
            stmt.setInt(1, dr.getQuantity());
            stmt.setInt(2, dr.getID());
            stmt.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            ConnectionManager.close(conn, stmt, rs);
        }
    }
    
    public static boolean reorderInventory(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("SELECT medicine_name, quantity FROM inventory order by medicine_name");
            rs = pstmt.executeQuery();
            ArrayList<Drug> drugList = new ArrayList<Drug>();
            int counter = 1;
            while (rs.next()) {
                String medicine = rs.getString("medicine_name");
                int quantity = rs.getInt("quantity");
                drugList.add(new Drug(counter, medicine, quantity));
                counter++;
            }
            System.out.println(drugList.size());
            
            pstmt = conn.prepareStatement("DELETE FROM inventory WHERE id <= " + drugList.size());
            
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("ALTER TABLE inventory AUTO_INCREMENT = 1");
            pstmt.executeUpdate();
            
            for(Drug drug : drugList){
                System.out.println(drug.getMedicine_name());
                pstmt = conn.prepareStatement("INSERT INTO inventory(medicine_name, quantity) VALUES(?, ?)");
                pstmt.setString(1, drug.getMedicine_name());
                pstmt.setInt(2, drug.getQuantity());
                pstmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally{
            ConnectionManager.close(conn, pstmt, rs);
        }
    }
    
    public static boolean addDrug(Drug dr){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("INSERT INTO inventory VALUES (?, ?, ?)");
            stmt.setInt(1, dr.getID());
            stmt.setString(2, dr.getMedicine_name());
            stmt.setInt(3, dr.getQuantity());
            stmt.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            ConnectionManager.close(conn, stmt, rs);
        }
    }
    
    public static boolean updateInventoryStatus(int orderID){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("update orders set status='APPROVED', action_time = ? where order_id = ?");
            stmt.setString(1, DateUtility.getCurrentDate());
            stmt.setInt(2, orderID);
            stmt.executeUpdate();
            
            return true;
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static void updateInventory(Order order){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("update inventory set quantity = quantity-? where medicine_name = ?");
            stmt.setDouble(1, order.getQuantity());
            stmt.setString(2, order.getMedicine());
            stmt.executeUpdate();
            
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }
    
    public static ArrayList<Order> getOrdersByVisitID(int visit_id){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Order> orderList = new ArrayList<Order>();
        ConsultDAO consultDAO = new ConsultDAO();
        VisitDAO visitDAO = new VisitDAO();
        
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT o.order_id, visit_id, medicine_name, quantity, notes, remarks FROM orders o INNER JOIN orderlist ol ON o.order_id = ol.order_id WHERE visit_id = ?");
            stmt.setInt(1, visit_id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int orderID = rs.getInt("order_id");
                rs.getInt("visit_id");
                String medicine_name = rs.getString("medicine_name");
                int quantity = rs.getInt("quantity");
                String notes = rs.getString("notes");
                String remarks = rs.getString("remarks");
                
                orderList.add(new Order(orderID,consultDAO.getConsultByVisitID(visit_id).getDoctor(), visitDAO.getVisitByVisitID(visit_id).getPatientId(), medicine_name, quantity, notes, remarks));
            }

            return orderList;
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return orderList;
    }
    
    public static boolean rejectOrders(int orderID){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("update orders set status='REJECTED', action_time = ? where order_id = ?");
            stmt.setString(1, DateUtility.getCurrentDate());
            stmt.setInt(2, orderID);
            stmt.executeUpdate();

            return true;
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean rejectPrevApproved(ArrayList<Order> orderList){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        int orderID = 0;
        
        try {
            for(Order ord : orderList){
                ord.setQuantity(ord.getQuantity() * (-1));
                updateInventory(ord);
                orderID = ord.getOrderID();
            }
            
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("update orders set status='REJECTED', action_time = ? where order_id = ?");
            stmt.setString(1, DateUtility.getCurrentDate());
            stmt.setInt(2, orderID);
            stmt.executeUpdate();

            return true;
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean hideOrders(int orderID){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("update orders set status='HIDDEN' where order_id = ?");
            stmt.setInt(1, orderID);
            stmt.executeUpdate();

            return true;
            //Returns the converted array to the caller of method

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
}
