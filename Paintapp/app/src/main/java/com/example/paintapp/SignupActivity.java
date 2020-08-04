package com.example.paintapp;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText name, email, password;
    private Button regi;
    private ProgressBar progressBar;
    private static String URL_REG ="http://192.168.43.130/paint/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.nm);
        email = findViewById(R.id.em);
        password = findViewById(R.id.pw);
        regi = findViewById(R.id.reg);
        TextView reg = findViewById(R.id.ln);

        regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void register() {
        progressBar.setVisibility(View.VISIBLE);
        regi.setVisibility(View.GONE);

        final String name = this.name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if(success.equals("1")){
                        Toast.makeText(SignupActivity.this,"Register Succesful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this,"Register Error!" + e.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    regi.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignupActivity.this,"Register Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                regi.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        requestQueue.add(stringRequest);
    }
}
