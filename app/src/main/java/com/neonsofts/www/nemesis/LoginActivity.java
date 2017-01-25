package com.neonsofts.www.nemesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private Api api;
    private GradientBackgroundPainter gradientBackgroundPainter;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;

    private TextView loading_text;
    private ProgressBar loading_progress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loading_text = (TextView) findViewById(R.id.progress_login);
        loading_progress = (ProgressBar) findViewById(R.id.progressBar_login);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        View backgroundImage = findViewById(R.id.root_view);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.temp_1;
        drawables[1] = R.drawable.temp_2;
        drawables[2] = R.drawable.temp_3;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

    }
     String email;
    public void login() {


        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);
        loading_text.setText("Authenticating...");
        loading_progress.setVisibility(View.VISIBLE);
        loading_text.setVisibility(View.VISIBLE);


         email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(api.get_json()!=null){
                        boolean error = false;
                        try {

                            if(api.get_json().get("Exception").equals("-666")){
                                error = true;
                                Log.v("user login ", "conditional excception");
                            }
                        } catch (JSONException e) {
                            Log.v("user login ", "exception 2"+e.getMessage());
                            onLoginFailed();
                        }
                        if(error){
                            Log.v("user login ", "conditional error");
                            onLoginFailed();
                        }else{
                            //disect json and match password
                            try {
                                //Log.v("user login ", "password from server: "+ api.get_json().get("user_password"));
                                //Log.v("user login ", "password from user: "+ password);
                                String server_pass = api.get_json().getString("user_password").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]","");
                                if(server_pass.equals(password)){

                                    String user_name = api.get_json().getString("user_name").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]","");

                                    SharedPreferences settings = getSharedPreferences("LEL", 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("user_name", user_name);
                                    editor.commit();

                                    onLoginSuccess();
                                }else{
                                    //wrong password

                                    onLoginFailed();
                                }
                            } catch (JSONException e) {
                                Log.v("user login ", "exception 1"+e.getMessage());
                                onLoginFailed();
                            }
                        }
                    }else{
                        onLoginFailed();
                    }

                    break;
                default:

                    break;
            }
            }
        };

         api = new Api(myHandler, getResources().getString(R.string.api_base), getResources().getString(R.string.api_table_users), null, "user_email", email);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
        finish();
        System.exit(0);
    }

    public void onLoginSuccess() {

        loading_text.setText("");
        loading_progress.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);


        _loginButton.setEnabled(true);
        SharedPreferences settings = getSharedPreferences("LEL", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_email", email);
        editor.commit();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        loading_text.setText("");
        loading_progress.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);

        final Snackbar sb =  Snackbar.make(this.findViewById(android.R.id.content), "Login failed", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).setDuration(5000);
        sb.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.dismiss();
            }
        });
        sb.show();


        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }
}
