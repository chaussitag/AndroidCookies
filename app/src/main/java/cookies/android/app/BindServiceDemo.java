package cookies.android.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cookies.android.R;
import cookies.android.app.services.localservice.LocalService;
import cookies.android.app.services.localservice.LocalService.LocalBinder;
import cookies.android.app.services.servicecommon.AidlServiceClientInfo;
import cookies.android.app.services.servicecommon.AidlServiceResult;
import cookies.android.app.services.servicecommon.IRandIntGenerator;
import cookies.android.app.services.servicecommon.ServiceMessages;

public class BindServiceDemo extends Activity {

    private TextView mLocalServiceResult;
    private TextView mMessengerServiceResult;
    private TextView mAidlServiceResult;

    private static final int UPDATE_LOCAL_SERVICE_RESULT = 100;
    private static final int SEND_REQUEST_TO_MESSENGER_SERVICE = 101;
    private static final int UPDATE_AIDL_SERVICE_RESULT = 103;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LOCAL_SERVICE_RESULT:
                    mLocalServiceResult.setText(String.valueOf(mLocalBinder.nextRandomInteger()));
                    sendEmptyMessageDelayed(UPDATE_LOCAL_SERVICE_RESULT, 200);
                    break;
                case ServiceMessages.MSG_INTEGER_PROVIDED:
                    mMessengerServiceResult.setText(String.valueOf(msg.arg1));
                    sendEmptyMessageDelayed(SEND_REQUEST_TO_MESSENGER_SERVICE, 200);
                    break;
                case SEND_REQUEST_TO_MESSENGER_SERVICE:
                    sendRequestToMessengerService();
                    break;
                case UPDATE_AIDL_SERVICE_RESULT:
                    try {
                        AidlServiceClientInfo clientInfo =
                                new AidlServiceClientInfo(android.os.Process.myPid(), getProcessName());
                        AidlServiceResult result = mAidlService.getRandomIntResult(clientInfo);
                        DateFormat format = SimpleDateFormat.getDateInstance();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(result.getRandInt());
                        stringBuilder.append(", ");
                        stringBuilder.append(format.format(new Date(result.getGeneratedTime())));
                        mAidlServiceResult.setText(stringBuilder.toString());
                        sendEmptyMessageDelayed(UPDATE_AIDL_SERVICE_RESULT, 200);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private String getProcessName() {
        String processName = null;
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        return processName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindservice_demo);

        // for local service
        Button button = (Button) findViewById(R.id.bind_local_service);
        button.setOnClickListener(mBindLocalService);

        button = (Button) findViewById(R.id.unbind_local_service);
        button.setOnClickListener(mUnBindLocalService);

        mLocalServiceResult = (TextView) findViewById(R.id.integer_from_local_service);

        // for messenger service
        button = (Button) findViewById(R.id.bind_messenger_service);
        button.setOnClickListener(mBindMessengerService);

        button = (Button) findViewById(R.id.unbind_messenger_service);
        button.setOnClickListener(mUnBindMessengerService);

        mMessengerServiceResult = (TextView) findViewById(R.id.integer_from_messenger_service);

        // for aidl service
        button = (Button) findViewById(R.id.bind_aidl_service);
        button.setOnClickListener(mBindAIDLService);

        button = (Button) findViewById(R.id.unbind_aidl_service);
        button.setOnClickListener(mUnBindAIDLService);

        mAidlServiceResult = (TextView) findViewById(R.id.integer_from_aidl_service);
    }

    // ======================================================================================
    // for local service
    private LocalService.LocalBinder mLocalBinder;
    private boolean mLocalServiceBinded = false;
    private ServiceConnection mLocalServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocalBinder = (LocalBinder) service;
            mHandler.sendEmptyMessage(UPDATE_LOCAL_SERVICE_RESULT);
            mLocalServiceBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocalBinder = null;
        }

    };

    private OnClickListener mBindLocalService = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BindServiceDemo.this, LocalService.class);
            bindService(intent, mLocalServiceConnection,
                    Context.BIND_AUTO_CREATE);
        }
    };

    private OnClickListener mUnBindLocalService = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mLocalServiceBinded) {
                mHandler.removeMessages(UPDATE_LOCAL_SERVICE_RESULT);
                unbindService(mLocalServiceConnection);
                mLocalServiceBinded = false;
            }
        }
    };
    // ======================================================================================

    // ======================================================================================
    // for messenger service
    private Messenger mClientSideMessenger = new Messenger(mHandler);
    private Messenger mMessengerService;
    private boolean mMessengerServiceBinded = false;
    private ServiceConnection mMessengerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessengerService = new Messenger(service);
            sendRequestToMessengerService();
            mMessengerServiceBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessengerService = null;
        }
    };

    private void sendRequestToMessengerService() {
        if (mMessengerService != null) {
            Message msg = Message.obtain();
            msg.what = ServiceMessages.MSG_REQUEST_INTEGER;
            msg.replyTo = mClientSideMessenger;
            try {
                mMessengerService.send(msg);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static final String ACTION_MESSENGER_SERVICE =
            "cookies.android.app.services.remoteservice.MessengerService_random_int";
    private OnClickListener mBindMessengerService = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ACTION_MESSENGER_SERVICE);
            bindService(intent, mMessengerServiceConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private OnClickListener mUnBindMessengerService = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mMessengerServiceBinded) {
                mHandler.removeMessages(SEND_REQUEST_TO_MESSENGER_SERVICE);
                unbindService(mMessengerServiceConnection);
                mMessengerServiceBinded = false;
            }
        }
    };
    // ======================================================================================

    // ======================================================================================
    // for aidl service
    private boolean mAidlServiceBinded = false;
    private IRandIntGenerator mAidlService;
    private ServiceConnection mAidlServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAidlService = IRandIntGenerator.Stub.asInterface(service);
            mHandler.sendEmptyMessage(UPDATE_AIDL_SERVICE_RESULT);
            mAidlServiceBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAidlService = null;
        }

    };

    private static final String ACTION_AIDL_SERVICE =
            "cookies.android.app.services.remoteservice.AIDLService_random_int";
    private OnClickListener mBindAIDLService = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ACTION_AIDL_SERVICE);
            bindService(intent, mAidlServiceConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private OnClickListener mUnBindAIDLService = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mAidlServiceBinded) {
                mHandler.removeMessages(UPDATE_AIDL_SERVICE_RESULT);
                unbindService(mAidlServiceConnection);
                mAidlServiceBinded = false;
            }
        }
    };
    // ======================================================================================
}
