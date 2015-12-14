package cookies.android.app.services.remoteservice;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import cookies.android.app.services.servicecommon.ServiceMessages;

public class MessengerService extends Service {

    private Random mRand = new Random();

    private final Messenger mService = new Messenger(new ServiceHandler());

    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ServiceMessages.MSG_REQUEST_INTEGER:
                Messenger client = msg.replyTo;
                if (client != null) {
                    Message respondMsg = Message.obtain(null,
                            ServiceMessages.MSG_INTEGER_PROVIDED,
                            mRand.nextInt(100), 0);
                    try {
                        client.send(respondMsg);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return mService.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
