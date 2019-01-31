package com.cudpast.app.doctor.doctorApp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.app.doctor.doctorApp.Common.Common;
import com.cudpast.app.doctor.doctorApp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;


public class VerificacionLoginActivity extends AppCompatActivity {


//    private GoogleSignInClient mGoogleSignInClient;
//
//    public static final int RC_SIGN_IN = 777;
//
//    private TextView id_login_name,id_login_mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion_login);
        getSupportActionBar().hide();
//        //-->
//        id_login_name = findViewById(R.id.id_login_name);
//        id_login_mail = findViewById(R.id.id_login_main);
//        if (getIntent() != null){
//            try {
//                String name = getIntent().getExtras().getString("usuario");
//                String mail = getIntent().getExtras().getString("correo");
//                id_login_name.setText(name);
//                id_login_mail.setText(mail);
//                Log.e("getIntent", "id_login_name -->" +name);
//                Log.e("getIntent", "id_login_mail -->" +mail);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        //<--
//
//        SignInButton signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id_android))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient =   GoogleSignIn.getClient(this, gso);
    }


//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
//    }
//
//    private void updateUI(GoogleSignInAccount account) {
//
//        if (account !=null){
//            Log.e("updateUI","la cuenta existe");
//        }else {
//            Log.e("updateUI","la cuenta existe");
//        }
//
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            if (completedTask.isSuccessful()){
//                Toast.makeText(this, "handleSignInResult ---> " + "completedTask.isSuccessful()" + completedTask.isSuccessful() , Toast.LENGTH_SHORT).show();
//                Log.e("handleSignInResult", "handleSignInResult --->" +  "completedTask.isSuccessful()" + completedTask.isSuccessful());
//                goMainScreen();
//               // updateUI(account);
//            }
//
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.e("handleSignInResult", "signInResult:failed code=" + e.getStatusCode());
//            Toast.makeText(this, "handleSignInResult ---> " + "signInResult:failed code=" + e.getStatusCode() , Toast.LENGTH_SHORT).show();
//            updateUI(null);
//        }
//    }
//
//
//
//    private void goMainScreen() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("usuario", Common.currentUser.getFirstname());
//        startActivity(intent);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.sign_in_button:
//                signIn();
//                break;
//            // ...
//        }
//    }
//
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
}
