
package com.example.navigation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    //Third Step
    private HashMap<String,String> parseJsonObject(JSONObject object){
        HashMap<String,String> datalist = new HashMap<>();
        try {
            String name = object.getString("name");
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            String longtitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            datalist.put("name", name);
            datalist.put("lat", latitude);
            datalist.put("lng", longtitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }

    //Second Step
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> datalist = new ArrayList<>();
        for (int i = 0; i<jsonArray.length(); i++){
            try {
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }

    //First Step
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArray(jsonArray);
    }
}
