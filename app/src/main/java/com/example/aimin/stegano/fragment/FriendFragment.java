package com.example.aimin.stegano.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.adapter.FriendAdapter;
import com.example.aimin.stegano.friend.AddFriendActivity;
import com.example.aimin.stegano.friend.AddRequestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aimin on 2017/4/12.
 */

public class FriendFragment extends Fragment {

    protected RecyclerView friendRecycler;
    private FriendAdapter friendAdapter;
    private List<AVUser> mList = new ArrayList<>();

    private Button requestBtn;
    private Button searchBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        //好友列表

        requestBtn = (Button)view.findViewById(R.id.request_btn);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddRequestActivity.class));
            }
        });

        searchBtn = (Button) view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
            }
        });

        friendRecycler = (RecyclerView)view.findViewById(R.id.friend_recycler);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendAdapter = new FriendAdapter(getActivity());
        friendRecycler.setAdapter(friendAdapter);
        friendRecycler.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onResume() {
        try {
            initData();
        } catch (AVException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void initData() throws AVException {
        mList.clear();
        AVQuery<AVUser> followerNameQuery = AVUser.followerQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followerNameQuery.include("follower");
        followerNameQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException avException) {
                mList.addAll(avObjects);
                friendAdapter.setUserList(mList);
                friendAdapter.notifyDataSetChanged();
            }
        });
    }

}
