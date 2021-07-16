package com.example.tinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText mNameField,mPhoneFiled;
    private Button mBAck,mConfirm;
    private ImageView ProfileImage;
    private FirebaseAuth mAuth2;
    private DatabaseReference mUserDatabase;
    private String userId,name,phone,prifuleImageUri,userSex;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mNameField=findViewById(R.id.name);
        mPhoneFiled=findViewById(R.id.phone);
        ProfileImage=findViewById(R.id.profileImage);
        mBAck=findViewById(R.id.back);
        mConfirm=findViewById(R.id.confirm);

        mAuth2=FirebaseAuth.getInstance();
        userId=mAuth2.getCurrentUser().getUid().toString();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        getUserInfo();
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        mBAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return ;
            }
        });
    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){//&&dataSnapshot.getChildrenCount()>0
                    Map<String,Object> map=(Map<String,Object>)dataSnapshot.getValue();
                    if(dataSnapshot.child("name").getValue()!=null){
                        name=dataSnapshot.child("name").getValue().toString();
                        mNameField.setText(name);
                    }
                    if(map.get("phone")!=null){
                        phone=map.get("phone").toString();
                        mPhoneFiled.setText(phone);
                    }
                    if(map.get("sex")!=null){
                        userSex=map.get("sex").toString();

                      //  mPhoneFiled.setText(phone);
                    }
                    Glide.clear(ProfileImage);
                    if(map.get("ProfileImageUrl")!=null){
                        prifuleImageUri=map.get("ProfileImageUrl").toString();
                        switch (prifuleImageUri){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.ic_launcher_background).into(ProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(prifuleImageUri).into(ProfileImage);
                                break;
                        }
                       // Glide.with(getApplication()).load(prifuleImageUri).into(ProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        name=mNameField.getText().toString();
        phone=mPhoneFiled.getText().toString();
        final Map userInfo = new HashMap();
        userInfo.put("name",name);
        userInfo.put("phone",phone);

        mUserDatabase.updateChildren(userInfo);

        if(resultUri!=null){
            final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userId);
            Bitmap bitmap=null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data=baos.toByteArray();

            UploadTask  uploadTask=filepath.putBytes(data);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this,"Cannot upload error in uploading image..",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Map userInfo=new HashMap();
                            userInfo.put("ProfileImageUrl",uri.toString());
                            mUserDatabase.updateChildren(userInfo);
                            finish();
                            return ;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this,"could not load image",Toast.LENGTH_SHORT).show();
                        }
                    });

//                    Task<Uri> downloadUrl=taskSnapshot.getStorage().getDownloadUrl();
//                    Map userInfo = new HashMap();
//                    userInfo.put("ProfileImageUrl",downloadUrl.toString());
//                    mCostumerDatabase.updateChildren(userInfo);
//
//                    finish();
//                    return ;
                }
            });

        }
        else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&resultCode==RESULT_OK){
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            ProfileImage.setImageURI(resultUri);

        }
    }
}
