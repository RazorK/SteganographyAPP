package com.example.aimin.stegano.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.DBHelper;
import com.example.aimin.stegano.R;

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

        dbHelper = new DBHelper(TestActivity.this, Constants.DATABASE_NAME, null, 1);

        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
            // 开始组装第一条数据
                values.put("leanID", "Dan Brown");
                values.put("username", "wtf");
                db.insert("user", null, values); // 插入第一条数据
                values.clear();
            }
        });

    }
}
