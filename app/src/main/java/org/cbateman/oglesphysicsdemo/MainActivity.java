package org.cbateman.oglesphysicsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG;

    private DemoSurfaceView mDemoSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Explicitly load all shared libraries
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        mDemoSurfaceView = new DemoSurfaceView(this);
        DemoRenderer demoRenderer = new DemoRenderer(this);

        mDemoSurfaceView.setRenderer(demoRenderer);
        setContentView(mDemoSurfaceView);

        Log.i(TAG, "MainActivity created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDemoSurfaceView.onResume();
        mDemoSurfaceView.registerAccelerometer();

        Log.i(TAG, "MainActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDemoSurfaceView.onPause();
        mDemoSurfaceView.unregisterAccelerometer();

        Log.i(TAG, "MainActivity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDemoSurfaceView.cleanUp();

        Log.i(TAG, "MainActivity destroyed");
    }
}
