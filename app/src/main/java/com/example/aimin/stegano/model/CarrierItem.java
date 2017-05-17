package com.example.aimin.stegano.model;

/**
 * Created by aimin on 2017/5/15.
 */

public class CarrierItem {
    public int datebaseId;
    public String userId;
    public String username;
    public double size;
    public int storage;
    public String filepath;
    public String inserttime;

    public String toString(){
        return "a CarrierItem, databaseId:"+datebaseId;
    }
}
