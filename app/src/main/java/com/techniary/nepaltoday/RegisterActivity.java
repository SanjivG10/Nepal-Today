package com.techniary.nepaltoday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button sign_up;
    private Button sign_in;

    private TextInputLayout email_input;
    private TextInputLayout password_user;
    private TextInputLayout confirm_password_user;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle(" Registering User ");
        progressDialog.setMessage(" Please wait, while we sign you up ");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.register_activity_login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Register ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sign_up = (Button) findViewById(R.id.register_button_register_activity);
        sign_in = (Button) findViewById(R.id.sign_in_button_register_activity);

        email_input = (TextInputLayout) findViewById(R.id.text_input_email_register);
        password_user = (TextInputLayout) findViewById(R.id.text_input_password_register);
        confirm_password_user = (TextInputLayout) findViewById(R.id.text_input_confirm_password_register);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_email = email_input.getEditText().getText().toString().trim();
                String password_user_string = password_user.getEditText().getText().toString();
                String confirm_password_user_string = confirm_password_user.getEditText().getText().toString();


                if (!(TextUtils.isEmpty(password_user_string) && TextUtils.isEmpty(confirm_password_user_string) && TextUtils.isEmpty(user_email))) {

                    if (password_user_string.equals(confirm_password_user_string)) {
                        progressDialog.show();

                        mAuth.createUserWithEmailAndPassword(user_email, confirm_password_user_string).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    
                                    HashMap<String,String> user_info = new HashMap<>();
                                    user_info.put("Username","default");
                                    user_info.put("Bio","default");
                                    user_info.put("profile_picture","default");

                                    mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(user_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, MyAccountActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(RegisterActivity.this, " Error while signing up ", Toast.LENGTH_LONG).show();
                                                progressDialog.hide();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(RegisterActivity.this, " Error while signing up ", Toast.LENGTH_LONG).show();
                                    progressDialog.hide();
                                }
                            }
                        });


                    }
                    else {
                        progressDialog.hide();
                        Toast.makeText(RegisterActivity.this, " Email empty or password do not match ", Toast.LENGTH_LONG).show();

                    }
                }

            }

        });

    }
}
