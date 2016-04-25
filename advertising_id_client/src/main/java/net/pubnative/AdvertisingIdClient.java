// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class AdvertisingIdClient {

    private static final String TAG = AdvertisingIdClient.class.getSimpleName();

    //==============================================================================================
    // LISTENER
    //==============================================================================================
    public interface Listener {

        /**
         * Called when process completed using UI Thread
         *
         * @param adInfo <code>AdInfo</code> object
         */
        void onAdvertisingIdClientFinish(AdInfo adInfo);

        /**
         * Called when getting advertising id fails using UI Thread
         *
         * @param exception exception with extended message of the error.
         */
        void onAdvertisingIdClientFail(Exception exception);
    }

    protected Listener mListener;
    protected Handler  mHandler;
    //==============================================================================================
    // Public methods
    //==============================================================================================

    /**
     * Method to invoke the process of getting advertisingid, using UIThread
     *
     * @param context  a valid context
     * @param listener valid Listener for callbacks
     */
    public static synchronized void getAdvertisingId(Context context, Listener listener) {

        new AdvertisingIdClient().start(context, listener);
    }

    /**
     * Ad Info data class with the results
     */
    public class AdInfo {

        private final String  mAdvertisingId;
        private final boolean mLimitAdTrackingEnabled;

        AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {

            mAdvertisingId = advertisingId;
            mLimitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId() {

            return mAdvertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {

            return mLimitAdTrackingEnabled;
        }
    }
    //==============================================================================================
    // Inner classes
    //==============================================================================================

    /**
     * Advertising Service Connection
     */
    protected class AdvertisingConnection implements ServiceConnection {

        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<IBinder>(1);

        public void onServiceConnected(ComponentName name, IBinder service) {

            try {
                this.queue.put(service);
            } catch (InterruptedException localInterruptedException) {}
        }

        public void onServiceDisconnected(ComponentName name) {}

        public IBinder getBinder() throws InterruptedException {

            if (this.retrieved) { throw new IllegalStateException(); }
            this.retrieved = true;
            return (IBinder) this.queue.take();
        }
    }

    /**
     * Advertising IInterface to get the ID
     */
    protected class AdvertisingInterface implements IInterface {

        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {

            binder = pBinder;
        }

        public IBinder asBinder() {

            return binder;
        }

        public String getId() throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean) throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }

    //==============================================================================================
    // Inner methods
    //==============================================================================================
    protected void start(final Context context, final Listener listener) {

        if (listener == null) {
            Log.e(TAG, "getAdvertisingId - Error: null listener, dropping call");
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            mListener = listener;
            if (context == null) {
                invokeFail(new Exception(TAG + " - Error: context null"));
            } else {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        getAdvertisingIdInfo(context);
                    }
                }).start();
            }
        }
    }

    private void getAdvertisingIdInfo(Context context) {

        Log.v(TAG, "getAdvertisingIdInfo");
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("com.android.vending", 0);
            Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
            intent.setPackage("com.google.android.gms");
            AdvertisingConnection connection = new AdvertisingConnection();
            try {
                if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
                    AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                    AdInfo adInfo = new AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
                    invokeFinish(adInfo);
                }
            } catch (Exception exception) {
                Log.e(TAG, "getAdvertisingIdInfo - Error: " + exception);
                invokeFail(exception);
            } finally {
                context.unbindService(connection);
            }
        } catch (Exception exception) {
            Log.e(TAG, "getAdvertisingIdInfo - Error: " + exception);
            invokeFail(exception);
        }
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================
    protected void invokeFinish(final AdInfo adInfo) {

        Log.v(TAG, "invokeFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onAdvertisingIdClientFinish(adInfo);
                }
            }
        });
    }

    protected void invokeFail(final Exception exception) {

        Log.v(TAG, "invokeFail: " + exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onAdvertisingIdClientFail(exception);
                }
            }
        });
    }
}
