package com.bulbinc.nick;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bulbinc.nick.Values.BandValues;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private Api api;
    private static final String TAG = "SignupActivity";
    private GradientBackgroundPainter gradientBackgroundPainter;
    private String name,email;
    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;

    private TextView loading_text;
    private ProgressBar loading_progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        loading_text = (TextView) findViewById(R.id.progress_signup);
        loading_progress = (ProgressBar) findViewById(R.id.progressBar_signUp);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        View backgroundImage = findViewById(R.id.root_view_2);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.temp_1;
        drawables[1] = R.drawable.temp_2;
        drawables[2] = R.drawable.temp_3;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);
        loading_text.setText("Creating Account...");
        loading_progress.setVisibility(View.VISIBLE);
        loading_text.setVisibility(View.VISIBLE);


         name = _nameText.getText().toString();
         email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        BandValues bv = new BandValues();
        String sql_string = "(user_email,user_password,user_name,band_id)values('"+email+"','"+ password+"','"+ name+"','"+ bv.band_id +"')";

        try {
            sql_string = URLEncoder.encode(sql_string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // TODO: Implement your own signup logic here.

        Handler myHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if(api.get_json_reply_string()!=null){

                            //String server_reply = api.get_json_reply_string().replaceAll("\"", "");
                            if(api.get_json_reply_string().contains("success")){
                                onSignupSuccess();
                            }else{
                                onSignupFailed();
                            }
                        }

                        break;
                    default:
                        onSignupFailed();
                        break;
                }
            }
        };
        api = new Api(myHandler2, getResources().getString(R.string.api_base), "post", null, getResources().getString(R.string.api_table_users), sql_string);
    }

    public void onSignupSuccess() {
        loading_text.setText("");
        loading_progress.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences("LEL", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_email", email);
        editor.putString("user_name", name);
        editor.commit();

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);

        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        loading_text.setText("");
        loading_progress.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);

        final Snackbar sb =  Snackbar.make(this.findViewById(android.R.id.content), "Signup failed", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).setDuration(5000);
        sb.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.dismiss();
            }
        });
        sb.show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
        finish();
        System.exit(0);
    }
}