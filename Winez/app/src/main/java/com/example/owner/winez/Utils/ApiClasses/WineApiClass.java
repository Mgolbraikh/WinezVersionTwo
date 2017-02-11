package com.example.owner.winez.Utils.ApiClasses;

/**
 * Created by owner on 04-Feb-17.
 */

public class WineApiClass {
    private String Id;
    private String Name;
    private String picture;
    private String Type;
    private String Rating;
    private Double PriceRetail;
    private String Vintage;



    public WineApiClass(String id, String name) {
        Id = id;
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public Double getPriceRetail() {
        return PriceRetail;
    }

    public void setPriceRetail(Double priceRetail) {
        PriceRetail = priceRetail;
    }

    public String getVintage() {
        return Vintage;
    }

    public void setVintage(String vintage) {
        Vintage = vintage;
    }
}
