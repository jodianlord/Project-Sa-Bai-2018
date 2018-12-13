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
import model.Queue;
import util.DateUtility;

/**
 *
 * @author Jordy
 */
public class QueueDAO {
    public static boolean updateQueue(int patientID, int visitID, String status){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if(status == null){
            return false;
        }
        try{
           conn = ConnectionManager.getConnection();
           if(status.equals("REGISTERED")){
               stmt = conn.prepareStatement("INSERT INTO queue_status values(?,?,?,?)");
               stmt.setInt(1, patientID);
               stmt.setInt(2, visitID);
               stmt.setString(3, DateUtility.getCurrentDate());
               stmt.setString(4, status);
           }else{
               stmt = conn.prepareStatement("UPDATE queue_status SET timestamp = ?, status = ? WHERE visit_id = ?");
               stmt.setString(1, DateUtility.getCurrentDate());
               stmt.setString(2, status);
               stmt.setInt(3, visitID);
           }
           int rows = stmt.executeUpdate();
           if(rows > 0){
               return true;
           }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static ArrayList<Queue> getQueue(){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Queue> queueList = new ArrayList<>();
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT patient_id, visit_id, timestamp, status, name FROM queue_status inner join patients ON queue_status.patient_id = patients.id");
            rs = stmt.executeQuery();
            while(rs.next()){
                int patientID = rs.getInt("patient_id");
                int visitID = rs.getInt("visit_id");
                String timestamp = rs.getString("timestamp");
                String status = rs.getString("status");
                String name = rs.getString("name");
                Queue q = new Queue(patientID, visitID, timestamp, status, name);
                queueList.add(q);
            }
            return queueList;
        }catch(SQLException e){
            e.printStackTrace();
            return queueList;
        }finally{
            ConnectionManager.close(conn, stmt, rs);
        }
    }
}
