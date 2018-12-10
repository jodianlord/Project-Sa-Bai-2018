package model;

public class Vitals {
    private int visitId;
    private double height;
    private double weight;
    private int systolic;
    private int diastolic;
    private double temperature;
    private int hivPositive;
    private int ptbPositive;
    private int hepCPositive;
    private int heartRate;

    public Vitals(int visitId, double height, double weight, int systolic, int diastolic, double temperature, int hivPositive, int ptbPositive, int hepCPositive, int heartRate) {
        this.visitId = visitId;
        this.height = height;
        this.weight = weight;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.temperature = temperature;
        this.hivPositive = hivPositive;
        this.ptbPositive = ptbPositive;
        this.hepCPositive = hepCPositive;
        this.heartRate = heartRate;
    }
    
    public Vitals(int visitId, double height, double weight, int systolic, int diastolic, double temperature, int heartRate) {
        this.visitId = visitId;
        this.height = height;
        this.weight = weight;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.temperature = temperature;
        this.heartRate = heartRate;
    }
    
    public int getVisitId() {
        return visitId;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public int getSystolic() {
        return systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public int getHivPositive() {
        return hivPositive;
    }
    
    public int getPtbPositive() {
        return ptbPositive;
    }
    
    public int getHepCPositive() {
        return hepCPositive;
    }
    
    public int getHeartRate(){
        return heartRate;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHivPositive(int hivPositive) {
        this.hivPositive = hivPositive;
    }

    public void setPtbPositive(int ptbPositive) {
        this.ptbPositive = ptbPositive;
    }
    
    public void setHepCPositive(int hepCPositive) {
        this.hepCPositive = hepCPositive;
    }
    
    public void setHeartRate(int heartRate){
        this.heartRate = heartRate;
    }
}