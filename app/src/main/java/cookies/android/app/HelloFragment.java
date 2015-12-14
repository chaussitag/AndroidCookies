package cookies.android.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import cookies.android.R;

public class HelloFragment extends Activity implements SimpleFragment
        .OnFragmentInteractionListener {

    private static final String TAG = HelloFragment.class.getName();

    private static final String FRAGMENT_TAG = SimpleFragment.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hello_fragment);


        RelativeLayout container = (RelativeLayout) findViewById(R.id.fragment_container);
        Log.d(TAG, "fragment_container " + container);

        FragmentManager fragmentManager = getFragmentManager();
        // NOTE:
        // if savedInstanceState != null,
        // Activity.onCreate() calls FragmentManagerImpl.restoreAllState(),
        // which creates all fragments hold by this activity and restore their state.
        // if just add a new fragment without the if statement, then there will be more and more
        // duplicated fragments each time you rotate the screen, including the system restored
        // fragments and those you added here, resulting in lots of duplicated action items if
        // some fragment has contribution to the action bar.
        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            Fragment simpleFragment = SimpleFragment.newInstance("param1", "param2");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, simpleFragment, FRAGMENT_TAG);
            transaction.commit();
        } else {
            // nothing to do, the framework will restore the fragment
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello_fragment_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.hello_frag_activity_item1) {
            return true;
        } else if (id == R.id.hello_frag_activity_item2) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
