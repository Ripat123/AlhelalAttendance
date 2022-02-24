package com.sbitbd.alhelalattendance.connectionservice;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.agora.Config;
import com.sbitbd.alhelalattendance.agora.EngineEventListener;
import com.sbitbd.alhelalattendance.agora.Global;
import com.sbitbd.alhelalattendance.agora.IEventListener;
import com.sbitbd.alhelalattendance.rtmtutorial.ChatManager;
import com.sbitbd.alhelalattendance.rtmtutorial.RtmTokenBuilder;

import java.util.concurrent.TimeUnit;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmCallManager;
import io.agora.rtm.RtmClient;

public class OpenDuoApplication extends Application {
    private static final String TAG = OpenDuoApplication.class.getSimpleName();

    private RtcEngine mRtcEngine;
    private RtmClient mRtmClient,mRtmClient_chat;
    private RtmCallManager rtmCallManager;
    private EngineEventListener mEventListener;
    private Config mConfig;
    private config config = new config();
    private Global mGlobal;
    private String appCertificate;
    private static String userId;
    private static int expireTimestamp;
    private ChatManager mChatManager;
    private static OpenDuoApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        try {
            if (config.User_info(this) != null) {
                initConfig();
                initEngine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static OpenDuoApplication the() {
        return sInstance;
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }

    private void initConfig() {
        sInstance = this;
        mConfig = new Config(getApplicationContext());
        mGlobal = new Global();
        if (config.User_info(this).getPhone().contains("+"))
            userId = config.User_info(this).getPhone().replace("+","");
        else
            userId = config.User_info(this).getPhone();
        mChatManager = new ChatManager(this);
        mChatManager.init();
    }

    private void initEngine() {
        String appId = getString(R.string.agora_app_id);
        if (TextUtils.isEmpty(appId)) {
            throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
        }

        mEventListener = new EngineEventListener();
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), appId, mEventListener);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableDualStreamMode(true);
            mRtcEngine.enableVideo();
            mRtcEngine.enableAudio();
//            mRtcEngine.setLogFile(FileUtil.rtmLogFile(getApplicationContext()));

            mRtmClient = RtmClient.createInstance(getApplicationContext(), appId, mEventListener);
//            mRtmClient.setLogFile(FileUtil.rtmLogFile(getApplicationContext()));

//            if (Config.DEBUG) {
//                mRtcEngine.setParameters("{\"rtc.log_filter\":65535}");
//                mRtmClient.setParameters("{\"rtm.log_filter\":65535}");
//            }

            rtmCallManager = mRtmClient.getRtmCallManager();
            rtmCallManager.setEventListener(mEventListener);

            appCertificate = getString(R.string.agora_app_certificate);

            expireTimestamp = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 3600;

//            AccessToken accessToken = new AccessToken(appId,appCertificate,"test",userId);
//            accessToken.addPrivilege(AccessToken.Privileges.kJoinChannel,expireTimestamp);
//            accessToken.addPrivilege(AccessToken.Privileges.kPublishAudioStream,expireTimestamp);
//            accessToken.addPrivilege(AccessToken.Privileges.kPublishVideoStream,expireTimestamp);
//            accessToken.addPrivilege(AccessToken.Privileges.kRtmLogin,expireTimestamp);
//            String t = accessToken.build();

            RtmTokenBuilder token = new RtmTokenBuilder();
            String result = token.buildToken(appId, appCertificate, userId, RtmTokenBuilder.Role.Rtm_User, expireTimestamp);

            Log.d("tttt", result);
            if (TextUtils.equals(result, "") || TextUtils.equals(result, "<#YOUR ACCESS TOKEN#>"))
            {
                result = null;
            }
            mRtmClient.login(result, userId, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "rtm client login success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.i(TAG, "rtm client login failed:" + errorInfo.getErrorDescription());
                }
            });
            // rtm login for chat manager.
//            mChatManager = AGApplication.the().getChatManager();
            mRtmClient_chat = mChatManager.getRtmClient();
            mRtmClient_chat.login(result, userId, new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "rtm client login success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.i(TAG, "rtm client login failed:" + errorInfo.getErrorDescription());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public RtmClient rtmClient() {
        return mRtmClient;
    }

    public void registerEventListener(IEventListener listener) {
        mEventListener.registerEventListener(listener);
    }

    public void removeEventListener(IEventListener listener) {
        mEventListener.removeEventListener(listener);
    }

    public RtmCallManager rtmCallManager() {
        return rtmCallManager;
    }

    public Config config() {
        return mConfig;
    }

    public Global global() {
        return mGlobal;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        destroyEngine();
    }

    private void destroyEngine() {
        RtcEngine.destroy();

        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "rtm client logout success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i(TAG, "rtm client logout failed:" + errorInfo.getErrorDescription());
            }
        });
    }
}
