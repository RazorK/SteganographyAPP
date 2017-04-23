package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.friend.AddRequest;
import com.example.aimin.stegano.friend.AddRequestManager;
import com.example.aimin.stegano.util.SimpleNetTask;
import com.example.aimin.stegano.util.Utils;

/**
 * Created by aimin on 2017/4/17.
 */

public class AddRequestViewHolder extends RecyclerView.ViewHolder {
    private Button acceptBtn;
    private Button ignoreBtn;
    private TextView nameText;
    private TextView resultText;
    private Context context;

    public AddRequestViewHolder(View itemView, Context act_context) {
        super(itemView);
        context = act_context;
        acceptBtn = (Button) itemView.findViewById(R.id.accept_btn);
        ignoreBtn = (Button) itemView.findViewById(R.id.ignore_btn);
        nameText = (TextView)itemView.findViewById(R.id.request_sender);
        resultText = (TextView) itemView.findViewById(R.id.result);
    }

    public void bindData(final AddRequest request) {
        nameText.setText(((AVUser)request.get(AddRequest.FROM_USER)).getUsername());
        if(request.getStatus() == AddRequest.STATUS_DONE){
            agreeBtnHandle();
        }
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("raz","in click acceptBtn");
                agreeBtnHandle();
                new SimpleNetTask(context){
                    @Override
                    protected void doInBack() throws Exception {
                        AddRequestManager.getInstance().agreeAddRequest(request, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e != null){
                                    Log.d("raz","agree && follow");
                                }
                            }
                        });
                    }
                    @Override
                    protected void onSucceed() {
                        Utils.toast(context,"成功加为好友");
                    }
                }.execute();
            }
        });
    }

    private void agreeBtnHandle(){
        Log.d("raz","in done add request");
        acceptBtn.setVisibility(View.GONE);
        ignoreBtn.setVisibility(View.GONE);
        resultText.setText("已成为好友");
        resultText.setVisibility(View.VISIBLE);
    }
}
