package com.example.tushar.mc_final;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tushar on 4/29/18.
 */

public class Data {
    private ArrayList<HashMap<String,String>> display_data;
    private int layout_type;
    private String Building,Floor;
    private String name;
    private int frag_type;
    private ArrayList<String> visiblity;

    public void setVisiblity(ArrayList<String> visiblity) {
        this.visiblity = visiblity;
    }

    public ArrayList<String> getVisiblity() {
        return visiblity;
    }

    public void setFrag_type(int frag_type) {
        this.frag_type = frag_type;
    }

    public int getFrag_type() {
        return frag_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuilding(String building) {
        Building = building;
    }

    public void setDisplay_data(ArrayList<HashMap<String,String>> display_data) {
        this.display_data = display_data;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }

    public void setLayout_type(int layout_type) {
        this.layout_type = layout_type;
    }

    public int getLayout_type() {
        return layout_type;
    }

    public String getBuilding() {
        return Building;
    }

    public String getFloor() {
        return Floor;
    }

    public ArrayList<HashMap<String,String>> getDisplay_data() {
        return display_data;
    }

}
