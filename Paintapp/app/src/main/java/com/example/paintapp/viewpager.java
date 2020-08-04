package com.example.paintapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Objects;
import android.content.Context;
import android.net.Uri;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;


public class viewpager extends AppCompatActivity {

    private PaintCanvas drawView;
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton currPaint;
    ViewPager viewPager;
    CustomPageAdapter mCustomPageAdapter;
    ArrayList<Uri> mArrayUri;
    int pos;
    public View btnNext, btnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_viewpager);

        mArrayUri = getIntent().getParcelableArrayListExtra("array");
        pos = Objects.requireNonNull(getIntent().getExtras()).getInt("id",0);
        mCustomPageAdapter = new CustomPageAdapter(this);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mCustomPageAdapter);
        viewPager.setCurrentItem(pos);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //drawing_board.reset();
            }
        });
        btnNext = findViewById(R.id.next);
        btnPrev = findViewById(R.id.prev);
        btnPrev.setOnClickListener(onClickListener(0));
        btnNext.setOnClickListener(onClickListener(1));

        drawView = findViewById(R.id.drawing);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        ImageButton paint = findViewById(R.id.bn);
        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(viewpager.this);
                dialog.setTitle("Choose Color");
                dialog.setContentView(R.layout.pallet);
                LinearLayout paintLayout = dialog.findViewById(R.id.paint_colors);
                currPaint = (ImageButton)paintLayout.getChildAt(0);
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
                ImageButton smallBtn = dialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        drawView.setErase(false);
                        dialog.dismiss();
                    }
                });
                ImageButton mediumBtn = dialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        drawView.setErase(false);
                        dialog.dismiss();
                    }
                });

                ImageButton largeBtn = dialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        drawView.setErase(false);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        drawView.setBrushSize(smallBrush);

        ImageButton eraseBtn = findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog brushDialog = new Dialog(viewpager.this);
                brushDialog.setTitle("Eraser size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                Button erase_all = brushDialog.findViewById(R.id.erase_all);
                erase_all.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.Erase_all();
                        brushDialog.dismiss();
                    }
                });


                brushDialog.show();
            }
        });

    }
    public void paintClicked(View view){
        //use chosen color
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if(view!=currPaint){
//update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    public class CustomPageAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        CustomPageAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mArrayUri.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ( object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            pos = position;
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = itemView.findViewById(R.id.imv);
            Glide.with(getApplicationContext()).load(mArrayUri.get(position)).placeholder(R.mipmap.progress_image).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private View.OnClickListener onClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i > 0) {
                    //next page
                    if (viewPager.getCurrentItem() < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                } else {
                    //previous page
                    if (viewPager.getCurrentItem() > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    }
                }
            }
        };

    }
}
