package com.example.aimin.stegano.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.example.aimin.stegano.ClientManager;
import com.example.aimin.stegano.R;

import java.util.Arrays;
import java.util.List;

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
        openClient(loginEditView.getText().toString().trim(), pwdEditView.getText().toString().trim());
    }

    private void openClient(final String selfId, final String targetID) {
        if (TextUtils.isEmpty(selfId) || TextUtils.isEmpty(targetID)) {
            showToast(R.string.login_null_name_tip);
            return;
        }
        loginButton.setEnabled(false);
        loginEditView.setEnabled(false);
        pwdEditView.setEnabled(false);
        ClientManager.getInstance().open(selfId, new AVIMClientCallback() {
            @Override
            public void done(final AVIMClient avimClient, AVIMException e) {
                if(filterException(e)){
                    loginButton.setEnabled(true);
                    loginEditView.setEnabled(true);
                    pwdEditView.setEnabled(true);

                    AVIMConversationQuery conversationQuery = avimClient.getQuery();
                    conversationQuery.withMembers(Arrays.asList(targetID), true);
                    conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
                        @Override
                        public void done(List<AVIMConversation> list, AVIMException e) {
                            String transConversationId;
                            if(filterException(e)){
                                if(null != list && list.size()>0) {
                                    transConversationId = list.get(0).getConversationId();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("target",targetID);
                                    intent.putExtra("title", selfId + " && " + targetID);
                                    intent.putExtra("conversation", transConversationId);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    avimClient.createConversation(Arrays.asList(targetID), selfId + " && " + targetID, null, new AVIMConversationCreatedCallback() {
                                        @Override
                                        public void done(AVIMConversation avimConversation, AVIMException e) {
                                            if(filterException(e)){
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("target",targetID);
                                                intent.putExtra("title", selfId + " && " + targetID);
                                                intent.putExtra("conversation", avimConversation.getConversationId());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

    }
}
