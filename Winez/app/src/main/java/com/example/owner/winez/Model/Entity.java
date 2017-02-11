package com.example.owner.winez.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ziv on 28/01/2017.
 */

public abstract class Entity {
    private String uid;
    private long saveTimeStamp;

    public Entity(){
        this.setUid("");
    }
    public Entity(String uid){
        this.uid = uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUid() { return uid; }

    public long getSaveTimeStamp(){
        return this.saveTimeStamp;
    }

    public void setSaveTimeStamp(long timeStamp){
        this.saveTimeStamp = timeStamp;
    }


    public Map<String,Object> toMap(){
        Map<String,Object> toRet = new HashMap<>();
        toRet.put("uid", this.getUid());
        toRet.put("saveTimeStamp", this.getSaveTimeStamp());

        return toRet;
    }

}
