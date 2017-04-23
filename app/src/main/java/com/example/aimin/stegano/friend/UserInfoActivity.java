package com.example.aimin.stegano.friend;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.activity.BaseActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;

/**
 * Created by aimin on 2017/4/17.
 */

public class UserInfoActivity extends BaseActivity {
    @Bind(R.id.friend_name)
    protected TextView friendName;

    @Bind(R.id.user_info_avatar)
    protected ImageView avatarImg;

    @Bind(R.id.add_request_btn)
    protected Button requestBtn;

    @Bind(R.id.delete_btn)
    protected Button deleteBtn;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private String username;
    private AVUser targetUser;

    private boolean friendStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info);

        //Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        final String targetId = getIntent().getStringExtra(Constants.TARGET_USER_ID);
        AVQuery<AVUser> query = new AVQuery<>("_User");
        query.whereEqualTo("objectId",targetId);
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if(filterException(e) && list.size() > 0){
                    targetUser = list.get(0);
                    username = targetUser.getUsername();
                    friendName.setText(username);
                    avatarHandle();
                    btnHandle();
                }
            }
        });
    }

    private void btnHandle() {
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRequestManager.getInstance().createAddRequestInBackground(UserInfoActivity.this,targetUser);
            }
        });
        AVQuery<AVUser> followerNameQuery = AVUser.getCurrentUser().followerQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followerNameQuery.whereEqualTo("follower", targetUser);
        Log.d("raz",targetUser.getObjectId());
        followerNameQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException avException) {
                Log.d("raz","in judege" + avObjects.toString());
                if(avObjects.size()>0) {
                    friendStatus = true;
                    deleteBtn.setVisibility(View.VISIBLE);
                    requestBtn.setVisibility(View.GONE);
                }
                else{
                    friendStatus = false;
                    requestBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private  void avatarHandle() {
        //TODO: AVUser 继承
        AVFile oriAvatar = targetUser.getAVFile("avatar");
        if(oriAvatar != null){
            Picasso.with(this).load(oriAvatar.getUrl()).into(avatarImg);
        } else {
            avatarImg.setImageResource(R.drawable.default_avatar);
        }
    }
}