package com.example.easyblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    String UserID;
    FirebaseUser user;
    ProgressBar progressBar;
    EditText title, description;
    Uri filepath;
    ImageView img;
    Button browse , post , back;
    Integer count;
    String key;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = FirebaseAuth.getInstance().getCurrentUser().getUid()+System.currentTimeMillis();
        setContentView(R.layout.activity_add_post);
        progressBar = findViewById(R.id.progress_bar);
        img = (ImageView) findViewById(R.id.blog_post_img);
        browse = (Button) findViewById(R.id.browse_post_img);
        post = (Button) findViewById(R.id.blog_post_btn);
        back = (Button) findViewById(R.id.blog_back_btn);
        title = (EditText) findViewById(R.id.blog_add_title);
        description = (EditText) findViewById(R.id.blod_add_description);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddPostActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title == null) {
                    title.setError("Title is Required");
                }
                uploadposttofirebase();
            }
        });


    }

    private void uploadposttofirebase() {
        progressBar.setVisibility(View.VISIBLE);
        post.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        DatabaseReference user_profile_reference = FirebaseDatabase.getInstance().getReference().child("userprofile");
        DatabaseReference user_posts_reference = FirebaseDatabase.getInstance().getReference().child("userposts");
        user_profile_reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                String usname = userProfile.uname;
                String usemail = userProfile.uemail;
                String usimage = userProfile.uimage;
                final Map<String, Object> map = new HashMap<>();
                map.put("UserID", UserID);
                map.put("timestamp", ServerValue.TIMESTAMP);
                map.put("post_key", key);
                map.put("uname", usname);
                map.put("email", usemail);
                map.put("profile_image", usimage);
                map.put("title", title.getText().toString());
                map.put("description", description.getText().toString());
                map.put("time", timeStamp);
                if (title == null || title.length() == 0) {
                    title.setError("Required");
                    progressBar.setVisibility(View.GONE);
                    post.setVisibility(View.VISIBLE);
                } else if (description == null || description.length() == 0) {
                    description.setError("Required");
                    progressBar.setVisibility(View.GONE);
                    post.setVisibility(View.VISIBLE);
                }else {
                    user_posts_reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                user_posts_reference.child(key).updateChildren(map);
                            } else {
                                user_posts_reference.child(key).setValue(map);
                            }
                            Toast.makeText(AddPostActivity.this, "Post Updated Successfully, Refresh to view changes", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            post.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    finish();
                }

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
        img.setImageURI(filepath);
        uploadprofileimagetofirebase();
    }

    private void uploadprofileimagetofirebase() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();
        DatabaseReference user_posts_reference = FirebaseDatabase.getInstance().getReference().child("userposts");
        DatabaseReference user_profile_reference = FirebaseDatabase.getInstance().getReference().child("userprofile");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploader = storage.getReference("postimages/"+ "img" + System.currentTimeMillis());
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final Map<String,Object> map=new HashMap<>();
                                user_profile_reference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User userProfile = snapshot.getValue(User.class);
                                        String usname = userProfile.uname;
                                        String usemail = userProfile.uemail;
                                        String usimage = userProfile.uimage;
                                        final Map<String, Object> map = new HashMap<>();
                                        map.put("uimage", uri.toString());
                                        map.put("UserID", UserID);
                                        map.put("timestamp", ServerValue.TIMESTAMP);
                                        map.put("post_key", key);
                                        map.put("uname", usname);
                                        map.put("email", usemail);
                                        map.put("profile_image", usimage);
                                        map.put("time", timeStamp);
                                            user_posts_reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        user_posts_reference.child(key).updateChildren(map);
                                                    } else {
                                                        user_posts_reference.child(key).setValue(map);
                                                    }
                                                    progressBar.setVisibility(View.GONE);
                                                    post.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Photo Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        post.setVisibility(View.GONE);
                    }
                });



    }

}