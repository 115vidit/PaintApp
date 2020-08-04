package com.example.paintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Objects;

public class StartActivity extends AppCompatActivity {

    ImageButton imageButton;
    int check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        imageButton = findViewById(R.id.imageButton);
        menu();
    }

    private void menu() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(StartActivity.this);
                dialog.setTitle("");
                dialog.setContentView(R.layout.menu);
                dialog.show();

                final Button Camera = dialog.findViewById(R.id.camera);
                Button Gallery = dialog.findViewById(R.id.gallery);
                Button Pdf = dialog.findViewById(R.id.pdf);

                Camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       check = 1;
                       open();
                        dialog.dismiss();

                    }
                });

                Gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        check = 2;
                        open();
                        dialog.dismiss();
                    }
                });

                Pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        check = 3;
                        open();
                        dialog.dismiss();
                    }

                });
            }
        });
    }

    private void open() {
        Intent i = new Intent(StartActivity.this, ImageActivity.class);
        i.putExtra("chk",check);
        startActivity(i);
    }
}