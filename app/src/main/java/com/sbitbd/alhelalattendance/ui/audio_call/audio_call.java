package com.sbitbd.alhelalattendance.ui.audio_call;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sbitbd.alhelalattendance.R;
import com.sbitbd.alhelalattendance.activity.BaseCallActivity;
import com.sbitbd.alhelalattendance.agora.Constants;

import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;

public class audio_call extends BaseCallActivity {

    private ImageView end, speaker, mute;
    private TextView peer;
    private Chronometer chronometer;
    private String mChannel;
    private int mpeerID;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final String TAG = audio_call.class.getSimpleName();
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);

        try {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (sensor == null) {
                Toast.makeText(this, "no proximity", Toast.LENGTH_SHORT).show();
            } else {
                sensorManager.registerListener(proximitySensorEventListener,
                        sensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            Intent intent = getIntent();
            mChannel = intent.getStringExtra(Constants.KEY_CALLING_CHANNEL);

            mpeerID = Integer.parseInt(intent.getStringExtra(Constants.KEY_CALLING_PEER));
            showLongToast(mChannel + " " + mpeerID);

            end = findViewById(R.id.endc);
            speaker = findViewById(R.id.speaker);
            mute = findViewById(R.id.mute);
            peer = findViewById(R.id.peert);
            chronometer = findViewById(R.id.meter);
            chronometer.start();
            peer.setText(String.valueOf("0" + mpeerID));
            speaker.setSelected(false);
            rtcEngine().setEnableSpeakerphone(false);
            end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEncCallClicked(v);
                }
            });
            speaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSwitchSpeakerphoneClicked(v);
                }
            });
            mute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocalAudioMuteClicked(v);
                }
            });
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (sensorEvent.values[0] == 0) {
                    // disable screen.
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.screenBrightness = 0;
                    getWindow().setAttributes(params);
                    end.setEnabled(false);
                    speaker.setEnabled(false);
                    mute.setEnabled(false);
                }
                else {
                    // enable screen.
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.screenBrightness = -1f;
                    getWindow().setAttributes(params);
                    end.setEnabled(true);
                    speaker.setEnabled(true);
                    mute.setEnabled(true);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    private void initAgoraEngineAndJoinChannel() {
        try {
            initializeAgoraEngine();     // Tutorial Step 1

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tutorial Step 2
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        leaveChannel();
        chronometer.stop();
    }

    public void onLocalAudioMuteClicked(View view) {
        try {
            if (view.isSelected()) {
                view.setSelected(false);
            } else {
                view.setSelected(true);
//                iv.setColorFilter(getResources().getColor(R.color.main_color), PorterDuff.Mode.MULTIPLY);
            }

            // Stops/Resumes sending the local audio stream.
            rtcEngine().muteLocalAudioStream(view.isSelected());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onSwitchSpeakerphoneClicked(View view) {
        try {
            if (view.isSelected()) {
                view.setSelected(false);
            } else {
                view.setSelected(true);

            }

            // Enables/Disables the audio playback route to the speakerphone.
            //
            // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.

            rtcEngine().setEnableSpeakerphone(view.isSelected());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i("test", "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(audio_call.this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            Log.i("test", "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

            switch (requestCode) {
                case PERMISSION_REQ_ID_RECORD_AUDIO: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        initAgoraEngineAndJoinChannel();
                    } else {
                        showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                        finish();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onEncCallClicked(View view) {
        finish();
    }


    @Override
    public void finish() {
        super.finish();
        leaveChannel();
        chronometer.stop();
    }


    private void initializeAgoraEngine() {
        try {
            rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        } catch (Exception e) {
            Log.e("test", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        joinRtcChannel(mChannel, "", mpeerID);
    }

    public final void showLongToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(audio_call.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

    }

    @Override
    public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

    }

    @Override
    public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
        Log.i(TAG, "Ignore remote invitation from " +
                remoteInvitation.getCallerId() + " while in calling");
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        finish();
//        if (uid != mpeerID) return;
        // finish();
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        super.onUserJoined(uid, elapsed);
    }

}