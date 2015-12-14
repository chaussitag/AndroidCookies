package cookies.android.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import cookies.android.R;

/**
 * Created by daiguozhou on 2015/3/30.
 */
public class CustomPreference extends Preference implements View.OnClickListener {
    private static final String[] MONTHS = new String[]{
            "01", "02", "03", "04",
            "05", "06", "07", "08",
            "09", "10", "11", "12"
    };

    private int mValue = 0;

    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.custom_preference_widget);
    }

    public String getDisplayValue(int preferenceValue) {
        return MONTHS[preferenceValue];
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View layout = super.onCreateView(parent);

        ImageButton minusButton =
                (ImageButton) layout.findViewById(R.id.custom_preference_widget_minus);
        ImageButton addButton =
                (ImageButton) layout.findViewById(R.id.custom_preference_widget_add);
        minusButton.setOnClickListener(this);
        addButton.setOnClickListener(this);

        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        ImageButton minusButton =
                (ImageButton) view.findViewById(R.id.custom_preference_widget_minus);
        if (mValue == 0) {
            minusButton.setEnabled(false);
        } else if (!minusButton.isEnabled() && mValue > 0) {
            minusButton.setEnabled(true);
        }
        ImageButton addButton =
                (ImageButton) view.findViewById(R.id.custom_preference_widget_add);
        if (mValue == (MONTHS.length - 1)) {
            addButton.setEnabled(false);
        } else if (!addButton.isEnabled() && mValue < (MONTHS.length - 1)) {
            addButton.setEnabled(true);
        }

        TextView text =
                (TextView) view.findViewById(R.id.custom_preference_widget_value);
        text.setText(MONTHS[mValue]);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(0);
        } else {
            mValue = (Integer) defaultValue;
            persistInt(mValue);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.isEnabled()) {
            boolean valueChanged = false;
            int viewId = v.getId();
            if (viewId == R.id.custom_preference_widget_minus) {
                if (mValue > 0) {
                    --mValue;
                    valueChanged = true;

                }
            } else if (viewId == R.id.custom_preference_widget_add) {
                if (mValue < (MONTHS.length - 1)) {
                    ++mValue;
                    valueChanged = true;
                }
            }

            if (valueChanged) {
                persistInt(mValue);
                notifyChanged();
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState savedState = new SavedState(superState);
        savedState.setValue(mValue);
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mValue = savedState.getValue();
        notifyChanged();
    }

    private static class SavedState extends BaseSavedState {
        private int mValue;

        public SavedState(Parcel parcel) {
            super(parcel);
            mValue = parcel.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void setValue(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mValue);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
