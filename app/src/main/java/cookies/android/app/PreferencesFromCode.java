package cookies.android.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import cookies.android.R;

/**
 * Created by daiguozhou on 2015/3/26.
 */
public class PreferencesFromCode extends Activity {
    private static final String FRAGMENT_TAG =
            "cookies.android.app.PreferenceFromXml.fragment_tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            fragmentManager.beginTransaction().replace(android.R.id.content,
                    new CodeConfiguredPreference(), FRAGMENT_TAG).commit();
        }
    }

    public static class CodeConfiguredPreference extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(root);

            // setup the preference hierarchy
            createPreferenceHierarchy(root);
        }

        private void createPreferenceHierarchy(PreferenceScreen root) {
            Context context = getActivity();

            // =============================================================================
            // Inline preference category
            PreferenceCategory inlinePrefCategory = new PreferenceCategory(context);
            inlinePrefCategory.setTitle(R.string.intent_preference_title);

            // must call this before adding something to inlinePrefCategory
            root.addPreference(inlinePrefCategory);

            // add a checkbox
            CheckBoxPreference checkbox = new CheckBoxPreference(context);
            checkbox.setKey("checkbox_key");
            checkbox.setTitle(R.string.checkbox_title);
            checkbox.setSummary(R.string.checkbox_summary);
            checkbox.setDefaultValue(true);
            inlinePrefCategory.addPreference(checkbox);

            // add a switch
            SwitchPreference switchPreference = new SwitchPreference(context);
            switchPreference.setKey("switch_key");
            switchPreference.setTitle(R.string.switch_preference_title);
            switchPreference.setSummary(R.string.switch_preference_summary);
            switchPreference.setDefaultValue(true);
            inlinePrefCategory.addPreference(switchPreference);
            // =============================================================================

            // =============================================================================
            // Dialog based preference category
            PreferenceCategory dialogPrefCategory = new PreferenceCategory(context);
            dialogPrefCategory.setTitle(R.string.dialog_based_preferences_category);

            root.addPreference(dialogPrefCategory);

            // add an edittext
            EditTextPreference editTextPreference = new EditTextPreference(context);
            editTextPreference.setKey("edit_text");
            editTextPreference.setTitle(R.string.edit_text_preference_title);
            editTextPreference.setSummary(R.string.edit_text_preference_summary);
            editTextPreference.setDialogTitle(R.string.edit_text_preference_title);
            editTextPreference.setDefaultValue("the default value");
            dialogPrefCategory.addPreference(editTextPreference);

            // add a list
            ListPreference listPreference = new ListPreference(context);
            listPreference.setKey("list");
            listPreference.setTitle(R.string.list_preference_title);
            listPreference.setSummary(R.string.list_preference_summary);
            listPreference.setDialogTitle(R.string.list_preference_dialog_title);
            listPreference.setEntries(R.array.list_preference_entry);
            listPreference.setEntryValues(R.array.list_preference_entryValues);
            Resources resources = context.getResources();
            String listPrefDefaultValue = resources.getString(R.string
                    .list_preference_value_medium);
            listPreference.setDefaultValue(listPrefDefaultValue);
            dialogPrefCategory.addPreference(listPreference);
            // =============================================================================

            // =============================================================================
            // launch preference category
            PreferenceCategory launchPrefCategory = new PreferenceCategory(context);
            launchPrefCategory.setTitle(R.string.launch_preferences_category);
            root.addPreference(launchPrefCategory);

            // add a screen preference
            PreferenceScreen screenPref = getPreferenceManager().createPreferenceScreen(context);
            // assign a key here so that it is able to save and restore its instance state
            screenPref.setKey("new_preference_screen");
            screenPref.setTitle(R.string.screen_preference_title);
            screenPref.setSummary(R.string.screen_preference_summary);
            launchPrefCategory.addPreference(screenPref);
            // add something to the new preference screen
            CheckBoxPreference newScreenCheckbox = new CheckBoxPreference(context);
            newScreenCheckbox.setKey("new_preference_screen_checkbox");
            newScreenCheckbox.setTitle(R.string.new_screen_checkbox_title);
            newScreenCheckbox.setSummary(R.string.new_screen_checkbox_summary);
            newScreenCheckbox.setDefaultValue(false);
            screenPref.addPreference(newScreenCheckbox);

            // add an preference screen with an intent to start some other activity
            PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(context);
            intentPref.setTitle(R.string.intent_preference_title);
            intentPref.setSummary(R.string.intent_preference_summary);
            intentPref.setIntent(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse
                    ("http://www.qq.com")));
            launchPrefCategory.addPreference(intentPref);
            // =============================================================================

            // =============================================================================
            // embedded checkboxes
            PreferenceCategory embeddedCheckboxPrefCategory = new PreferenceCategory(context);
            embeddedCheckboxPrefCategory.setTitle(R.string.embedded_checkbox_category);
            root.addPreference(embeddedCheckboxPrefCategory);

            // parent checkbox
            CheckBoxPreference parentCheckbox = new CheckBoxPreference(context);
            parentCheckbox.setKey("parent_chkbox");
            parentCheckbox.setTitle(R.string.parent_checkbox_title);
            parentCheckbox.setSummary(R.string.parent_checkbox_summary);
            parentCheckbox.setDefaultValue(true);
            embeddedCheckboxPrefCategory.addPreference(parentCheckbox);

            // child checkbox
            CheckBoxPreference childCheckbox = new CheckBoxPreference(context);
            childCheckbox.setKey("child_chkbox");
            childCheckbox.setTitle(R.string.child_checkbox_title);
            childCheckbox.setSummary(R.string.child_checkbox_summary);
            childCheckbox.setDefaultValue(true);
            // setup the layout according current applied theme
            TypedArray typedArray = context.obtainStyledAttributes(new int[]{
                    android.R.attr.preferenceLayoutChild
            });
            childCheckbox.setLayoutResource(typedArray.getResourceId(0, 0));
            typedArray.recycle();
            embeddedCheckboxPrefCategory.addPreference(childCheckbox);
            // NOTE:
            // setDependency() must be called after PreferenceFragment.setPreferenceScreen(),
            // which get called inside onCreate() of this class in this demo,
            // setDependency() must also be called after this 'childCheckbox' has been add to
            // embeddedCheckboxPrefCategory, or there will be a crash.
            childCheckbox.setDependency("parent_chkbox");
            // =============================================================================
        }
    }
}
