package com.techniary.nepaltoday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalAccountActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private Toolbar mToolbar;
    private Button mChangeQuote;
    private Button mChangePic;
    private TextView quoteView;
    private ProgressDialog anotherProgressDialog;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private static final int GALLERY_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);


        mCircleImageView = (CircleImageView) findViewById(R.id.imageView_personal_account);
        mToolbar = (Toolbar) findViewById(R.id.personalAccountToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Profile ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quoteView = (TextView) findViewById(R.id.bioGraphy);
        mChangePic = (Button) findViewById(R.id.changePicButton_Personal_activity);
        mChangeQuote = (Button) findViewById(R.id.changeBioButton_Personal_activity);
        anotherProgressDialog = new ProgressDialog(this);
        anotherProgressDialog.setTitle(" Updating Your Profile Picture ");
        anotherProgressDialog.setMessage(" Please wait, while we update your Profile Picture ");
        anotherProgressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        final String current_user_id = mAuth.getCurrentUser().getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             String userBio =dataSnapshot.child("Users").child(current_user_id).child("Bio").getValue().toString();
             final String user_image =dataSnapshot.child("Users").child(current_user_id).child("profile_picture").getValue().toString();

             quoteView.setText(userBio);

             Picasso.with(getApplicationContext()).load(user_image).networkPolicy(NetworkPolicy.OFFLINE).into(mCircleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(PersonalAccountActivity.this).load(user_image).placeholder(R.drawable.default_avatar).into(mCircleImageView);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        mChangeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quote = quoteView.getText().toString();
                Intent intent = new Intent(PersonalAccountActivity.this,ChangeQuoteActivity.class);
                intent.putExtra("quote",quote);
                startActivity(intent);

            }
        });



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
                Picasso.with(PersonalAccountActivity.this).load(image).fit().placeholder(R.drawable.default_avatar).into(mCircleImageView);

                upDateDatainFirebaseStorage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }




    }

    private void upDateDatainFirebaseStorage(Uri resultUri) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id =  mAuth.getCurrentUser().getUid();

        anotherProgressDialog.show();
        StorageReference filePath = mStorageReference.child("profile_images").child(current_user_id).child(current_user_id+".jpg");
        filePath.putFile(resultUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(PersonalAccountActivity.this," Profile Picture Updated ",Toast.LENGTH_SHORT).show();
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
