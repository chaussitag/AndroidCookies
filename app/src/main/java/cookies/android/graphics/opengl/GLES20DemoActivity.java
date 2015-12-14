package cookies.android.graphics.opengl;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cookies.android.R;

public class GLES20DemoActivity extends Activity {

    private static final String TAG = GLES20DemoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gles20_demo);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.glsurfaceview_container, new PlaceholderFragment()).commit();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private LocalGLSurfaceView mGLSurfaceView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            mGLSurfaceView = new LocalGLSurfaceView(getActivity().getApplication());
            return mGLSurfaceView;
        }

        @Override
        public void onResume() {
            Log.d(TAG, "PlaceHolderFragment.onResume()");
            super.onResume();
            mGLSurfaceView.requestRender();
        }

        @Override
        public void onPause() {
            Log.d(TAG, "PlaceHolderFragment.onPause()");
            super.onPause();
        }
    }

    private static class LocalGLSurfaceView extends GLSurfaceView {
        public LocalGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            this.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
            setRenderer(new DemoGLRenderer(context));
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }
}
