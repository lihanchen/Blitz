package cs490.blitz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Profile extends Activity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        SharedPreferences sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
        username = sp.getString("username", null);
        ((TextView) findViewById(R.id.textUsername)).setText(username);

    }

}
