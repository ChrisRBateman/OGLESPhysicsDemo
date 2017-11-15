package org.cbateman.oglesphysicsdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Extends GLSurfaceView to handle touch and sensor events.
 */
public class DemoSurfaceView extends GLSurfaceView implements SensorEventListener {
    private static final String TAG = Constants.TAG;

    private SensorManager mManager;
    private Sensor mAccelerometer;
    private DemoRenderer mRenderer;

    public DemoSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public DemoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Sets the user's renderer.
     *
     * @param renderer the DemoRenderer
     */
    public void setRenderer(DemoRenderer renderer) {
        mRenderer = renderer;
        super.setRenderer(renderer);
    }

    public void registerAccelerometer() {
        mManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterAccelerometer() {
        mManager.unregisterListener(this);
    }

    public void cleanUp() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.cleanUp();
            }
        });
    }

    // SensorEventListener -------------------------------------------------------------------------

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float x = event.values[0];
            final float y = event.values[1];

            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.setAcceleration(x, y);
                }
            });
        }
    }

    // Private methods -----------------------------------------------------------------------------

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setPreserveEGLContextOnPause() {
        setPreserveEGLContextOnPause(true);
    }

    /**
     * Initialize surface view.
     */
    private void init(Context context) {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        if (BuildConfig.DEBUG) {
            setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
        }
        setPreserveEGLContextOnPause();

        mManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        if (mManager != null) {
            mAccelerometer = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        Log.i(TAG, "DemoSurfaceView initialized");
    }
}

