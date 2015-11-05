package cs490.blitz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
        (findViewById(R.id.imageAvatar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("username", null).apply();
                finish();
            }
        });

        String ary[] = new String[50];
        for (int i = 0; i < 50; i++) {
            ary[i] = "功能" + i;
        }
        //((ListView)findViewById(R.id.listView)).setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.tv_position, ary));
    }

}
