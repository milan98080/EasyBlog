package com.example.easyblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {
    String UserID;

    FirebaseUser user;
    ProgressBar progressBar;
    Uri filepath;
    EditText name , email;
    Button browse , edit , cancel , delete_image ;

    ImageView image;

    String pimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = findViewById(R.id.progress_bar);
        image = (ImageView) findViewById(R.id.edit_profile_img);
        browse = (Button) findViewById(R.id.browse_img_profile);
        edit = (Button) findViewById(R.id.edit_profile);
        delete_image = (Button) findViewById(R.id.delete_img_profile);
        cancel = (Button) findViewById(R.id.cancel);
        name = (EditText) findViewById(R.id.profile_full_name);
        email = (EditText) findViewById(R.id.email_profile);
        getdata();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setImageURI(null);
                pimage = "https://www.pngitem.com/pimgs/m/30-307416_profile-icon-png-image-free-download-searchpng-employee.png";
                Picasso.get().load("https://www.pngitem.com/pimgs/m/30-307416_profile-icon-png-image-free-download-searchpng-employee.png").into(image);
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadprofiletofirebase();
            }
        });

    }

    private void uploadprofiletofirebase() {
        progressBar.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();
        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child("userprofile");
        final Map<String,Object> map=new HashMap<>();
        map.put("uname",name.getText().toString());
        map.put ("uemail", email.getText().toString());
        map.put("uimage", pimage);
        if(name == null || name.length()==0)
        {
            name.setError("Required");
            progressBar.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        } else if (email == null || email.length() == 0) {
            email.setError("Required");
            progressBar.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        }else {
            dbreference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dbreference.child(UserID).updateChildren(map);
                    Toast.makeText(ProfileActivity.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    edit.setVisibility(View.VISIBLE);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userposts");

                    Query query = ref.orderByChild("UserID").equalTo(UserID);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                postSnapshot.child("email").getRef().setValue(email.getText().toString());
                                postSnapshot.child("profile_image").getRef().setValue(pimage);
                                postSnapshot.child("uname").getRef().setValue(name.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                        }
                    });




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish();
        }


    }

    private void getdata(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userprofile");
        UserID = user.getUid();
        reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                String usname = userProfile.uname;
                String usemail = userProfile.uemail;
                String usimage = userProfile.uimage;
                name.setText(usname);
                email.setText(usemail);
                Picasso.get().load(usimage).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filepath = data.getData();
        image.setImageURI(filepath);
        uploadprofileimagetofirebase();
    }

    private void uploadprofileimagetofirebase(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploader = storage.getReference("profileimages/"+ "img" + System.currentTimeMillis());
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pimage = uri.toString();

                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        edit.setVisibility(View.VISIBLE);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.GONE);
                    }
                });


    }
}