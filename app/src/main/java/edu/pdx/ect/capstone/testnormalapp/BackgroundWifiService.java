package edu.pdx.ect.capstone.testnormalapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Venom on 2/23/2018.
 */

public class BackgroundWifiService extends IntentService {

    int mStartMode = START_STICKY;
    IBinder mBinder = null;
    boolean mAllowRebind = false;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     *
     * This default constructor creates with worker thread with the class name
     */
    public BackgroundWifiService() {
        super("BackgroundWifiService");
    }


    public BackgroundWifiService(String name) {
        super(name);
    }

    /**
     * The service is starting, due to a call to startService()
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return mStartMode;
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        //HandlerThread thread = new HandlerThread("ServiceStartArguments",
        //        Process.THREAD_PRIORITY_BACKGROUND);
        //thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        //mServiceLooper = thread.getLooper();
        //mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    /**
     * A client is binding to the service with bindService()
     * @param intent
     * @return We don't provide binding, so return null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     * @param intent
     * @return We don't allow binding so we return false to prevent rebinding
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     * @param intent
     */
    @Override
    public void onRebind(Intent intent){
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 10 seconds.

        // loop this? w/Thread.sleep(X);
        boolean mobileDataEnabled = false;  // initially assume disabled
        ConnectivityManager cm = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        while(true) {
            showToast("Starting IntentService");
            try {
                Thread.sleep(10000);
                showToast("10 Seconds Have Passed");

                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");

                method.setAccessible(true);
                mobileDataEnabled = (Boolean)method.invoke(cm);
                String msg = "mobile data is " + mobileDataEnabled;
                showToast(msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            showToast("Finishing IntentService");
        }
    }

    /**
     * Called when the service is no longer used and is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
