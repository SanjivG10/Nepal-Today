package com.techniary.nepaltoday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MyAccountActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    public static final int  GALLERY_REQUEST_CODE = 10;
    private Button mChooseImageButton;
    private Button mFinishButton;
    private TextInputLayout userNameInputLayout;
    private TextInputLayout userBioInputLayout;
    private CircleImageView userBioImageCircleView;
    private Toolbar mToolbar;
    private DatabaseReference user_profile_info;
    private ProgressDialog progressDialog;
    private ProgressDialog anotherProgressDialog;
    private String user_profile_image_download_url;
    private DatabaseReference usernameDatabaseReference;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);


        mAuth = FirebaseAuth.getInstance();
        String curren_user = mAuth.getCurrentUser().getUid();
       progressDialog = new ProgressDialog(this);

        anotherProgressDialog = new ProgressDialog(MyAccountActivity.this);
        anotherProgressDialog.setTitle(" Uploading Your Picture ");
        anotherProgressDialog.setMessage(" Please wait, while we save your Database");
        anotherProgressDialog.setCanceledOnTouchOutside(false);


        usernameDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Usernames");
        user_profile_image_download_url = "default_avatar";
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user_profile_info = FirebaseDatabase.getInstance().getReference().child("Users").child(curren_user);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_my_account_activity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Your Account Info ");

        mFinishButton = (Button) findViewById(R.id.finish_button_my_account);
        mFinishButton.setEnabled(true);

        mChooseImageButton = (Button) findViewById(R.id.choose_image_my_account_activity);
        userNameInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);


        userBioInputLayout = (TextInputLayout) findViewById(R.id.fav_quote_InputLayout_my_account_activity);
        userBioImageCircleView = (CircleImageView) findViewById(R.id.user_image_my_account_activity);

        mChooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });


        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(userNameInputLayout.getEditText().getText().toString())) {

                    progressDialog.setTitle(" Saving ");
                    progressDialog.setMessage(" Please wait while we update things");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    saveDataInFirebaseRealTime();

                }


            }
        });




    }

    private void saveDataInFirebaseRealTime() {

        final String userName = userNameInputLayout.getEditText().getText().toString().trim();
        String bio = userBioInputLayout.getEditText().getText().toString().trim();

        String profile_image = user_profile_image_download_url;

        usernameDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    String username = x.getValue().toString();
                    if (username.toLowerCase() == userName.toLowerCase()) {
                        flag = false;
                        Log.e("USERNAME", username);
                    } else {
                        flag = true;
                        Log.e("USERNAME", username);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (flag == true) {

            final HashMap<String, String> user_info = new HashMap<>();
            user_info.put("Username", userName);
            user_info.put("Bio", bio);
            user_info.put("profile_picture", profile_image);

            user_profile_info.setValue(user_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        usernameDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MyAccountActivity.this, " Data Saved", Toast.LENGTH_SHORT);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MyAccountActivity.this, " Data Saving Error", Toast.LENGTH_SHORT);

                                }
                            }
                        });

                    } else {
                        Toast.makeText(MyAccountActivity.this, " Error in Saving ", Toast.LENGTH_SHORT);
                        progressDialog.hide();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Username is taken mate ",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();


            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();

                    String image = resultUri.toString();
                    Picasso.with(MyAccountActivity.this).load(image).fit().placeholder(R.drawable.default_avatar).into(userBioImageCircleView);

                    saveDatainFirebaseStorage(resultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

            }

    }

    private void saveDatainFirebaseStorage(Uri imageUri) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id =  mAuth.getCurrentUser().getUid();

        anotherProgressDialog.show();
        StorageReference filePath = mStorageRef.child("profile_images").child(current_user_id).child(current_user_id+".jpg");
            filePath.putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        user_profile_image_download_url = task.getResult().getDownloadUrl().toString();
                        anotherProgressDialog.dismiss();

                    }
                    else
                    {
                        anotherProgressDialog.hide();
                    }

                }
            });

    }
}

