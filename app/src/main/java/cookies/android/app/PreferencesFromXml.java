package cookies.android.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import cookies.android.R;

/**
 * Created by daiguozhou on 2015/3/25.
 */
public class PreferencesFromXml extends Activity {
    private static final String FRAGMENT_TAG =
            "cookies.android.app.PreferenceFromXml.fragment_tag";

    private static final String ACTIVITY_CATEGORY_HAS_PREFERENCE_METADATA =
            "cookies.android.intent.category.preference_contributor";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            fragmentManager.beginTransaction().replace(android.R.id.content,
                    new XmlConfiguredPreference(), FRAGMENT_TAG).commit();
        }
    }

    public static class XmlConfiguredPreference extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            // add preference resources that specified in the metadata of activities those
            // match the Intent
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(ACTIVITY_CATEGORY_HAS_PREFERENCE_METADATA);
            addPreferencesFromIntent(intent);
        }

        @Override
        public void onResume() {
            super.onResume();

            updateListPreferenceSummary();
            updateEditTextPreferenceSummary();

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
            Resources resources = getResources();
            String listPreferenceKey = resources.getString(R.string.list_preference_key);
            String editTextPreferenceKey = resources.getString(R.string.edit_text_preference_key);
            if (key.equals(listPreferenceKey)) {
                updateListPreferenceSummary();
            } else if (key.equals(editTextPreferenceKey)) {
                updateEditTextPreferenceSummary();
            }
        }

        private void updateListPreferenceSummary() {
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            Resources resources = getResources();
            String listPreferenceKey = resources.getString(R.string.list_preference_key);
            String value = sharedPreferences.getString(listPreferenceKey, null);
            if (value != null) {
                Preference pref = getPreferenceScreen().findPreference
                        (listPreferenceKey);
                pref.setSummary("your choose: " + value);
            }
        }

        private void updateEditTextPreferenceSummary() {
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            Resources resources = getResources();
            String editTextPreferenceKey = resources.getString(R.string.edit_text_preference_key);
            String value = sharedPreferences.getString(editTextPreferenceKey, null);
            if (value != null) {
                Preference pref = getPreferenceScreen().findPreference(editTextPreferenceKey);
                pref.setSummary("your entered: " + value);
            }
        }
    }
}
