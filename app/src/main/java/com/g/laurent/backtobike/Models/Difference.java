package com.g.laurent.backtobike.Models;

public class Difference {

    private String difference;
    private String idEvent;

    public Difference(String difference, String idEvent) {
        this.difference = difference;
        this.idEvent = idEvent;
    }

    public String getDifference() {
        return difference;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }
}
