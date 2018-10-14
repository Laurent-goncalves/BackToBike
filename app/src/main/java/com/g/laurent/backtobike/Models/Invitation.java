package com.g.laurent.backtobike.Models;

import java.util.ArrayList;

public class Invitation {

    private String date, time, comments;
    private int idRoute;
    private ArrayList<String> listIdFriends;

    public Invitation() {
    }

    public Invitation(String date, String time, String comments, int idRoute, ArrayList<String> listIdFriends) {
        this.date = date;
        this.time = time;
        this.comments = comments;
        this.idRoute = idRoute;
        this.listIdFriends = listIdFriends;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public ArrayList<String> getListIdFriends() {
        return listIdFriends;
    }

    public void setListIdFriends(ArrayList<String> listIdFriends) {
        this.listIdFriends = listIdFriends;
    }
}
