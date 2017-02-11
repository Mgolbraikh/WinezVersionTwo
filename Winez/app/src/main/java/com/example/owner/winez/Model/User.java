package com.example.owner.winez.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by owner on 28-Jan-17.
 */

public class User extends Entity {

    private String name;
    private String email;
    private Map<String,String> userWines;

    public User(){
        this.userWines = new HashMap<>();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> toRet = super.toMap();
        toRet.put("name", this.getName());
        toRet.put("email", this.getEmail());
        toRet.put("userWines",this.getUserWines());
        return toRet;
    }

    public User(String name, String email, String uid) {
        super(uid);
        this.name = name;
        this.email = email;
        this.userWines = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Key is wine uid and value is wine title.
     * Returns an empty if none exists
     * @return
     */
    public Map<String,String> getUserWines() {
        return userWines;
    }


}
