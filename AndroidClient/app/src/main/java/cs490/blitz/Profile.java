package cs490.blitz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Profile extends Activity {

    String username;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        sp = getSharedPreferences("cs490.blitz.account", MODE_PRIVATE);
        username = sp.getString("username", null);
        ((TextView) findViewById(R.id.textUsername)).setText(username);

        String item[] = new String[5];
        int i = 0;
        item[i++] = "Notifications";
        item[i++] = "My Posts";
        item[i++] = "My Responses";
        item[i++] = "Change Password";
        item[i++] = "Log out";
        ((ListView) findViewById(R.id.listView)).setAdapter(new ArrayAdapter<>(this, R.layout.profile_item, R.id.textItem, item));
        ((ListView) findViewById(R.id.listView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;//notification
                    case 1:
                        break;//Posts
                    case 2:
                        break;//Responses
                    case 3:
                        Intent loginIntent = new Intent(Profile.this, ChangePassword.class);
                        startActivity(loginIntent);
                        break;
                    case 4:
                        sp.edit().putString("username", null).apply();
                        finish();
                        break;
                }
            }
        });
    }

}
