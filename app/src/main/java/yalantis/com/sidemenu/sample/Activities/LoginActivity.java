package yalantis.com.sidemenu.sample.Activities;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

import yalantis.com.sidemenu.sample.R;
import yalantis.com.sidemenu.sample.Services.ServiceUpdateLocalDB;

/**
 * Created by ASUS-PC on 19/12/2015.
 */


public class LoginActivity extends Activity {
    Button login_button;
    Button register_button;
    EditText email_field;
    EditText pass_field;
    public static Activity acivity;
    public static Context context = null;
    private static ProgressDialog mProgressDialog;
    Button btn ;

    //////////////////////////////////facebook login
    LoginButton facebookButton;
    CallbackManager rCallback;

    ///////////////////////////////////

    ///////////////gooogle+ REQUEST_CODE_PICK_ACCOUNT
    Button googleButton;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    String mEmail;
    String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    TextToSpeech tts;
    private int result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        tts=new TextToSpeech(LoginActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    result=tts.setLanguage(Locale.UK);

                }
                else{
                    Toast.makeText(context,"not supported",Toast.LENGTH_SHORT).show();
                }
            }
        });






        //update local DB
        if(isNetworkAvailable()==true){


            Intent i = new Intent(LoginActivity.this, ServiceUpdateLocalDB.class);
            startService(i);
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.jarraya.dosdanetunisie",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        context = this.getApplicationContext();

        LoginActivity.acivity = this;

        btn= (Button) findViewById(R.id.button2);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
        // intialisation facebookButton
        facebookButton = (LoginButton) this.findViewById(R.id.facebookButton);
        facebookButton.setVisibility(View.VISIBLE);
        //list permission facebookButton
        facebookButton.setReadPermissions(Arrays.asList("user_status", "email", "user_actions.video", "user_birthday", "user_photos", "user_posts", "user_about_me", "user_location"));
        rCallback = CallbackManager.Factory.create();
        facebookButton.registerCallback(rCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                ///recuperation access token
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);
                //fin recuperation acess token

                //recuperation des donnes
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        btn.setVisibility(View.VISIBLE);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });
                        // Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        //   startActivity(intent);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // ParÃ¡metros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        ////////////////////////////////test sission exist ou non


        if( AccessToken.getCurrentAccessToken() != null)
        {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    Bundle bFacebookData = getFacebookData(object);
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // ParÃ¡metros que pedimos a facebook
            request.setParameters(parameters);
            request.executeAsync();

        }




        //////////////////////////////////google + authen
        googleButton= (Button) this.findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show();
                pickUserAccount();
            }
        });
    }
    ///////////////////////////////////////////google + methode
    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }


    //////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        rCallback.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Log.e("type", data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)) ;
                // With the account name acquired, go get the auth token
                //getUsername();
                Toast.makeText(this, data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE), Toast.LENGTH_LONG).show();
                Toast.makeText(this, mEmail, Toast.LENGTH_SHORT).show();



            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                // Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////////////////////////////////// recupere data from facebook
    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();
        try {

            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name")){
                bundle.putString("first_name", object.getString("first_name"));
            }
            if (object.has("last_name")){
                bundle.putString("last_name", object.getString("last_name"));
            }
            if (object.has("email")) {
                bundle.putString("email", object.getString("email"));
            }
            if (object.has("gender")){
                bundle.putString("gender", object.getString("gender"));
            }
            if (object.has("birthday")){
                bundle.putString("birthday", object.getString("birthday"));
            }
            if (object.has("location")){
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            }
            int duration = Toast.LENGTH_LONG;


            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    //check network connection method
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Close the Text to Speech Library
        if(tts != null) {

            tts.stop();
            tts.shutdown();
        }
    }


}
