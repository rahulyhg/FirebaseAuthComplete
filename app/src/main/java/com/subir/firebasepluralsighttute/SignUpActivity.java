package com.subir.firebasepluralsighttute;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.subir.firebasepluralsighttute.model.User;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username,phone_no,password,confirm_password;
    private Button registerBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.emailEditText);
        phone_no = findViewById(R.id.phoneEditText);
        password = findViewById(R.id.passwordEditText);
        confirm_password = findViewById(R.id.confirmPasswordEditText);
        registerBtn = findViewById(R.id.registerButton);

        registerBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.registerButton)
        {
            String s1,s2,s3,s4;
            s1 = username.getText().toString();
            s2 = phone_no.getText().toString();
            s3 = password.getText().toString();
            s4 = confirm_password.getText().toString();



            if(s1.equals("")||s2.equals("")||s3.equals("")||s4.equals(""))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage("Cannot Leave Fields Empty");
                builder.setTitle("Warning");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog  = builder.create();
                dialog.show();
            }
            else
            {
                if(!(s3.equals(s4))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage("Passwords do not match");
                    builder.setTitle("Warning");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog  = builder.create();
                    dialog.show();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(s1,s3)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("success","on Complete : "+task.isSuccessful());

                            if(task.isSuccessful())
                            {
                                Log.d("success","onComplete : AuthState"+mAuth
                                        .getCurrentUser().getUid());
                                Snackbar.make(findViewById(R.id.constraintLayout),
                                        "SignUp Successful",Snackbar.LENGTH_SHORT).show();
                                sendVerificationEmail();

                                User user = new User();
                                user.setName("");
                                user.setPhone("");
                                user.setProfile_image("https://firebasestorage.googleapis.com/v0/b/fir-pluralsighttutorial.appspot.com/o/AtLLheUKrTaD6SgTY4daPehFemE3%2FIMG_20171230_150051.jpg?alt=media&token=9f4136df-babf-4f5a-8c03-f640d4db2b30");
                                user.setSecurity_level("1");
                                user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.dbnode_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(),"Data Successfully Saved",Toast.LENGTH_SHORT)
                                                    .show();
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Something went Wrong",Toast.LENGTH_SHORT)
                                                .show();
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

                                    }
                                });
                            }
                            else
                            {
                                Log.d("failed", "createUserWithEmail:failure", task.getException());
                                Snackbar.make(findViewById(R.id.constraintLayout),
                                        "SignUp Failed",Snackbar.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                            }
                        }
                    });
                }
            }

        }
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.d("success","onComplete AuthState : "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Snackbar.make(findViewById(R.id.constraintLayout),"Email Sent",Snackbar.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Log.d("failed","Email not sent");
                        Snackbar.make(findViewById(R.id.constraintLayout),"Email Could not be Sent",Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

}
