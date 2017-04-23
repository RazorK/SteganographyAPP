package com.example.aimin.stegano.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.activity.BaseActivity;
import com.example.aimin.stegano.adapter.UserAdapter;
import com.example.aimin.stegano.event.UserClickEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by aimin on 2017/4/9.
 */

public class AddFriendActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.result_list)
    protected RecyclerView friendRecycler;

    private UserAdapter friendAdapter;
    private List<AVObject> mList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend_add);
        super.onCreate(savedInstanceState);

        //toolbar
        setSupportActionBar(toolbar);
        setTitle("添加好友");
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //frend list
        //好友列表
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));
        friendAdapter = new UserAdapter(mList, AddFriendActivity.this);
        friendRecycler.setAdapter(friendAdapter);
        friendRecycler.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    public void onEvent(UserClickEvent userEvent) {
        if (null != userEvent) {
            if (!TextUtils.isEmpty(userEvent.targetID)) {
                Intent intent = new Intent(this,UserInfoActivity.class);
                intent.putExtra(Constants.TARGET_USER_ID,userEvent.targetID);
                startActivity(intent);
            }
        }
    }

    private void initData() {
        mList.clear();
        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
        avQuery.orderByDescending("createdAt");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mList.addAll(list);
                    friendAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
