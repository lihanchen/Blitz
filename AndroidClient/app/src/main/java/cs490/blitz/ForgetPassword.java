package cs490.blitz;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;


/**
 * Created by Dingzhe on 10/28/2015.
 */
public class ForgetPassword extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);
        setupUI(findViewById(R.id.forgetPWparent));


        //onclick listener for go back to main page
        TextView textResetGoBack = (TextView) findViewById(R.id.textResetGoBack);
        textResetGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ForgetPassword.this, Login.class);
                startActivity(loginIntent);
            }
        });

        //set onclick listener for reset
        Button buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.resetUsername)).getText().toString();
                String email = ((EditText) findViewById(R.id.resetEmail)).getText().toString();
                new AsyncTask<String, Integer, JSONObject>() {
                    protected JSONObject doInBackground(String... params) {
                        HashMap<String, String> resetPassword = new HashMap();
                        resetPassword.put("operation", "ForgetPassword");
                        resetPassword.put("username", params[0]);
                        resetPassword.put("email", params[1]);
                        String ret = Tools.query(JSON.toJSONString(resetPassword), 9066);
                        return JSON.parseObject(ret);
                    }

                    protected void onPostExecute(JSONObject jsonObject) {
                        if (jsonObject == null) {
                            Log.e("Err", "Failed login");
                            AlertDialog alertDialog = new AlertDialog.Builder(ForgetPassword.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Please enter username and email.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        else if (jsonObject.get("success").equals(true)) {
                            Log.e("Success", "Please click on the link in the verification email to reset your password.");
                            AlertDialog alertDialog = new AlertDialog.Builder(ForgetPassword.this).create();
                            alertDialog.setTitle("Success!");
                            alertDialog.setMessage("Please click on the link in the verification email to reset your password.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent loginIntent = new Intent(ForgetPassword.this, Login.class);
                                            startActivity(loginIntent);
                                        }
                                    });
                            alertDialog.show();
                        }
                        else {
                            Log.e("Err", (String) jsonObject.get("msg"));
                            AlertDialog alertDialog = new AlertDialog.Builder(ForgetPassword.this).create();
                            alertDialog.setTitle("Error!");
                            alertDialog.setMessage((String) jsonObject.get("msg"));
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        super.onPostExecute(jsonObject);
                    }
                }.execute(username, email);
            }
        });
    }

    //setup ui inorder to hide keyboard when user click on margin
    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ForgetPassword.this);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }




}
