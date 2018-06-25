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

import com.corelabsplus.journalapp.R;
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
import com.rengwuxian.materialedittext.MaterialEditText;

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

    //GLOBAL VARIABLES AND CONSTANTS

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        context = this;

        mAuth = FirebaseAuth.getInstance();


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
            }
        }
    }

    //AUTH WITH GOOGLE

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    //LOGOUT

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                    }
                });
    }

    //REVOKE USER ACCESS

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                    }
                });
    }


    //ONCLICK LISTENER

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.google_login){
            googleSignIn();
        }

        else if (id == R.id.login_btn){

        }

        else if (id == R.id.register_btn){

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
}
