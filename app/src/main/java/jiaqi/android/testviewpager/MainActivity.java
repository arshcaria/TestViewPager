package jiaqi.android.testviewpager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnContactUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnContactUtils = (Button) findViewById(R.id.btn_contact_utils);
        btnContactUtils.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contact_utils:
                Intent intent = getIntent();
                intent.setClass(this, ContactUtilsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
