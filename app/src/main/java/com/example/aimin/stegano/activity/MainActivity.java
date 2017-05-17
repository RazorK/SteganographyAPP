package com.example.aimin.stegano.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.event.FriendClickEvent;
import com.example.aimin.stegano.event.LogoutEvent;
import com.example.aimin.stegano.event.RefreshConversationListEvent;
import com.example.aimin.stegano.fragment.ConversationFragment;
import com.example.aimin.stegano.fragment.FriendFragment;
import com.example.aimin.stegano.fragment.ProfileFragment;
import com.example.aimin.stegano.manager.ActivityManager;
import com.example.aimin.stegano.manager.ClientManager;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FRAGMENT_TAG_CONVERSATION = "conversation";
    private static final String FRAGMENT_TAG_CONTACT = "contact";
    private static final String FRAGMENT_TAG_PROFILE = "profile";
    private static final String[] fragmentTags = new String[]{FRAGMENT_TAG_CONVERSATION, FRAGMENT_TAG_CONTACT,
            FRAGMENT_TAG_PROFILE};

    public ConversationFragment conversationListFragment;
    public FriendFragment friendFragment;
    public ProfileFragment profileFragment;

    protected TextView navUsername;
    protected ImageView navImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //侧滑菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //侧滑菜单中信息显示部分
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.nav_username);
        navImageView = (ImageView) headerView.findViewById(R.id.nav_imageView);
        navUsername.setText(AVUser.getCurrentUser().getUsername());

        //头像部分 TODO:继承AVUSer
        AVUser user = AVUser.getCurrentUser();
        AVFile oriAvatar = user.getAVFile("avatar");
        if(oriAvatar != null){
            Picasso.with(this).load(oriAvatar.getUrl()).into(navImageView);
        } else {
            navImageView.setImageResource(R.drawable.default_avatar);
        }

        //底部导航栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (conversationListFragment == null) {
            conversationListFragment = new ConversationFragment();
            transaction.add(R.id.fragment_container, conversationListFragment, FRAGMENT_TAG_CONVERSATION);
        }
        transaction.show(conversationListFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            logout();
            ActivityManager.getInstance().exit();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧滑导航栏点击逻辑
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            startActivity(new Intent(this,TestActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,CarrierActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 底部导航栏点击逻辑
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            hideFragments(manager, transaction);

            switch (item.getItemId()) {
                case R.id.navigation_conversation:
                    if (conversationListFragment == null) {
                        conversationListFragment = new ConversationFragment();
                        transaction.add(R.id.fragment_container, conversationListFragment, FRAGMENT_TAG_CONVERSATION);
                    }
                    transaction.show(conversationListFragment);
                    transaction.commit();
                    EventBus.getDefault().post(new RefreshConversationListEvent());
                    return true;
                case R.id.navigation_friend:
                    if (friendFragment == null) {
                        Log.d("raz","in create friendFragent");
                        friendFragment = new FriendFragment();
                        transaction.add(R.id.fragment_container, friendFragment, FRAGMENT_TAG_CONTACT);
                    }
                    transaction.show(friendFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_profile:
                    if (profileFragment == null) {
                        Log.d("raz","in create friendFragent");
                        profileFragment = new ProfileFragment();
                        transaction.add(R.id.fragment_container, profileFragment, FRAGMENT_TAG_PROFILE);
                    }
                    transaction.show(profileFragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }

    };

    private void hideFragments(FragmentManager fragmentManager, FragmentTransaction transaction) {
        for (int i = 0; i < fragmentTags.length; i++) {
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags[i]);
            if (fragment != null && fragment.isVisible()) {
                transaction.hide(fragment);
            }
        }
    }

    /**
     * 主要用来处理好友列表的点击事件，直接开启ChatActivity
     * @param event
     *
     */
    public void onEvent(FriendClickEvent event) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.MEMBER_ID, event.targetID);
        startActivity(intent);
    }

    public void onEvent(LogoutEvent event) {
        logout();
        this.finish();
    }
    /**
     * 登出client storage
     */
    private void logout(){
        ClientManager.getInstance().close(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                filterException(e);
            }
        });
        AVUser.getCurrentUser().logOut();
    }
}
