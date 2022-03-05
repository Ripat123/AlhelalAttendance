package com.sbitbd.alhelalattendance.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import com.sbitbd.alhelalattendance.Config.config;
import com.sbitbd.alhelalattendance.agora.Constants;
import com.sbitbd.alhelalattendance.connectionservice.OpenDuoConnectionService;
import com.sbitbd.alhelalattendance.ui.audio_call.audio_call;
import com.sbitbd.alhelalattendance.ui.video_call.video_call;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmCallManager;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmMessage;

public abstract class BaseCallActivity extends BaseRtcActivity implements RtmChannelListener, ResultCallback<Void> {
    private static final String TAG = BaseCallActivity.class.getSimpleName();
    private config config = new config();
    protected RtmCallManager mRtmCallManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRtmCallManager = rtmCallManager();
    }

    public void gotoCallingInterface(String peerUid, String channel, int role,int status) {
        if (config().useSystemCallInterface()) {
            // Not supported yet.
             placeSystemCall(config.User_info(this).getId(), peerUid, channel,status);
        } else {
            gotoCallingActivity(channel, peerUid, role,status);
        }
    }

    private void placeSystemCall(String myUid, String peerUid, String channel,int status) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Bundle extras = new Bundle();
            extras.putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL);

            Bundle extraBundle = new Bundle();
            extraBundle.putString(Constants.CS_KEY_UID, myUid);
            extraBundle.putString(Constants.CS_KEY_SUBSCRIBER, peerUid);
            extraBundle.putString(Constants.CS_KEY_CHANNEL, channel);
            extraBundle.putInt(Constants.CS_KEY_ROLE, Constants.CALL_ID_OUT);
            extraBundle.putInt(Constants.KEY_CALLING_STATUS, status);
            extras.putBundle(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extraBundle);

            try {
                TelecomManager telecomManager = (TelecomManager)
                        getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
                PhoneAccount pa = telecomManager.getPhoneAccount(
                        config().getPhoneAccountOut().getAccountHandle());
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, pa.getAccountHandle());
                telecomManager.placeCall(Uri.fromParts(
                        OpenDuoConnectionService.SCHEME_AG, peerUid, null), extras);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    protected void gotoCallingActivity(String channel, String peer, int role,int status) {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(Constants.KEY_CALLING_CHANNEL, channel);
        intent.putExtra(Constants.KEY_CALLING_PEER, peer);
        intent.putExtra(Constants.KEY_CALLING_ROLE, role);
        intent.putExtra(Constants.KEY_CALLING_STATUS, status);
        startActivity(intent);
    }

    protected void inviteCall(final String peerUid, final String channel,int status) {
        LocalInvitation invitation = mRtmCallManager.createLocalInvitation(peerUid);
        invitation.setChannelId(channel);
        invitation.setContent(String.valueOf(status));
        mRtmCallManager.sendLocalInvitation(invitation, this);
        global().setLocalInvitation(invitation);
    }

    protected void answerCall(final RemoteInvitation invitation) {
        if (mRtmCallManager != null && invitation != null) {
            mRtmCallManager.acceptRemoteInvitation(invitation, this);
        }
    }

    protected void cancelLocalInvitation() {
        if (mRtmCallManager != null && global().getLocalInvitation() != null) {
            mRtmCallManager.cancelLocalInvitation(global().getLocalInvitation(), this);
        }
    }

    protected void refuseRemoteInvitation(@NonNull RemoteInvitation invitation) {
        if (mRtmCallManager != null) {
            mRtmCallManager.refuseRemoteInvitation(invitation, this);
        }
    }

    @Override
    public void onMemberCountUpdated(int count) {

    }

    @Override
    public void onAttributesUpdated(List<RtmChannelAttribute> list) {

    }

    @Override
    public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
        Log.d("ttt",rtmMessage.getText());
    }

    @Override
    public void onMemberJoined(RtmChannelMember rtmChannelMember) {

    }

    @Override
    public void onMemberLeft(RtmChannelMember rtmChannelMember) {

    }

    @Override
    public void onSuccess(Void aVoid) {

    }

    @Override
    public void onFailure(ErrorInfo errorInfo) {

    }

    @Override
    public void onLocalInvitationReceived(LocalInvitation localInvitation) {
        super.onLocalInvitationReceived(localInvitation);
    }

    @Override
    public void onLocalInvitationAccepted(LocalInvitation localInvitation, String response) {
        Log.i("BaseActivity", "onLocalInvitationAccepted by peer:" + localInvitation.getCalleeId());
        gotoVideoActivity(localInvitation.getChannelId(), localInvitation.getCalleeId(),Integer.parseInt(localInvitation.getContent()));
    }

    @Override
    public void onLocalInvitationRefused(LocalInvitation localInvitation, String response) {
        super.onLocalInvitationRefused(localInvitation, response);
    }

    @Override
    public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
        super.onLocalInvitationCanceled(localInvitation);
    }

    @Override
    public void onLocalInvitationFailure(LocalInvitation localInvitation, int errorCode) {
        super.onLocalInvitationFailure(localInvitation, errorCode);
        Log.w("BaseActivity", "onLocalInvitationFailure:" + errorCode);
    }

    @Override
    public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
        Log.i("BaseActivity", "onRemoteInvitationReceived from caller:" + remoteInvitation.getCallerId());
        global().setRemoteInvitation(remoteInvitation);
        gotoCallingActivity(remoteInvitation.getChannelId(), remoteInvitation.getCallerId(), Constants.ROLE_CALLEE,Integer.parseInt(remoteInvitation.getContent()));
    }

    @Override
    public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {
        Log.i("BaseActivity", "onRemoteInvitationAccepted from caller:" + remoteInvitation.getCallerId());
        gotoVideoActivity(remoteInvitation.getChannelId(), remoteInvitation.getCallerId(),Integer.parseInt(remoteInvitation.getContent()));
    }

    @Override
    public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {
        super.onRemoteInvitationRefused(remoteInvitation);
    }

    @Override
    public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {
        super.onRemoteInvitationCanceled(remoteInvitation);
    }

    @Override
    public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int errorCode) {
        super.onRemoteInvitationFailure(remoteInvitation, errorCode);
        Log.w("BaseActivity", "onRemoteInvitationFailure:" + errorCode);
    }

    public void gotoVideoActivity(String channel, String peer,int status) {
        if (status == 0){
            Intent intent = new Intent(this, audio_call.class);
            intent.putExtra(Constants.KEY_CALLING_CHANNEL, channel);
            intent.putExtra(Constants.KEY_CALLING_PEER, peer);
            startActivity(intent);
        }else if (status == 1){
            Intent intent = new Intent(this, video_call.class);
            intent.putExtra(Constants.KEY_CALLING_CHANNEL, channel);
            intent.putExtra(Constants.KEY_CALLING_PEER, peer);
            startActivity(intent);
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
}
