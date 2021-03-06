package com.example.aimin.stegano.layout;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimin.stegano.R;
import com.example.aimin.stegano.event.InputBottomBarEvent;
import com.example.aimin.stegano.event.InputBottomBarTextEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/3/25.
 */

public class InputBar extends LinearLayout {

    private final int MIN_INTERVAL_SEND_MESSAGE = 1000;

    /**
     * 发送文本的Button
     */
    private ImageButton sendTextBtn;

    private ImageButton addPhotoBtn;

    private EditText contentView;

    /**
     * 底部拓展layout
     */
    private View moreActionView;

    /**
     * action layout
     */
    //private LinearLayout actionLayout;
    private View stegenoBtn;
    private View pictureBtn;



    public InputBar(Context context) {
        super(context);
        initView(context);
    }

    public InputBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.input_bar, this);

        sendTextBtn = (ImageButton) findViewById(R.id.input_bottom_bar_btn_send);
        contentView = (EditText) findViewById(R.id.input_bottom_bar_et_content);
        addPhotoBtn = (ImageButton) findViewById(R.id.input_bottom_bar_btn_add);

        moreActionView = findViewById(R.id.input_bar_layout_more);
        stegenoBtn = (TextView) findViewById(R.id.input_bar_btn_stegano);
        pictureBtn = (TextView)findViewById(R.id.input_bar_btn_picture);

        setEditTextChangeListener();

        sendTextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentView.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), R.string.message_is_null, Toast.LENGTH_SHORT).show();
                    return;
                }

                contentView.setText("");

                //按钮间隔
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendTextBtn.setEnabled(true);
                    }
                }, MIN_INTERVAL_SEND_MESSAGE);

                Log.d("raz", "onClick: sendTextBtn"+ content);
                EventBus.getDefault().post(
                        new InputBottomBarTextEvent(InputBottomBarTextEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, content, getTag()));
            }
        });

        /**
         * 显示翻转
         */
        addPhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //EventBus.getDefault().post(new InputBottomBarEvent(InputBottomBarEvent.INPUTBOTTOMBAR_ADD_ACTION, getTag()));
                boolean showActionView =
                        (GONE == moreActionView.getVisibility() || GONE == moreActionView.getVisibility());
                moreActionView.setVisibility(showActionView ? VISIBLE : GONE);
                //LCIMSoftInputUtils.hideSoftInput(getContext(), contentEditText);
            }
        });

        /**
         * 发送事件，在chatfragment中接受
         */
        stegenoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new InputBottomBarEvent(
                        InputBottomBarEvent.INPUTBOTTOMBAR_STEGANO_ACTION, getTag()));
            }
        });

        pictureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("raz","Image Event Send");
                EventBus.getDefault().post(new InputBottomBarEvent(
                        InputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION, getTag()));
            }
        });
    }

    private void setEditTextChangeListener() {
        contentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

}
