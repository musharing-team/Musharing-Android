package com.mine.musharing.bases;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RecordingDialogManager {

    private Context mContext;
    private Dialog mDialog;
    private GifImageView mIcon;
    private ImageView mVoice;
    private TextView mLable;

    private Timer mTimer;
    private TimerTask mTimerTask;

    public RecordingDialogManager(Context context) {
        mContext = context;
    }

    /**
     * 展示正在录音的Dialog
     */
    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.RecordingDialogTheme);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recording, null);
        mDialog.setContentView(view);

        mIcon = (GifImageView) mDialog.findViewById(R.id.dialog_icon);
        // mVoice = (ImageView) mDialog.findViewById(R.id.dialog_voice);
        mLable = (TextView) mDialog.findViewById(R.id.dialog_text);

        mDialog.show();
    }

    /**
     * 设置正在录音的界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mLable.setText("HotLine on ...");
        }
    }

    /**
     * 设置隐藏Dialog
     */
    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


}
