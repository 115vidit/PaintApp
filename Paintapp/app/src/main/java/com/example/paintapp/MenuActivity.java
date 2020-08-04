package com.example.paintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sessionManager = new SessionManager(this);
        sessionManager.checklogin();

        TextView name = findViewById(R.id.n);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(SessionManager.NAME);

        name.setText(mName);

        ImageButton profile = findViewById(R.id.profile);
        profile.setOnClickListener(this);

        ImageButton start = findViewById(R.id.start);
        start.setOnClickListener(this);

        ImageButton open = findViewById(R.id.open);
        open.setOnClickListener(this);

        ImageButton contact = findViewById(R.id.contact);
        contact.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.profile:
                i = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(i);
                break;

            case R.id.start:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                String title = "Let's go full-screen";
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(0xff33b5e5);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                alertDialogBuilder.setTitle(spannableStringBuilder);
                alertDialogBuilder.setIcon(R.mipmap.rotate);
                alertDialogBuilder.setMessage("Orientation of your phone will be now changed to landscape.");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent i = new Intent(MenuActivity.this, StartActivity.class);
                        startActivity(i);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            case R.id.open:
                i = new Intent(MenuActivity.this, OpenActivity.class);
                startActivity(i);
                break;

            case R.id.contact:
                i = new Intent(MenuActivity.this, ContactActivity.class);
                startActivity(i);
                break;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            sessionManager.logout();
        }
        return super.onOptionsItemSelected(item);
    }
}
