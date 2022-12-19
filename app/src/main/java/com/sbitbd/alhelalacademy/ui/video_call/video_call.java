package com.sbitbd.alhelalacademy.ui.video_call;

import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sbitbd.alhelalacademy.R;
import com.sbitbd.alhelalacademy.activity.BaseCallActivity;
import com.sbitbd.alhelalacademy.agora.Constants;
import com.sbitbd.alhelalacademy.databinding.ActivityVideoCallBinding;

import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;

public class video_call extends BaseCallActivity {

    private static final String TAG = video_call.class.getSimpleName();

    private FrameLayout mLocalPreviewLayout;
    private FrameLayout mRemotePreviewLayout;
    private AppCompatImageView mMuteBtn;
    private String mChannel;
    private int mPeerUid;
    private ActivityVideoCallBinding videoBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoBinding = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(videoBinding.getRoot());
        initUI();
        initVideo();
    }

    private void initUI() {
        mLocalPreviewLayout = videoBinding.localPreviewLayout;
        mRemotePreviewLayout = videoBinding.remotePreviewLayout;
        mMuteBtn = videoBinding.btnMute;
        mMuteBtn.setActivated(true);
//        Intent intent = getIntent();
//        mChannel = intent.getStringExtra(Constants.KEY_CALLING_CHANNEL);
//
//        mPeerUid = Integer.parseInt(intent.getStringExtra(Constants.KEY_CALLING_PEER));
    }

    private void initVideo() {
        Intent intent  = getIntent();
        mChannel = intent.getStringExtra(Constants.KEY_CALLING_CHANNEL);
        try {
            mPeerUid = Integer.valueOf(intent.getStringExtra(Constants.KEY_CALLING_PEER));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.message_wrong_number,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        setVideoConfiguration();
        setupLocalPreview();
        joinRtcChannel(mChannel, "", Integer.parseInt(config().getUserID(this)));
    }

    private void setupLocalPreview() {
        SurfaceView surfaceView = setupVideo(Integer.parseInt(config().getUserID(this)), true);
        surfaceView.setZOrderOnTop(true);
        mLocalPreviewLayout.addView(surfaceView);
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) mLocalPreviewLayout.getLayoutParams();
        params.topMargin += statusBarHeight;
        mLocalPreviewLayout.setLayoutParams(params);

        RelativeLayout buttonLayout = videoBinding.buttonLayout;
        params = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
        params.bottomMargin = displayMetrics.heightPixels / 8;
        params.leftMargin = displayMetrics.widthPixels / 6;
        params.rightMargin = displayMetrics.widthPixels / 6;
        buttonLayout.setLayoutParams(params);
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        if (uid != mPeerUid) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRemotePreviewLayout.getChildCount() == 0) {
                    SurfaceView surfaceView = setupVideo(uid, false);
                    mRemotePreviewLayout.addView(surfaceView);
                }
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
//        if (uid != mPeerUid) return;
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        leaveChannel();
    }

    @Override
    public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
        Log.i(TAG, "Ignore remote invitation from " +
                remoteInvitation.getCallerId() + " while in calling");
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_endcall:
                finish();
                break;
            case R.id.btn_mute:
                rtcEngine().muteLocalAudioStream(mMuteBtn.isActivated());
                mMuteBtn.setActivated(!mMuteBtn.isActivated());
                break;
            case R.id.btn_switch_camera:
                rtcEngine().switchCamera();
                break;
        }
    }

    @Override
    public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

    }

    @Override
    public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

    }
}