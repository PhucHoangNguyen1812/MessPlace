package com.doanjava.messbcode;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GuiThongBao {
    public static void GuiThongBao(String ten, String tin , String token, Context context) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", ten);
            data.put("body", tin);

            JSONObject thongbao = new JSONObject();
            thongbao.put("notification",data);
            thongbao.put("to",token);

            JsonObjectRequest request = new JsonObjectRequest(url, thongbao
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Toast.makeText(NhanTin.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String ,String> map = new HashMap<>();
                    String key = "Key=AAAA8NUrIDc:APA91bHNbOtDbeO-Sq6_qQYVA7gTWy5RmrAVI7CAxHejA2unCSypQFywQRiZmrUrvMGP2zjTi9jEX3XuBjGdD4VJULkKg3YoaexdEoIrYliV6_5hy7c0XMfLvkCVjV0zc4sWZZMwbmKX";
                    map.put("Content-Type","application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };

            queue.add(request);
        }
        catch (Exception ex) {

        }
    }
}
