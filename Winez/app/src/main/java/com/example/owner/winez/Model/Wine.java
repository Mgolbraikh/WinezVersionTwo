package com.example.owner.winez.Model;

import com.example.owner.winez.Utils.ApiClasses.WineApiClass;

import java.util.Map;

/**
 * Created by owner on 28-Jan-17.
 */

public class Wine extends Entity {

    public Wine(String name) {
        this.name = name;
    }

    public Wine(WineApiClass fromWine){
        this.setName(fromWine.getName());
        this.setPrice(fromWine.getPriceRetail());
        this.setType(fromWine.getType());
        this.setUid(fromWine.getId());
        this.setVintage(fromWine.getVintage());
    }

    public Wine(){

    }
    public Wine(String name, String uid) {
        super(uid);
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    private String name;
    private double price;
    private String type;
    private String vintage;
    private  String picture;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVintage() {
        return vintage;
    }

    public void setVintage(String vintage) {
        this.vintage = vintage;
    }

    @Override
    public Map<String,Object> toMap(){
        Map<String,Object> map = super.toMap();
        map.put("price",getPrice());
        map.put("type",getType());
        map.put("name",getName());
        map.put("vintage",getVintage());

        // Skipping picture if null
        if (getPicture() != null) {
            map.put("picture", getPicture());
        }
        return map;
    }

}
