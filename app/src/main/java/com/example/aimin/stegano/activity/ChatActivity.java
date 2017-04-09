package com.example.aimin.stegano.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.example.aimin.stegano.manager.ClientManager;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.fragment.ChatFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * Created by aimin on 2017/3/25.
 */

public class ChatActivity extends BaseActivity {
    private AVIMConversation squareConversation;
    private ChatFragment chatFragment;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.input_bar_layout_more)
    protected View moreLayout;

    /**
     * 上一次点击 back 键的时间
     * 用于双击退出的判断
     */
    private static long lastBackTime = 0;

    /**
     * 当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private final int BACK_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatFragment = (ChatFragment)getFragmentManager().findFragmentById(R.id.fragment_chat);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String memberId = getIntent().getStringExtra(Constants.MEMBER_ID);
        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
        avQuery.getInBackground(memberId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                setTitle(avObject.get("username").toString());
            }
        });

        //open the Conversation
        getConversation(memberId);
    }

    @Override
    public void onBackPressed(){
        if(moreLayout.getVisibility() != View.GONE)
            moreLayout.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    /**
     * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
     * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
     */
    private void getConversation(final String memberId) {
        final AVIMClient client = ClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.withMembers(Arrays.asList(memberId), true);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (filterException(e)) {
                    //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
                    if (null != list && list.size() > 0) {
                        chatFragment.setConversation(list.get(0));
                    } else {
                        client.createConversation(Arrays.asList(memberId), null , new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation avimConversation, AVIMException e) {
                                chatFragment.setConversation(avimConversation);
                            }
                        });
                    }
                }
            }
        });
    }
}
