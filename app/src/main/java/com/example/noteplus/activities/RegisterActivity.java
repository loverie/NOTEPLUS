package com.example.noteplus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteplus.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private EditText userNameInput,passWordInuput,emailInput;
    private ImageView registerButton;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userNameInput=findViewById(R.id.userInputText);
        passWordInuput=findViewById(R.id.PasswordInput);
        emailInput=findViewById(R.id.EmailInput);
        registerButton=findViewById(R.id.RegisterButton);
        auth=FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_text=userNameInput.getText().toString();
                String email_text=emailInput.getText().toString();
                String pass_text = passWordInuput.getText().toString();
                if(TextUtils.isEmpty(username_text)||TextUtils.isEmpty(email_text)||TextUtils.isEmpty(pass_text)){
                    Toast.makeText(RegisterActivity.this,"Please Fill All Fields",Toast.LENGTH_SHORT).show();
                }else{
                    RegisterNow(username_text,email_text,pass_text);
                }
            }
        });
    }
    private void  RegisterNow(final  String username,String email,String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid=firebaseUser.getUid();
                            myRef= FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status","offline");
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this,"Invalid Email or Username",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
