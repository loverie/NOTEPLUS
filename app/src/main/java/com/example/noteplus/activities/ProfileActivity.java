package com.example.noteplus.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.noteplus.Model.Users;
import com.example.noteplus.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private TextView username;
    private ImageView imageView;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private StorageTask uploadTask;
    private Uri imageUri;
    private ImageView logoutView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView=findViewById(R.id.profile_image2);
        username=findViewById(R.id.profile_username);
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        logoutView=findViewById(R.id.imageView4);
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(ProfileActivity.this).load(user.getImageURL()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SelectImage();
        }
    });


    }
    private void SelectImage(){
        Intent i =new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMAGE_REQUEST);
    }
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver=ProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void UploadMyImage() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Uploding");
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imageUri=data.getData();
            if(uploadTask!=null&&uploadTask.isInProgress()){
                Toast.makeText(ProfileActivity.this,"Upload in progress...",Toast.LENGTH_SHORT).show();
            }else{
                UploadMyImage();
            }
        }

    }
}