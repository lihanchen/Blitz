package cs490.blitz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class Signup extends AppCompatActivity implements View.OnClickListener{
    Button bSignUp;
    EditText etUserName, etPassword, etPassword2, etEMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        etUserName = (EditText)findViewById(R.id.editUsername);
        etPassword = (EditText)findViewById(R.id.editPassword);
        etPassword2 = (EditText)findViewById(R.id.editPassword2);
        etEMail - (EditText)findViewById(R.id.editText);

        bSignUp = (Button)findViewById(R.id.buttonSignup);

        bSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonSignup:
                signUp();
                break;
            case R.id.buttonCancel:
                cancel();
                break;

        }
    }


    private void signUp() {
        String username = ((EditText) findViewById(R.id.editUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.editPassword2)).getText().toString();
        String email = String ((EditText) findViewById(R.id.editText)).getText().toString();
        if (!password.equals(password2)) {
            // output errror
            return;
        }
    }

    private void cancel() {
        startActivity(new Intent(this, Login.class));
    }
}
