package cookies.android.app.services.servicecommon;

import android.os.Parcel;
import android.os.Parcelable;

public class AidlServiceClientInfo implements Parcelable {

    private int mClientProcessId;
    private String mClientProcessName;

    public AidlServiceClientInfo(int clientProcessId, String clientProcessName) {
        mClientProcessId = clientProcessId;
        mClientProcessName = clientProcessName;
    }

    public AidlServiceClientInfo(Parcel source) {
        readFromParcel(source);
    }

    public int getClientProcessId() {
        return mClientProcessId;
    }

    public String getClientProcessName() {
        return mClientProcessName;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mClientProcessId);
        dest.writeString(mClientProcessName);
    }

    public void readFromParcel(Parcel source) {
        mClientProcessId = source.readInt();
        mClientProcessName = source.readString();
    }

    public static final Creator<AidlServiceClientInfo> CREATOR =
            new Creator<AidlServiceClientInfo>() {

                @Override
                public AidlServiceClientInfo createFromParcel(Parcel source) {
                    return new AidlServiceClientInfo(source);
                }

                @Override
                public AidlServiceClientInfo[] newArray(int size) {
                    return new AidlServiceClientInfo[size];
                }
            };
}
