package com.example.paintapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ImageActivity extends AppCompatActivity {

    int PICK_IMAGE_MULTIPLE = 1;
    private GalleryAdapter galleryAdapter;
    private GridView gridView;
    ArrayList<Uri> mArrayUri = new ArrayList<>();
    String imageEncoded;
    List<String> imagesEncodedList;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 13;
    int REQUEST_CAMERA = 0;
    int REQUEST_Pdf = 2;
    int pos, dragPosition;
    ClipData dragData;
    Object current = null;
    LinearLayout linearLayout;
    Timer timer;
    int yLocation;
    ImageView trashZone;
    Drawable originalBackground;
    ImageView arrow;
    CheckBox checkBox;
    String t = "Select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);

        linearLayout = findViewById(R.id.lay);
        trashZone = findViewById(R.id.trash);
        gridView = findViewById(R.id.gv);
        arrow = findViewById(R.id.arrow);
        checkBox = findViewById(R.id.chk);

        int check = getIntent().getIntExtra("chk", 0);
        if (check == 2) {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            gallery();
        } else if (check == 1) {
            camera();
        } else if (check == 3) {
            pdf();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mArrayUri.size()) {
                    menu();
                }
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                current = view;
                if (position > AdapterView.INVALID_POSITION) {
                    // ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                    // dragData = new ClipData((CharSequence) view.getTag(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                    final View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
                    view.startDrag(dragData, myShadow, null, 0);
                    linearLayout.setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View v, DragEvent dragEvent) {
                            v = (View) current;
                            boolean result = true;
                            int action = dragEvent.getAction();
                            switch (action) {
                                case DragEvent.ACTION_DRAG_STARTED:
                                    break;
                                case DragEvent.ACTION_DRAG_ENTERED:
                                case DragEvent.ACTION_DRAG_ENDED:
                                    if (timer != null) {
                                        timer.cancel();
                                        timer.purge();
                                        timer = null;
                                    }
                                    break;
                                case DragEvent.ACTION_DRAG_EXITED:
                                    if (yLocation < 50) {
                                        scroll(-40);
                                    } else if (yLocation > 950) {
                                        scroll(40);
                                    }
                                    break;
                                case DragEvent.ACTION_DROP:
                                    int dropX = (int) dragEvent.getX();
                                    int dropY = (int) dragEvent.getY();
                                    dragPosition = gridView.pointToPosition(dropX, dropY);
                                    int trashY = (int) trashZone.getY();
                                    if (dragEvent.getLocalState() == v) {
                                        return false;
                                    } else if (yLocation > trashY) {
                                        originalBackground = ((View) current).getBackground();
                                        ((View) current).setBackgroundColor(Color.RED);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                                        builder.setMessage("Do you really want to delete this image")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ((View) current).setBackgroundDrawable(originalBackground);
                                                        mArrayUri.remove(pos);
                                                        for (int i = 0; i < mArrayUri.size() - pos; i++) {
                                                            mArrayUri.get(pos + i);
                                                        }
                                                        galleryAdapter.notifyDataSetChanged();
                                                    }
                                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((View) current).setBackgroundDrawable(originalBackground);
                                                dialog.cancel();
                                            }
                                        });
                                        builder.create().show();
                                    } else {
                                        try {
                                            Uri uri = mArrayUri.get(pos);
                                            Uri u = mArrayUri.get(dragPosition);
                                            mArrayUri.remove(pos);
                                            mArrayUri.add(pos, u);
                                            mArrayUri.remove(dragPosition);
                                            mArrayUri.add(dragPosition, uri);
                                            galleryAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.fillInStackTrace();
                                        }
                                    }
                                    break;
                                case DragEvent.ACTION_DRAG_LOCATION:
                                    yLocation = (int) dragEvent.getY();
                                    break;
                                default:
                                    result = false;
                                    break;
                            }
                            return result;
                        }
                    });
                }
                return false;
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), viewpager.class);
                i.putParcelableArrayListExtra("array", mArrayUri);
                startActivity(i);

            }
        });
    }

    private void scroll(final int d) {

        TimerTask startScroll = new TimerTask() {
            @Override
            public void run() {
                gridView.smoothScrollBy(d, 2);
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(startScroll, 0, 50);
    }

    private void menu() {
        final Dialog dialog = new Dialog(ImageActivity.this);
        dialog.setTitle("Choose an Option");
        dialog.setContentView(R.layout.menu);
        dialog.show();

        Button Camera = dialog.findViewById(R.id.camera);
        Button Gallery = dialog.findViewById(R.id.gallery);
        Button Pdf = dialog.findViewById(R.id.pdf);


        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera();
                dialog.dismiss();
            }
        });

        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery();
                dialog.dismiss();
            }
        });

        Pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdf();
                dialog.dismiss();
            }

        });
    }

    public void gallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    public void camera() {
        if (checkPermissionWrite_EXTERNAL_STORAGE(this)) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    public void pdf() {
        if (checkPermissionRead_EXTERNAL_STORAGE(this) && checkPermissionWrite_EXTERNAL_STORAGE(this)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_Pdf);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                    gridView.setAdapter(galleryAdapter);
                    gridView.setVerticalSpacing(gridView.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gridView
                            .getLayoutParams();
                    mlp.setMargins(0, gridView.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            assert cursor != null;
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                            gridView.setAdapter(galleryAdapter);
                            gridView.setVerticalSpacing(gridView.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gridView
                                    .getLayoutParams();
                            mlp.setMargins(0, gridView.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
                assert data != null;
                Bitmap bitmap;
                bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Titile", null);
                mArrayUri.add(Uri.parse(path));
                galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                gridView.setAdapter(galleryAdapter);
                gridView.setVerticalSpacing(gridView.getHorizontalSpacing());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gridView
                        .getLayoutParams();
                mlp.setMargins(0, gridView.getHorizontalSpacing(), 0, 0);
            } else if (requestCode == REQUEST_Pdf && resultCode == RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                assert uri != null;
                try {
                    PdfiumCore pdfiumCore = new PdfiumCore(this);
                    Bitmap bitmap;
                    ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(uri, "r");
                    PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
                    int pageCount = pdfiumCore.getPageCount(pdfDocument);
                    for (int i = 0; i < pageCount; i++) {
                        pdfiumCore.openPage(pdfDocument, i);
                        int width = pdfiumCore.getPageWidthPoint(pdfDocument, i);
                        int height = pdfiumCore.getPageHeightPoint(pdfDocument, i);
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0, width, height);
                        String p = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Titile", null);
                        mArrayUri.add(Uri.parse(p));
                        galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                        gridView.setAdapter(galleryAdapter);
                        gridView.setVerticalSpacing(gridView.getHorizontalSpacing());
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gridView
                                .getLayoutParams();
                        mlp.setMargins(0, gridView.getHorizontalSpacing(), 0, 0);
                    }
                    pdfiumCore.closeDocument(pdfDocument);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                Toast.makeText(this, "You haven't picked file", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkPermissionWrite_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public boolean checkPermissionRead_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("yo", "yo");
                }
            else {
                Toast.makeText(ImageActivity.this, " Denied",Toast.LENGTH_SHORT).show();
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.select){
            //galleryAdapter.set();
           if(galleryAdapter.toggleChecks(t)) {
               t = "Unselect";
               item.setTitle(t);
           }
            else {
               t = "Select";
               item.setTitle(t);
            }
        }
        else if(item.getItemId() == R.id.delete){
            final int len = galleryAdapter.selection.length;
            int cnt = 0;
            List<Uri> indices = new ArrayList<>();
            String selectImages = "";
            for (int i = 0; i<len; i++)
            {
                if (galleryAdapter.selection[i]){
                    //String  selected = mArrayUri.get();
                    indices.add(mArrayUri.get(i));
                    cnt++;
                  //  selectImages = selectImages + galleryAdapter.arrPath[i] + "|";
                }
            }
            if (cnt == 0){
                Toast.makeText(getApplicationContext(),
                        "Please select at least one image",
                        Toast.LENGTH_LONG).show();
            } else {
                for(Uri i:indices){
                    mArrayUri.remove(i);
                    //System.out.println(indices);
                }
                indices.clear();
                galleryAdapter.notifyDataSetChanged();
                galleryAdapter.clear_selection();
                Toast.makeText(getApplicationContext(),
                        "You've selected Total " + cnt + " image(s).",
                        Toast.LENGTH_LONG).show();
                Log.d("SelectedImages", selectImages);
            }
            //galleryAdapter.removeselected();
        }
        return super.onOptionsItemSelected(item);
    }
}
