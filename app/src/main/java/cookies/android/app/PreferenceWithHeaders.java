package cookies.android.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import java.util.List;

import cookies.android.R;

/**
 * Created by daiguozhou on 2015/3/27.
 */
public class PreferenceWithHeaders extends PreferenceActivity {

    private static final String TAG = PreferenceWithHeaders.class.getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add a footer to the header list
        if (hasHeaders()) {
            TextView textView = new TextView(this);
            int bgColor = getResources().getColor(android.R.color.holo_purple);
            textView.setBackgroundColor(bgColor);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            float textSizeInPixel = getResources().getDimension(R.dimen
                    .preference_list_footer_text_size);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixel);
            int textColor = getResources().getColor(android.R.color.holo_blue_bright);
            textView.setTextColor(textColor);
            textView.setText("preference list's footer");
            setListFooter(textView);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals(PreferenceGroup1Fragment.class.getName())
                || fragmentName.equals(PreferenceGroup1FragmentInnerFragment.class.getName())
                || fragmentName.equals(PreferenceGroup2Fragment.class.getName());
    }

    // ====================================================================================
    // class PreferenceGroup1Fragment
    public static class PreferenceGroup1Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference_group1);
        }
    }

    public static class PreferenceGroup1FragmentInnerFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference_group1_inner);
        }
    }
    // ====================================================================================


    // ====================================================================================
    // class PreferenceGroup2Fragment
    public static class PreferenceGroup2Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d(TAG, "arguments: " + getArguments());
            addPreferencesFromResource(R.xml.preference_group2);
        }
    }
    // ====================================================================================
}
