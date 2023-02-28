package com.example.easyblog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;


public class BlogsFragment extends Fragment {

    FirebaseUser user;
    String UserID ;
    DatabaseReference reference_userprofile, databaseReference;
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    ArrayList<UserPosts> list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference_userprofile = FirebaseDatabase.getInstance().getReference("userprofile");
        UserID = user.getUid();
        View view = inflater.inflate(R.layout.fragment_blogs, container, false);
        ImageButton button = (ImageButton) view.findViewById(R.id.add_postBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.post_list);
        databaseReference = FirebaseDatabase.getInstance().getReference("userposts");
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),list);
        recyclerView.setAdapter(postAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserPosts userPosts = dataSnapshot.getValue(UserPosts.class);
                    list.add(userPosts);
                }

                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                reference_userprofile.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);
                        if(userProfile != null){
                            String uname = userProfile.uname;
                            if(uname==null || uname.length()==0){
                                Toast.makeText(getActivity(), "Please Edit Your Username First", Toast.LENGTH_SHORT).show();
                            }else {
                                startActivity(new Intent(getActivity(),AddPostActivity.class));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getActivity(), "Error Connecting to Database", Toast.LENGTH_SHORT).show();

                    }
                });
            }



        });


        return view;

    }
}