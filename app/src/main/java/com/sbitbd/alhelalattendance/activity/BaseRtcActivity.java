package com.sbitbd.alhelalattendance.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.media.RtcTokenBuilder;

import java.util.concurrent.TimeUnit;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public abstract class BaseRtcActivity extends BaseActivity {
    protected void joinRtcChannel(String channel, String info, int uid) {
        String appCertificate,appId;
        appCertificate = getString(R.string.agora_app_certificate);
        appId = getString(R.string.agora_app_id);
        int expireTimestamp = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 3600;

        RtcTokenBuilder builder = new RtcTokenBuilder();
        String accessToken = builder.buildTokenWithUid(appId,appCertificate,channel,uid, RtcTokenBuilder.Role.Role_Publisher,expireTimestamp);
//        String accessToken = getString(R.string.agora_access_token);
        Log.d("www",accessToken);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "<#YOUR ACCESS TOKEN#>"))
        {
            accessToken = null;
        }
        rtcEngine().joinChannel(accessToken, channel, info, uid);
    }

    protected void leaveChannel() {
        rtcEngine().leaveChannel();
    }

    protected void setVideoConfiguration() {
        rtcEngine().setVideoEncoderConfiguration(
            new VideoEncoderConfiguration(
                config().getDimension(),
                config().getFrameRate(),
                VideoEncoderConfiguration.STANDARD_BITRATE,
                config().getOrientation())
        );
    }

    protected SurfaceView setupVideo(int uid, boolean local) {
        SurfaceView surfaceView = RtcEngine.
                CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(new VideoCanvas(surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid));
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }

        return surfaceView;
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d("ttt","joined success ch");
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {

    }
}
