package cs490.blitz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView textSignup = (TextView) findViewById(R.id.textSignup);
        textSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(Login.this, Signup.class);
                startActivity(signupIntent);
            }
        });

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.editUsername)).getText().toString();
                String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
                new AsyncTask<String, Integer, JSONObject>() {
                    protected JSONObject doInBackground(String... params) {
                        HashMap<String, String> loginCredential = new HashMap();
                        loginCredential.put("operation", "Login");
                        loginCredential.put("username", params[0]);
                        loginCredential.put("password", params[1]);
                        String ret = Tools.query(JSON.toJSONString(loginCredential), 9066);
                        return JSON.parseObject(ret);
                    }

                    protected void onPostExecute(JSONObject jsonObject) {
                        if (jsonObject == null)
                            Log.e("Err", "Failed login");
                        else if (jsonObject.get("success").equals(true))
                            Log.e("Success", "success");
                        else
                            Log.e("Err", (String) jsonObject.get("msg"));
                        super.onPostExecute(jsonObject);
                    }
                }.execute(username, password);
            }
        });
    }
}
