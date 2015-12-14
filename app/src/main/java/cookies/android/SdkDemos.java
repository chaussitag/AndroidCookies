package cookies.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdkDemos extends ListActivity {
    private static final String TAG = SdkDemos.class.getName();

    private static final String LIST_ITEM_TEXT_KEY = "title";
    private static final String LIST_ITEM_INTENT_KEY = "intent";
    private static final String LIST_ITEM_PATH_PREFIX_KEY = "prefix";

    private static final String DEMO_INTENT_ACTION_TAG =
            "cookies.android.intent.action.demo_tag";
    private static final String DEMO_INTENT_CATEGORY_TAG =
            "cookies.android.intent.category.demo_tag";

    private static final String SAVED_PREFIX = "cookies.android.sdkdemos.current_prfix";
    private String mCurrentPrefix = "";

    private LocalAdapter mAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentPrefix = savedInstanceState.getString(SAVED_PREFIX, "");
        }

        mAdapter = new LocalAdapter(this);
        setListAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        reloadData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_PREFIX, mCurrentPrefix);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onListItemClick(ListView lv, View view, int position, long id) {
        Map<String, Object> item = (Map<String, Object>) lv.getItemAtPosition(position);
        if (item.containsKey(LIST_ITEM_INTENT_KEY)) {
            Intent startIntent = (Intent) item.get(LIST_ITEM_INTENT_KEY);
            startActivity(startIntent);
        } else if (item.containsKey(LIST_ITEM_PATH_PREFIX_KEY)) {
            mCurrentPrefix = (String) item.get(LIST_ITEM_PATH_PREFIX_KEY);
            reloadData();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPrefix.length() > 0) {
            int lastSlashPos = mCurrentPrefix.lastIndexOf('/');

            if (lastSlashPos != -1) {
                mCurrentPrefix = mCurrentPrefix.substring(0, lastSlashPos);
            } else {
                mCurrentPrefix = "";
            }
            reloadData();
            return;
        }
        super.onBackPressed();
    }

    private void reloadData() {
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        Intent intent = new Intent(DEMO_INTENT_ACTION_TAG, null);
        intent.addCategory(DEMO_INTENT_CATEGORY_TAG);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (null == activities) {
            return;
        }

        String[] prefixSegments = null;
        String prefixWithSlash = mCurrentPrefix;
        if (mCurrentPrefix.length() > 0) {
            prefixSegments = mCurrentPrefix.split("/");
            prefixWithSlash = mCurrentPrefix + "/";
        }

        Map<String, Boolean> entries = new HashMap<String, Boolean>();
        for (ResolveInfo activity : activities) {
            CharSequence labelSeq = activity.loadLabel(pm);
            String label = ((labelSeq != null) ? labelSeq.toString() : activity.activityInfo.name);
            if (mCurrentPrefix.length() == 0 || label.startsWith(prefixWithSlash)) {
                String[] labelSegments = label.split("/");
                String nextLabel = ((prefixSegments == null) ? labelSegments[0] :
                        labelSegments[prefixSegments.length]);
                if ((prefixSegments != null ? prefixSegments.length : 0)
                        == (labelSegments.length - 1)) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    Intent startIntent = new Intent();
                    startIntent.setClassName(activity.activityInfo.applicationInfo.packageName,
                            activity.activityInfo.name);
                    item.put(LIST_ITEM_TEXT_KEY, nextLabel);
                    item.put(LIST_ITEM_INTENT_KEY, startIntent);
                    data.add(item);
                } else {
                    if (!entries.containsKey(nextLabel)) {
                        HashMap<String, Object> item = new HashMap<String, Object>();
                        item.put(LIST_ITEM_TEXT_KEY, nextLabel);
                        item.put(LIST_ITEM_PATH_PREFIX_KEY, (mCurrentPrefix.length() == 0) ?
                                nextLabel : prefixWithSlash + nextLabel);
                        data.add(item);
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        mAdapter.setData(data);
    }

    // ===========================================================================
    // class LocalAdapter
    private static class LocalAdapter extends BaseAdapter {
        private Context mContext = null;
        private List<HashMap<String, Object>> mData = null;

        public LocalAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<HashMap<String, Object>> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return (mData != null) ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return (mData != null) ? mData.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView itemView = null;
            HashMap<String, Object> itemData = (HashMap<String, Object>) getItem(position);
            if (itemData != null) {
                if (convertView == null) {
                    itemView = (TextView) LayoutInflater.from(mContext).inflate(
                            android.R.layout.simple_list_item_1, parent, false);
                } else {
                    itemView = (TextView) convertView;
                }
                String title = (String) itemData.get(LIST_ITEM_TEXT_KEY);
                itemView.setText(title);
            }
            return itemView;
        }
    }
    // ===========================================================================
}
