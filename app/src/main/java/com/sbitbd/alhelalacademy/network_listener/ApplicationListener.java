package com.sbitbd.alhelalacademy.network_listener;

import android.app.Application;

public class ApplicationListener extends Application {

    private static ApplicationListener mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized ApplicationListener getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
