package com.example.easyblog;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {


    Context context;

    ArrayList<UserPosts> list;

    public PostAdapter(Context context, ArrayList<UserPosts> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.postitems,parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String dateset, timeset;
        UserPosts userPosts = list.get(position);
        dateset = userPosts.getTime().substring(0,10);
        timeset = userPosts.getTime().substring(userPosts.getTime().length()-8);
        holder.uname.setText(userPosts.getUname());
        holder.email.setText(userPosts.getEmail());
        holder.title.setText(userPosts.getTitle());
        holder.time.setText(timeset);
        holder.date.setText(dateset);
        holder.description.setText(userPosts.description);
        Picasso.get().load(userPosts.getProfile_image()).into(holder.profile_image);
        Picasso.get().load(userPosts.getUimage()).into(holder.uimage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView uname, email, title, date, time, description;
        ImageView profile_image, uimage;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            uname = itemView.findViewById(R.id.post_user_name);
            email = itemView.findViewById(R.id.user_email);
            title = itemView.findViewById(R.id.post_title);
            date = itemView.findViewById(R.id.post_date);
            description = itemView.findViewById(R.id.post_description);
            profile_image = itemView.findViewById(R.id.post_profile_image);
            uimage = itemView.findViewById(R.id.post_image);
            time = itemView.findViewById(R.id.post_time);


        }
    }

}
