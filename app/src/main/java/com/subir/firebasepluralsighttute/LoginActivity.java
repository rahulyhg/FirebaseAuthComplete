package com.subir.firebasepluralsighttute;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginBtn;
    TextView registerTxt;
    EditText username,password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        chkFirebaseState();

        loginBtn = findViewById(R.id.loginButton);
        registerTxt = findViewById(R.id.signupText);
        username = findViewById(R.id.usernameEditText);
        password = findViewById(R.id.passwordEditText);

        loginBtn.setOnClickListener(this);
        registerTxt.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:

                String s1,s2;
                s1 = username.getText().toString();
                s2 = password.getText().toString();

                if((s1.equals("")||s2.equals("")))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                    mAuth.signInWithEmailAndPassword(s1,s2)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Log.d("success","signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if(user.isEmailVerified())
                                        {
                                            Log.d("success","verifyMail:success");
                                            Toast.makeText(getApplicationContext(),"EmailVerified",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this,ProfileActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


                                        }
                                        else
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage("Your email has not been verified yet. Please verify email to continue." +
                                                    "If you have not recieved the email yet, click Resend");
                                            builder.setTitle("Verify Email");

                                            builder.setPositiveButton("Resend", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    sendVerificationEmail();
                                                    Snackbar.make(findViewById(R.id.constraintLayout),
                                                            "Email Sent",Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            AlertDialog dialog  = builder.create();
                                            dialog.show();


                                            Log.d("failure","verifyMail:fail");
                                            Snackbar.make(findViewById(R.id.constraintLayout),"EmailNotVerified",
                                                    Snackbar.LENGTH_SHORT).show();

                                        }
                                    }
                                    else
                                    {
                                        Log.d("failure", "signInWithEmail:failure", task.getException());
                                        /*Snackbar.make(findViewById(R.id.constraintLayout), "Authentication failed.",
                                                Snackbar.LENGTH_LONG).show();*/
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                            alertDialog.setTitle("Wrong Password");
                            alertDialog.setMessage("Send a password reset link to your registered e-mail");
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetPassword();
                                }
                            });

                            AlertDialog dialog  = alertDialog.create();
                            dialog.show();
                        }
                    });

                }
                break;
            case R.id.signupText:
                startActivity(new Intent(this,SignUpActivity.class));
                break;
        }
    }

    /* *****************Firebase Setup****************** */
    private void chkFirebaseState()
    {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser!=null)
                {
                    Log.d("UserIn", "user : " + firebaseUser.getUid());
                    Toast.makeText(getApplicationContext(),"SuccessfullyLoggedIn",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.d("NoUserIn","user : none");
                }
            }
        };
    }

    /* *********************SendVerificationEmail******************** */
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


    public void resetPassword()
    {
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Snackbar.make(findViewById(R.id.constraintLayout),"Link sent successfully",Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        else
                        {
                            Snackbar.make(findViewById(R.id.constraintLayout),"Link could not be sent",Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

    }

}
