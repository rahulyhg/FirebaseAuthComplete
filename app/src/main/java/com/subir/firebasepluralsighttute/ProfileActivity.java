package com.subir.firebasepluralsighttute;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText uid,email,name,pass;
    ImageView profPic;
    Button applyChange;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uid  = findViewById(R.id.uid);
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
                changeEmail();
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
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
            uid.setText(user.getUid());
            email.setText(user.getEmail());
            name.setText(user.getDisplayName());
            pass.setText("**********");
            Glide.with(ProfileActivity.this)
                    .load(user.getPhotoUrl())
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
                    .setPhotoUri(Uri.parse("https://images.pexels.com/photos/257540/pexels-photo-257540.jpeg"))
                    .build();

            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
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

    public void changeEmail()
    {

    }
}
