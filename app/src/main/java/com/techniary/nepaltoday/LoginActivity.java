package com.techniary.nepaltoday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email_user;
    private TextInputLayout password_user;
    private Button sign_in_button;
    private Button register_button;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Login ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();


        email_user = (TextInputLayout) findViewById(R.id.text_input_email);
        password_user =(TextInputLayout) findViewById(R.id.text_input_password);

        sign_in_button = (Button) findViewById(R.id.sign_in_button_login_activity);
        register_button = (Button) findViewById(R.id.register_button_login_activity);


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_user.getEditText().getText().toString().trim();
                String password = password_user.getEditText().getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle(" Signing In");
                progressDialog.setMessage(" Please wait, while we sign you in ");
                progressDialog.setCanceledOnTouchOutside(false);

                progressDialog.show();
                if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)))
                {
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();

                                final String current_user = mAuth.getCurrentUser().getUid();
                                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(current_user)) {

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Intent intent = new Intent(LoginActivity.this,MyAccountActivity.class);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }


                                });
                            }
                            else
                            {
                                progressDialog.hide();
                                Toast.makeText(LoginActivity.this," Error Signing In. Check Your Credentials ",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                else
                {
                 progressDialog.hide();
                }
            }
        });


    }

}
