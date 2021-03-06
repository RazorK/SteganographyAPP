package com.example.aimin.stegano.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.db.DBConsult;
import com.example.aimin.stegano.manager.ClientManager;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by aimin on 2017/3/22.
 */

public class LoginActivity extends BaseActivity {

    @Bind(R.id.login_edtId)
    protected EditText loginEditView;

    @Bind(R.id.login_edtPwd)
    protected EditText pwdEditView;

    @Bind(R.id.login_btnLogin)
    protected Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Cursor userList = new DBConsult(this).getAllLoginedUser();

        //TODO: add cache user list
        while(userList.moveToNext()){
            Log.d("raz Logined",userList.getString(userList.getColumnIndex("username")));
        }
    }

    @OnClick(R.id.login_btnLogin)
    public void onLoginClick(View view) {
        attemptLogin();
    }

    @OnClick(R.id.login_go_register)
    public void onRegisterClick(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void attemptLogin() {
        final String username = loginEditView.getText().toString().trim();
        String password = pwdEditView.getText().toString().trim();
        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(final AVUser avUser, AVException e) {
                if (e == null) {
                    String id = avUser.getObjectId();
                    //TODO: add default user
                    //add in DB
                    new DBConsult(LoginActivity.this).tryAddLoginedUser(id,username);
                    //IMClient OPEN
                    ClientManager.getInstance().open(id, new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if(filterException(e)){
                                //LoginActivity.this.finish();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }
                    });
                } else {
                    toast(e.getMessage());
                }
            }
        });
    }

}
