package com.example.easyblog;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FirebaseUser user;
    String UserID;
    DatabaseReference reference;

    Button edit ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("userprofile");
        UserID = user.getUid();
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        edit = (Button) view.findViewById(R.id.edit_profile);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ProfileActivity.class));
            }
        });


        final TextView useless = (TextView) view.findViewById(R.id.useless);
        final TextView name = (TextView) view.findViewById(R.id.full_name);
        final TextView email = (TextView) view.findViewById(R.id.email);
        final ImageView image = (ImageView) view.findViewById(R.id.profile_img);
        image.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        useless.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        reference.child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String uname = userProfile.uname;
                    String uemail = userProfile.uemail;
                    String uimage = userProfile.uimage;

                    name.setText(uname);
                    email.setText(uemail);
                    Picasso.get().load(uimage).into(image);
                    image.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    useless.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity() , ProfileActivity.class);
                            startActivity(intent);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "cannot retrive data", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}
