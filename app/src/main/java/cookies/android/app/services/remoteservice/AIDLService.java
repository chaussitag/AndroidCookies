package cookies.android.app.services.remoteservice;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import cookies.android.app.services.servicecommon.AidlServiceClientInfo;
import cookies.android.app.services.servicecommon.AidlServiceResult;
import cookies.android.app.services.servicecommon.IRandIntGenerator;

public class AIDLService extends Service {

    private static final String TAG = "AIDLService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "AIDLService.onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private Random mRand = new Random();
    private final IRandIntGenerator.Stub mBinder = new IRandIntGenerator.Stub() {

        @Override
        public AidlServiceResult getRandomIntResult(AidlServiceClientInfo arg0)
                throws RemoteException {
            Log.d(TAG, "request from process (" + arg0.getClientProcessId() + ", " + arg0.getClientProcessName() + ")");
            return new AidlServiceResult(mRand.nextInt(100), System.currentTimeMillis());
        }

    };
}
