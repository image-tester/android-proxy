package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.WifiScannerHandler;

import be.shouldit.proxy.lib.constants.APLIntents;

/**
 * Created by marco on 07/11/13.
 */
public class BaseWifiActivity extends BaseActivity
{
    private static final String TAG = BaseWifiActivity.class.getSimpleName();
    private WifiScannerHandler mScanner;

    protected WifiScannerHandler getWifiScanner()
    {
        if (mScanner == null)
            mScanner = new WifiScannerHandler();

        return mScanner;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getWifiScanner().resume();

        // Start register the status receivers
        IntentFilter ifilt = new IntentFilter();

//        ifilt.addAction(Intents.WIFI_AP_UPDATED);
        ifilt.addAction(APLIntents.APL_UPDATED_PROXY_STATUS_CHECK);
        ifilt.addAction(Intents.PROXY_REFRESH_UI);

        try
        {
            registerReceiver(changeStatusReceiver, ifilt);
        }
        catch (IllegalArgumentException e)
        {
            App.getEventsReporter().sendException(e);
        }

        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getWifiScanner().pause();
        mScanner = null;

        try
        {
            // Stop the registered status receivers
            unregisterReceiver(changeStatusReceiver);
        }
        catch (IllegalArgumentException e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            App.getLogger().logIntent(TAG, intent, Log.DEBUG, true);

//            if (action.equals(Intents.WIFI_AP_UPDATED))
//            {
////                if (App.getInstance().wifiActionEnabled)
//                {
//                    App.getLogger().d(TAG, "Received broadcast for proxy configuration written on device -> RefreshUI");
//                    refreshUI();
//                }
//            }
//            else
            if (action.equals(APLIntents.APL_UPDATED_PROXY_STATUS_CHECK))
            {
                App.getLogger().d(TAG, "Received broadcast for partial update on status of proxy configuration - RefreshUI");
                refreshUI();
            }
            else if (action.equals(Intents.PROXY_REFRESH_UI))
            {
                App.getLogger().d(TAG, "Received broadcast for update the Proxy Settings UI - RefreshUI");
                refreshUI();
            }
            else
            {
                App.getLogger().e(TAG, "Received intent not handled: " + intent.getAction());
            }
        }
    };
}
