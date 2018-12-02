/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Kwtam
 */
public class Drug {
    private String medicine_name;
    private int quantity;
    private int id;
    
    public Drug(int id, String medicine_name, int quantity) {
        this.medicine_name = medicine_name;
        this.quantity = quantity;
        this.id = id;
    }
    
    public int getID(){
        return id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
