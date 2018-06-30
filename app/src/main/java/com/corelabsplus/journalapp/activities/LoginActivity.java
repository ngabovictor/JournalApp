package com.corelabsplus.journalapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.utils.Entry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    //BINDING VIEWS

    @BindView(R.id.login_view) LinearLayout loginView;
    @BindView(R.id.register_view) LinearLayout registerView;
    @BindView(R.id.or) TextView orText;
    @BindView(R.id.email_input) MaterialEditText emailView;
    @BindView(R.id.r_email_input) MaterialEditText registerEmailView;
    @BindView(R.id.password_input) MaterialEditText passwordView;
    @BindView(R.id.r_password_input) MaterialEditText registerPasswordView;
    @BindView(R.id.repeat_password_input) MaterialEditText rePasswordView;
    @BindView(R.id.name_input) MaterialEditText nameView;
    @BindView(R.id.login_btn) Button loginBtn;
    @BindView(R.id.register_btn) Button registerBtn;
    @BindView(R.id.go_login) Button goLogin;
    @BindView(R.id.go_register) Button goRegister;
    @BindView(R.id.google_login) Button googleLoginBtn;
    @BindView(R.id.avi) AVLoadingIndicatorView avi;

    //GLOBAL VARIABLES AND CONSTANTS

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Context context;
    private DatabaseReference databaseReference;
    private List<Entry> entries = new ArrayList<Entry>();
    private String ENTRIES_DIR;
    //private StorageReference storageReference;


    //INPUTS

    private String name, email, password, rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        context = this;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_db));
        ENTRIES_DIR = getString(R.string.entries_dir);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //BUTTON CLICKS

        googleLoginBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        goLogin.setOnClickListener(this);
        goRegister.setOnClickListener(this);
    }

    //SIGNIN INTENT

    private void googleSignIn() {
        disableViews();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //GET INTENT RESULT

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
                enableViews();
            }
        }
    }

    //AUTH WITH GOOGLE

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Set Values to Google account values
                            name = acct.getDisplayName();
                            email = acct.getEmail();

                            //Toast.makeText(context, name, Toast.LENGTH_SHORT).show();


                            //Check if the user is already in db
                            checkUser(user.getUid());

                        } else {
                            enableViews();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }



    //DISABLE VIEWS

    public void disableViews(){
        passwordView.setEnabled(false);
        emailView.setEnabled(false);
        registerEmailView.setEnabled(false);
        nameView.setEnabled(false);
        rePasswordView.setEnabled(false);
        registerBtn.setEnabled(false);
        loginBtn.setEnabled(false);
        googleLoginBtn.setEnabled(false);
        goLogin.setEnabled(false);
        goRegister.setEnabled(false);
        avi.setVisibility(View.VISIBLE);
    }

    //ENABLE VIEWS

    public void enableViews(){
        passwordView.setEnabled(true);
        emailView.setEnabled(true);
        registerEmailView.setEnabled(true);
        nameView.setEnabled(true);
        rePasswordView.setEnabled(true);
        registerBtn.setEnabled(true);
        loginBtn.setEnabled(true);
        googleLoginBtn.setEnabled(true);
        goLogin.setEnabled(true);
        goRegister.setEnabled(true);
        avi.setVisibility(View.GONE);
    }


    //ONCLICK LISTENER

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.google_login){
            googleSignIn();
        }

        else if (id == R.id.login_btn){
            email = emailView.getText().toString().trim();
            password = passwordView.getText().toString().trim();

            if (email.length() == 0){
                emailView.setError("Email is required");
            }

            else if (password.length() == 0){
                passwordView.setError("Password is required");
            }

            else {
                disableViews();

                //Login with email and password

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            goToEntries();
                        }

                        else {
                            enableViews();
                        }
                    }
                });
            }
        }

        else if (id == R.id.register_btn){

            email = registerEmailView.getText().toString().trim();
            password = registerPasswordView.getText().toString().trim();
            name = nameView.getText().toString().trim();
            rePassword = rePasswordView.getText().toString().trim();

            if (email.length() == 0){
                registerEmailView.setError("Email is required");
            }

            else if (password.length() == 0){
                registerPasswordView.setError("Password is required");
            }

            else if (name.length() == 0){
                nameView.setError("Name is required");
            }

            else if (rePassword.length() == 0){
                rePasswordView.setError("Confirm password");
            }

            else if (password.length() > 0 && rePassword.length() > 0 && !password.equals(rePassword)){
                passwordView.setError("Passwords should match");
                rePasswordView.setError("Passwords should match");
            }

            else {
                disableViews();

                //Creating user with email and password

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            //UPDATING USER PROFILE
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                                                                                            .Builder()
                                                                                            .setDisplayName(name)
                                                                                            .build();
                            final FirebaseUser user = task.getResult().getUser();

                            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //Got to save the user to db
                                        saveUserToDb(user.getUid());
                                    }

                                    else {
                                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        enableViews();
                                    }
                                }
                            });
                        }

                        else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            enableViews();
                        }
                    }
                });
            }

        }

        else if (id == R.id.go_login){

            registerView.setVisibility(View.GONE);
            loginView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orText.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.BELOW);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.login_view);

        }

        else if (id == R.id.go_register){
            registerView.setVisibility(View.VISIBLE);
            loginView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orText.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.BELOW);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.register_view);
        }
    }


    //SAVING NEW USER TO FIREBASE DATABSE

    private void saveUserToDb(String userID){
        Map<String, String> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);

        databaseReference.child("users").child(userID).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    goToEntries();
                }

                else {
                    Toast.makeText(context, "Unable to finish", Toast.LENGTH_SHORT).show();
                    enableViews();
                }
            }
        });
    }

    //GO TO USER ENTRIES ACTIVITY

    private void goToEntries(){
        Intent intent = new Intent(context, EntriesActivity.class);
        intent.putExtra("isFromLogin", true);
        intent.putExtra("entries", (Serializable) entries);
        startActivity(intent);
        finish();
    }

    //CHECK IF THE USER IS IN THE DB

    private void checkUser(final String userKey){
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userKey)){
                    if (dataSnapshot.child(userKey).hasChild(ENTRIES_DIR)){
                        for (DataSnapshot entrySnapshot : dataSnapshot.child(userKey).child(ENTRIES_DIR).getChildren()){
                            Entry entry = entrySnapshot.getValue(Entry.class);
                            entries.add(entry);
                        }
                    }
                    goToEntries();
                }

                else {
                    saveUserToDb(userKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                enableViews();
            }
        });
    }
}
