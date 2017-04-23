package com.example.aimin.stegano.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.event.LogoutEvent;
import com.example.aimin.stegano.util.PathUtils;
import com.example.aimin.stegano.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/4/20.
 */

public class ProfileFragment extends BaseFragment {

    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    private ImageView avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //好友列表
        Button logoutBtn = (Button) view.findViewById(R.id.profile_logout);
        RelativeLayout avatarLayout = (RelativeLayout) view.findViewById(R.id.profile_avatar_layout);
        TextView usernameView = (TextView) view.findViewById(R.id.profile_name);

        //setAvatar TODO: 子类化AVUser
        avatar = (ImageView)view.findViewById(R.id.profile_avatar);
        AVUser user = AVUser.getCurrentUser();
        AVFile oriAvatar = user.getAVFile("avatar");
        if(oriAvatar != null){
            Picasso.with(getContext()).load(oriAvatar.getUrl()).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.default_avatar);
        }

        usernameView.setText(AVUser.getCurrentUser().getUsername());

        avatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, IMAGE_PICK_REQUEST);
            }
        });
        EventBus.getDefault().register(this);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new LogoutEvent());
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                startImageCrop(uri, 200, 200, CROP_REQUEST);
            } else if(requestCode == CROP_REQUEST) {
                Bitmap bmap = data.getParcelableExtra("data");
                avatar.setImageBitmap(bmap);
                final String path = saveCropAvatar(data);
                final AVFile file;
                final AVUser user = AVUser.getCurrentUser();
                try {
                    file = AVFile.withAbsoluteLocalPath(user.getUsername(), path);
                    user.put("avatar", file);
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (null == e) {
                                user.saveInBackground();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startImageCrop(Uri uri, int outputX, int outputY, int requestCode) {
        if(null == uri)return;

        Intent intent = new Intent();

        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);// 输出图片大小
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, requestCode);
    }

    private String saveCropAvatar(Intent data) {
        Bundle extras = data.getExtras();
        String path = null;
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                path = PathUtils.getAvatarCropPath();
                Utils.saveBitmap(path, bitmap);
                if (bitmap != null && bitmap.isRecycled() == false) {
                    bitmap = null;
                }
            }
        }
        return path;
    }
}
