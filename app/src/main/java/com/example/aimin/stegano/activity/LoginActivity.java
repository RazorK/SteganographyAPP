package com.example.aimin.stegano.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.example.aimin.stegano.ClientManager;
import com.example.aimin.stegano.R;

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
        String username = loginEditView.getText().toString().trim();
        String password = pwdEditView.getText().toString().trim();
        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    //IMClient OPEN
                    ClientManager.getInstance().open(avUser.getObjectId().toString(), new AVIMClientCallback() {
                        @Override
                        public void done(AVIMClient avimClient, AVIMException e) {
                            if(filterException(e)){
                                LoginActivity.this.finish();
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
