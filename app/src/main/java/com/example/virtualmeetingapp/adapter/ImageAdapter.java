package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.ImageModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Context context;
    List<ImageModel> mList;

    public ImageAdapter(Context context, List<ImageModel> mList) {
        this.context = context;
        this.mList = mList;
    }


    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, int position) {


        holder.textView.setText(mList.get(position).getImagename());
        Picasso.get().load(mList.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.imageView);
        if(holder.textView.getText() == null)
        {
            holder.noImage.setVisibility(View.VISIBLE);
        }
//        holder.noImage.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView, noImage;
        ImageView imageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.filename);
            imageView= itemView.findViewById(R.id.icon);
            noImage  = itemView.findViewById(R.id.noImage);
        }
    }
}
