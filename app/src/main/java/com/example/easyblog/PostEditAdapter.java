package com.example.easyblog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostEditAdapter extends RecyclerView.Adapter<PostEditAdapter.MyEditViewHolder> {


    Context context;
    ArrayList<UserPostsEdit> posteditlist;

    public PostEditAdapter(Context context, ArrayList<UserPostsEdit> posteditlist) {
        this.context = context;
        this.posteditlist = posteditlist;
    }

    @NonNull
    @Override
    public MyEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.posteditlist,parent,false);
        return new MyEditViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyEditViewHolder holder, int position) {
        String dateset, timeset, postkey ;
        UserPostsEdit user = posteditlist.get(position);
        postkey = user.getPost_key();
        dateset = user.getTime().substring(0,10);
        timeset = user.getTime().substring(user.getTime().length()-8);
        holder.title.setText(user.getTitle());
        holder.description.setText(user.getDescription());
        holder.date.setText(dateset);
        holder.time.setText(timeset);
        Picasso.get().load(user.getUimage()).into(holder.postimage);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditPostActivity.class);
                intent.putExtra("postkey",postkey);
                context.startActivity(intent);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference user_posts_reference = FirebaseDatabase.getInstance().getReference().child("userposts");
                user_posts_reference.child(postkey).removeValue();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return posteditlist.size();
    }

    public static  class MyEditViewHolder extends RecyclerView.ViewHolder{


        Button delete, edit;
        ImageView postimage;
        TextView title, description , date , time;


        public MyEditViewHolder(@NonNull View itemView) {
            super(itemView);

            delete = itemView.findViewById(R.id.delete_post);
            edit = itemView.findViewById(R.id.edit_post);
            postimage = itemView.findViewById(R.id.post_edit_image);
            title = itemView.findViewById(R.id.post_edit_title);
            description = itemView.findViewById(R.id.post_edit_description);
            date = itemView.findViewById(R.id.post_edit_date);
            time = itemView.findViewById(R.id.post_edit_time);

        }
    }

}
