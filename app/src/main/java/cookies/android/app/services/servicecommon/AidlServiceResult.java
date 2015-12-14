package cookies.android.app.services.servicecommon;

import android.os.Parcel;
import android.os.Parcelable;

public class AidlServiceResult implements Parcelable {

    private int mRandInt;
    // the time when the random integer generated
    private long mGeneratedTime;

//    // a default constructor is necessary, or the generated IRandIntGenerator.java could not compile
//    public AidlServiceResult() {
//    }

    public AidlServiceResult(int randInt, long generatedTime) {
        mRandInt = randInt;
        mGeneratedTime = generatedTime;
    }

    public AidlServiceResult(Parcel source) {
        readFromParcel(source);
    }

    public int getRandInt() {
        return mRandInt;
    }

    public long getGeneratedTime() {
        return mGeneratedTime;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mRandInt);
        dest.writeLong(mGeneratedTime);
    }

    public void readFromParcel(Parcel source) {
        mRandInt = source.readInt();
        mGeneratedTime = source.readLong();
    }

    @SuppressWarnings("unused")
    public static final Creator<AidlServiceResult> CREATOR =
        new Creator<AidlServiceResult>() {

            @Override
            public AidlServiceResult createFromParcel(Parcel source) {
                return new AidlServiceResult(source);
            }

            @Override
            public AidlServiceResult[] newArray(int size) {
                return new AidlServiceResult[size];
            }
        
    };
}
