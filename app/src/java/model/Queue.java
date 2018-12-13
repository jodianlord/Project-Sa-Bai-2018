/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jordy
 */
public class Queue {
    private int patientID;
    private int visitID;
    private String timestamp;
    private String status;
    private String name;

    public String getName() {
        return name;
    }

    public int getPatientID() {
        return patientID;
    }

    public int getVisitID() {
        return visitID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }
    
    public Queue(int patientID, int visitID, String timestamp, String status, String name){
        this.patientID = patientID;
        this.visitID = visitID;
        this.timestamp = timestamp;
        this.status = status;
        this.name = name;
    }
}
