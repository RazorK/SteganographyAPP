package com.example.aimin.stegano.manager;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by aimin on 2017/4/9.
 */

public class ActivityManager {
    private List<Activity> mList = new LinkedList<Activity>();
    private static ActivityManager instance;
    private ActivityManager() {
    }

    public synchronized static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }


    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
