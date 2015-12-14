package cookies.android.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import cookies.android.R;

/**
 * Created by daiguozhou on 2015/3/30.
 */
public class CustomPreferenceDemo extends Activity {
    private static final String FRAGMENT_TAG =
            "cookies.android.app.CustomPreferenceDemo.fragment_tag";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            fragmentManager.beginTransaction().add(android.R.id.content,
                    new CustomPreferenceFragment(), FRAGMENT_TAG).commit();
        }
    }

    public static class CustomPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.custom_preference);
        }

        @Override
        public void onResume() {
            super.onResume();

            updateCustomPreferenceSummary();

            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String customPreferenceKey =
                    getResources().getString(R.string.custom_preference_key);
            if (key.equals(customPreferenceKey)) {
                updateCustomPreferenceSummary();
            }
        }

        private void updateCustomPreferenceSummary() {
            String customPreferenceKey = getResources().getString(R.string.custom_preference_key);
            int value = getPreferenceScreen().getSharedPreferences().getInt(customPreferenceKey, 0);
            CustomPreference customPreference =
                    (CustomPreference) getPreferenceScreen().findPreference(customPreferenceKey);
            customPreference.setSummary("You Choose: " + customPreference.getDisplayValue(value));
        }
    }
}
