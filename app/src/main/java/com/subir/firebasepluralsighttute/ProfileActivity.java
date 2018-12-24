package com.subir.firebasepluralsighttute;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.subir.firebasepluralsighttute.model.User;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText phone,email,name,pass;
    ImageView profPic;
    Button applyChange;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        phone  = findViewById(R.id.phno);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        pass = findViewById(R.id.password);
        applyChange = findViewById(R.id.applyChanges);
        applyChange.setOnClickListener(this);
        profPic = findViewById(R.id.profileImg);
        progressBar = findViewById(R.id.progressBar);

        getUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeEmail:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_email_change, null);
                dialogBuilder.setView(dialogView);

                final EditText email = dialogView.findViewById(R.id.new_email);

                dialogBuilder.setTitle("Change Email");
                dialogBuilder.setMessage("");
                dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeEmail(email.getText().toString());
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog b = dialogBuilder.create();
                b.show();

                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.applyChanges)
        {
            setUserDetails();
            recreate();
        }

    }

    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getUserDetails()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            email.setText(user.getEmail());
            name.setText(user.getDisplayName());
            pass.setText("**********");

            Query query;

            query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbnode_users));
            query.orderByKey();
            query.equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user1;
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        user1 = dataSnapshot1.getValue(User.class);
                        Glide.with(ProfileActivity.this)
                                .load(user1.getProfile_image())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(profPic);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbnode_users));
            query.orderByKey();
            query.equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user1;
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        user1 = dataSnapshot1.getValue(User.class);
                        phone.setText(user1.getPhone());
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            email.setEnabled(false);
            email.selectAll();
            pass.setEnabled(false);
            pass.selectAll();

            //Uri photoUri = user.getPhotoUrl();

            //String properties = "UID : "+uid+" Name : "+name+" Email : "+email+" PhotoURI : "+photoUri;

            //Log.d("detailsFetched","data : "+properties);
        }
    }

    private void setUserDetails()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name.getText().toString())
                    .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-pluralsighttutorial.appspot.com/o/AtLLheUKrTaD6SgTY4daPehFemE3%2FIMG_20171230_150051.jpg?alt=media&token=9f4136df-babf-4f5a-8c03-f640d4db2b30"))
                    .build();

            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child(getString(R.string.dbnode_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.field_name))
                                        .setValue(name.getText().toString());

                                databaseReference.child(getString(R.string.dbnode_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.field_phone))
                                        .setValue(phone.getText().toString());
                                Toast.makeText(getApplicationContext(),"Changes Applied Successfully",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Changes Could Not be Applied",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }

    }


    public void changeEmail(String newEmail)
    {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Email Successfully Changed",Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                            Log.d("success", "User email address updated.");
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Email Could Not be Updated",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
