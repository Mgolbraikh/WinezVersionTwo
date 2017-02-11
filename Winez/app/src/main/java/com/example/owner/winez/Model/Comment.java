package com.example.owner.winez.Model;

import java.util.Map;

/**
 * Created by Ziv on 28/01/2017.
 */
public class Comment extends Entity {
    private String wineID;
    private String userID;
    private String text;
    private String userName;

    public Comment(){

    }
    public Comment(String wineID, String userID, String text, String userName) {
        this.wineID = wineID;
        this.userID = userID;
        this.text = text;
        this.userName = userName;
    }

    public String getUserID(){
        return this.userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public String getText(){
        return this.text;
    }

    public void setText(String text){
        this.text = text;
    }
    public String getWineID() {
        return wineID;
    }

    public void setWineID(String wineID) {
        this.wineID = wineID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Map<String,Object> toMap(){
        Map<String,Object> toRet = super.toMap();
        toRet.put("userID", this.getUserID());
        toRet.put("wineID",this.getWineID());
        toRet.put("text", this.getText());
        toRet.put("userName", this.getUserName());
        return toRet;
    }
}
