package com.example.paintapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter{

    private Context ctx;
    private ArrayList<Uri> mArrayUri;
   // private ArrayList<Boolean> checkBoxes;
    private ViewHolder viewHolder;
    boolean[] selection;
   // private String[] arrPath = new String[getCount()];
    private boolean checked = false;

    GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri) {
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
        this.selection = new boolean[getCount()];
        //checkBoxes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mArrayUri.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public static class ViewHolder{
        ImageView imageView;
        CheckBox check;
    }


    boolean toggleChecks(String t) {
        if(t.equals("Select"))
            checked = true;
        else {
            Log.d("yo", "yo");
            checked = false;
        }
        notifyDataSetChanged();
        return checked;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            assert inflater != null;
            convertView = inflater.inflate(R.layout.pics, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.check = convertView.findViewById(R.id.chk);
            viewHolder.imageView = convertView.findViewById(R.id.picture);
            viewHolder.check.setVisibility(View.INVISIBLE);
            convertView.setTag(viewHolder);
            //convertView.setTag(R.id.chk, viewHolder.check);
            //convertView.setTag(R.id.picture, viewHolder.imageView);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //viewHolder.check.setTag(position);
        //checkBoxes.add(viewHolder.check);
        //viewHolder.check.setChecked(checked);
        //ImageView imageView = convertView.findViewById(R.id.picture);
        viewHolder.check.setId(position);
        viewHolder.imageView.setId(position);
        if(checked){
            viewHolder.check.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.check.setVisibility(View.GONE);
        }
        viewHolder.check.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if (selection[id]){
                    cb.setChecked(false);
                    selection[id] = false;
                } else {
                    cb.setChecked(true);
                    selection[id] = true;
                }
            }
        });

        if(position == mArrayUri.size())
        {
            Glide.with(ctx).load(R.mipmap.add).placeholder(R.mipmap.progress_image).into(viewHolder.imageView);
            //       imageView.setImageResource(R.mipmap.add);
            return  convertView;
        }
        Glide.with(ctx).load(mArrayUri.get(position)).placeholder(R.mipmap.progress_image).into(viewHolder.imageView);
        viewHolder.check.setChecked(selection[position]);
        //   imageView.setImageURI(mArrayUri.get(position));
        return convertView;
    }
    void clear_selection(){
        CheckBox cb = viewHolder.check;
        int id = cb.getId();
        cb.setChecked(false);
        selection[id] = false;
    }

}
