package com.example.easyblog;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String TAG = ContentValues.TAG;
    EditText emailEditText,passwordEditText,confirmPasswordEditText,full_name;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;
    String userID;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener((v)-> createAccount());
        loginBtnTextView.setOnClickListener((v) -> finish() );


    }

    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }

        createAccountInFirebase(email,password);


    }

    void createAccountInFirebase(String email , String password){
        changeInProgress(true);
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    Utility.showToast(CreateAccountActivity.this, "Successfully created account , Check Email to verify");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    String UserID = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child("userprofile");
                    final Map<String,Object> map=new HashMap<>();
                    map.put ("uemail", emailEditText.getText().toString());
                    map.put ("uimage", "https://www.pngitem.com/pimgs/m/30-307416_profile-icon-png-image-free-download-searchpng-employee.png");
                    dbreference.child(UserID).setValue(map);
                    firebaseAuth.signOut();
                    finish();
                }else{
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                    
                }
            }
        });

    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email , String password , String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Invalid Format");
            return false;
        }

        if(password.length()<6){
            passwordEditText.setError("Password length should be more than 6");
            return false;
        }

        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }

}