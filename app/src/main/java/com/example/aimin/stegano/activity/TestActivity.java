package com.example.aimin.stegano.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aimin.stegano.R;
import com.example.aimin.stegano.db.DBConsult;
import com.example.aimin.stegano.db.DBHelper;

import butterknife.Bind;

/**
 * Created by aimin on 2017/3/29.
 */

public class TestActivity extends BaseActivity {

    @Bind(R.id.test_btn)
    Button testbtn;

    @Bind(R.id.test_text)
    TextView testview;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBConsult dBConsolt = new DBConsult(TestActivity.this);
                dBConsolt.addCarrier("testUserId","testUsername",123.456,123456,"filepath","inserttime");
                Cursor result = dBConsolt.getAllCarrier();
                if(result.moveToFirst()){
                    do{
                        Log.d("raz",result.getString(result.getColumnIndex("username")));
                    } while (result.moveToNext());
                }
            }
        });
    }
}
