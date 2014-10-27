package com.jonasalejo.appcap;

public class PlanRow {

    String planName;

    public PlanRow(String planName) {
        super();
        this.planName = planName;
    }
    public String getItemName() {
        return planName;
    }
    public void setPlanName(String itemName) {
        this.planName = itemName;
    }

}