package com.example.owner.winez.Utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.owner.winez.MyApplication;
import com.example.owner.winez.Utils.ApiClasses.WineApiClass;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WineApi {
    public final int RED_WINE = 124;
    public final int WHITE_WINE = 125;
    public final int CHAMPANGE_SPARKLINGS = 123;
    public final int ROSE_WINE = 126;
    public final int DESERET_WINE = 128;
    public final int SAKE_WINE = 134;
    private final String apikey = "5c27d5820f7f571efd0b10dad310fcb7";
    private RequestQueue queue;


    private static WineApi targetURL;
    private WineApi() {
        this.queue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static WineApi getInstance() {
        if (targetURL == null) {
            targetURL = new WineApi();
        }
        return targetURL;
    }

    /**
     * @param getOnCompleteResult - Result event
     */
    public void GetWinesByCategory(final GetResultOnResponse<WineApiClass> getOnCompleteResult) {

        String url = "http://services.wine.com/api/beta2/service.svc/JSON/catalog?sort=(rating|ascending)&size=100&apikey=" + this.apikey;
        final ArrayList<WineApiClass> answer = new ArrayList<>();

        // Request a string response from the provided URL.
        //StringRequest stringRequest = new StringRequest(
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        // Display the first 500 characters of the response string.
                        Log.d("Response:", "onResponse: this worked  +  " + response);
                        try {
                            JSONArray productsarray = response.getJSONObject("Products").getJSONArray("List");
                            for (int index = 0; index < productsarray.length(); index++) {
                                WineApiClass newWine = gson.fromJson(productsarray.get(index).toString(), WineApiClass.class);
                                newWine.setRating(productsarray.getJSONObject(index).getJSONObject("Ratings").get("HighestScore").toString());
                                answer.add(newWine);
                            }
                            getOnCompleteResult.onResult(answer);

                        } catch (Exception e) {
                            Log.d("Error:", "this is error parsing array = " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR:", "Error lisiner on response : " + error.getMessage());
            }
        });

        this.queue.add(jor);
    }

    public interface GetResultOnResponse<T> {
        void onResult(ArrayList<T> data);

        void onCancel();
    }
}
