package com.example.aimin.stegano.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.adapter.ConversationAdapter;
import com.example.aimin.stegano.event.RefreshConversationListEvent;
import com.example.aimin.stegano.event.TypedMessageEvent;
import com.example.aimin.stegano.manager.ClientManager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/4/9.
 */

public class ConversationFragment extends Fragment {

    protected RecyclerView conversationRecycler;
    private ConversationAdapter conversationAdapter;
    private List<AVIMConversation> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        //好友列表

        conversationRecycler = (RecyclerView)view.findViewById(R.id.conversaiton_recycler);
        conversationRecycler.setHasFixedSize(true);
        conversationRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        conversationAdapter = new ConversationAdapter(mList, getActivity());
        conversationRecycler.setAdapter(conversationAdapter);
        conversationRecycler.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL));

        EventBus.getDefault().register(this);
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

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void initData() throws AVException {
        mList.clear();
        AVIMConversationQuery query = ClientManager.getInstance().getClient().getQuery();
        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setWithLastMessagesRefreshed(true);
        query.findInBackground(new AVIMConversationQueryCallback(){
            @Override
            public void done(List<AVIMConversation> convs, AVIMException e){
                if(e==null){
                    for(AVIMConversation conv : convs){
                        if (conv.getMembers().size()==1)
                            continue;
                        mList.add(conv);
                    }
                    conversationAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void onEvent(RefreshConversationListEvent event) throws AVException {
        initData();
    }

    public void onEvent(TypedMessageEvent event) throws AVException {
        initData();
    }

}
