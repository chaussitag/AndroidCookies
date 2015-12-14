package cookies.android.app.services.localservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class LocalService extends Service {

	private Binder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

    public static class LocalBinder extends Binder {
        private Random mRand = new Random();

        public int nextRandomInteger() {
            return mRand.nextInt(100);
        }
    }
}
