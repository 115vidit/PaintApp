package com.example.paintapp;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private ProgressBar progressBar;
    private Button login_button;
    SessionManager sessionManager;
    private static String URL_LOG ="http://192.168.43.130/paint/login.php";
    //private static String URL_LOG = "http://localhost/paint/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        progressBar = findViewById(R.id.progressBar);
        email = findViewById(R.id.eml);
        password = findViewById(R.id.pwd);
        login_button = findViewById(R.id.login);
        TextView ln = findViewById(R.id.sp);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if(!mEmail.isEmpty() || !mPassword.isEmpty()){
                    Login(mEmail, mPassword);
                }else{
                    email.setError("Please insert Email");
                    password.setError("Please insert Password");

                }
            }
        });

        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void Login(final String email, final String password){
        progressBar.setVisibility(View.VISIBLE);
        login_button.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    if(success.equals("1")){

                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String name = object.getString("name").trim();
                            String email = object.getString("email").trim();

                            Toast.makeText(LoginActivity.this,"Success Login. \nYour Name : " +name + "\nYour Email : " + email, Toast.LENGTH_SHORT).show();

                            sessionManager.createSession(name, email);
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    login_button.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this,"Error " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        login_button.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this,"Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }
}
