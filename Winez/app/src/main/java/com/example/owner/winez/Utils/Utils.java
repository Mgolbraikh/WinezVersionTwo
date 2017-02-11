package com.example.owner.winez.Utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.owner.winez.MyApplication;


public class Utils {

    private static Utils _instance;
    private RequestQueue queue;
    private Utils(){ this.queue = Volley.newRequestQueue(MyApplication.getAppContext()); }

    public static Utils getInstance() {
        if(_instance == null){
            _instance = new Utils();
        }
        return _instance;
    }

    public void hasActiveInternetConnection(final CheckConnectivity connectivityLisenner) {
        String url = "http://clients3.google.com/generate_204";

        StringRequest str = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                connectivityLisenner.onResult(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                connectivityLisenner.onResult(false);
            }
        });

        this.queue.add(str);
    }

    public interface CheckConnectivity{
        void onResult(boolean result);
    }
}

