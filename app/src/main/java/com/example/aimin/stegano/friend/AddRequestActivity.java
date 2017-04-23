package com.example.aimin.stegano.friend;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.activity.BaseActivity;
import com.example.aimin.stegano.adapter.AddRequestAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by aimin on 2017/4/17.
 */

public class AddRequestActivity extends BaseActivity {
    @Bind(R.id.add_request_recycler)
    protected RecyclerView requestRecycler;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private AddRequestAdapter requestAdapter;
    private List<AddRequest> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_request);
        super.onCreate(savedInstanceState);

        //toolbar
        setSupportActionBar(toolbar);
        setTitle("好友请求");
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestRecycler.setHasFixedSize(true);
        requestRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new AddRequestAdapter(mList, this);
        requestRecycler.setAdapter(requestAdapter);
        requestRecycler.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        mList.clear();
        AddRequestManager.getInstance().findAllAddRequests(new FindCallback<AddRequest>() {
            @Override
            public void done(List<AddRequest> list, AVException e) {
                AddRequestManager.getInstance().markAddRequestsRead(list);
                for (AddRequest addRequest : list) {
                    if (addRequest.getFromUser() != null) {
                        Log.d("raz","in addreaquest initdata"+addRequest.toString());
                        mList.add(addRequest);
                    }
                }
                requestAdapter.notifyDataSetChanged();
            }
        });
    }
}
