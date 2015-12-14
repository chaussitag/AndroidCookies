package cookies.android.test.instrumentedunittest;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by daiguozhou on 2015/12/3.
 * Tests that the parcelable interface is implemented correctly.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class LogHistoryTest {
    public static final String TEST_STRING = "This is a string";
    public static final long TEST_LONG = 12345678L;
    private LogHistory mLogHistory;

    @Before
    public void createHistory() {
        mLogHistory = new LogHistory();
    }

    @Test
    public void logHistory_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        mLogHistory.addEntry(TEST_STRING, TEST_LONG);
        
        Parcel parcel = Parcel.obtain();
        mLogHistory.writeToParcel(parcel, mLogHistory.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);
        // Read the data
        LogHistory createdFromParcel = LogHistory.CREATOR.createFromParcel(parcel);
        
        // verify that the restored object is correct.
        List<Pair<String, Long>> restoredData = createdFromParcel.getData();
        assertThat(restoredData.size(), is(1));
        assertThat(restoredData.get(0).first, is(TEST_STRING));
        assertThat(restoredData.get(0).second, is(TEST_LONG));
    }
}