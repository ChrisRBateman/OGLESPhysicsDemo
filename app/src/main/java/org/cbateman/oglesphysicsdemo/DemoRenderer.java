package org.cbateman.oglesphysicsdemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Draw;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.cbateman.oglesphysicsdemo.engine.PhysicsEngine;
import org.cbateman.oglesphysicsdemo.sprites.BackgroundImage;
import org.cbateman.oglesphysicsdemo.sprites.BeachballImage;
import org.cbateman.oglesphysicsdemo.sprites.WaterImage;
import org.cbateman.oglesphysicsdemo.sprites.WaterImageFactory;
import org.cbateman.oglesphysicsdemo.util.DebugDraw;

/**
 * Renderer class (registered with GLSurfaceView) that is responsible for making OpenGL
 * calls to render a frame.
 */
class DemoRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = Constants.TAG;
    private static final boolean DEBUG_DRAW = false;

    private static final float RADTODEG = 57.295779513082320876f;
    private static final float WORLD_BASE_SIZE = 20f;
    private static final float BB_RADIUS = 1.3f;

    private Context mContext;

    private BackgroundImage mBackgroundImage;
    private BeachballImage mBeachballImage;
    private WaterImage mWaterImage;

    private final float[] mPVMatrix = new float[16];
    private final float[] mTransformFromWorldMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] mScratch = new float[16];

    private final float[] mWVec = new float[4];
    private final float[] mTFWVec = new float[4];

    private static final float GRAVITY = 10f;
    private final float[] mGravityVec = new float[2];
    private PhysicsEngine mPhysicsEngine;
    private DebugDraw mDebugDraw = null;

    /**
     * FreeCellRenderer constructor.
     *
     * @param context the Context object
     */
    DemoRenderer(Context context) {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }
        mContext = context;

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                mGravityVec[0] = -GRAVITY;
                break;
            case Surface.ROTATION_90:
                mGravityVec[1] = -GRAVITY;
                break;
            case Surface.ROTATION_180:
                mGravityVec[0] = GRAVITY;
                break;
            case Surface.ROTATION_270:
                mGravityVec[1] = GRAVITY;
                break;
        }

        mPhysicsEngine = new PhysicsEngine();

        if (DEBUG_DRAW) {
            mDebugDraw = new DebugDraw();
            mDebugDraw.setFlags(Draw.SHAPE_BIT | Draw.PARTICLE_BIT);
        }

        Log.i(TAG, "DemoRenderer constructed");
    }

    /**
     * Clean up any resources used by renderer.
     */
    void cleanUp() {
        mPhysicsEngine.cleanUp();
        if (mDebugDraw != null) {
            mDebugDraw.delete();
            mDebugDraw = null;
        }
    }

    /**
     * Set the acceleration on the x and y axis. These are the values from
     * the accelerometer.
     *
     * @param x acceleration on x axis
     * @param y acceleration on y axis
     */
    void setAcceleration(float x, float y) {
        mPhysicsEngine.setWorldGravity(mGravityVec[0] * x - mGravityVec[1] * y,
                                       mGravityVec[1] * x + mGravityVec[0] * y);
    }

    // GLSurfaceView.Renderer ----------------------------------------------------------------------

    /**
     * Called when the surface is created or recreated.
     *
     * @param gl the GL interface
     * @param config the EGLConfig of the created surface
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        if (DEBUG_DRAW) {
            mDebugDraw.onSurfaceCreated();
        }
    }

    /**
     * Called when the surface changed size.
     *
     * @param gl the GL interface
     * @param width the surface width
     * @param height the surface height
     */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        float[] projectionMatrix = new float[16];
        float[] viewMatrix = new float[16];

        // Set the projection matrix
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mPVMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        float worldWidth = width * WORLD_BASE_SIZE / height;
        float worldHeight = WORLD_BASE_SIZE;

        // Transform world coord to opengl coord.
        Matrix.setIdentityM(mTransformFromWorldMatrix, 0);
        Matrix.translateM(mTransformFromWorldMatrix, 0, -ratio, -1, 0);
        Matrix.scaleM(mTransformFromWorldMatrix, 0, (2 * ratio) / worldWidth, 2f / worldHeight, 1);

        mBackgroundImage = new BackgroundImage(mContext);
        mWaterImage = WaterImageFactory.getWaterImage(mContext);
        mWaterImage.onSurfaceChanged(width, height, worldWidth, worldHeight);

        mPhysicsEngine.onSurfaceChanged(0.0f, 0.0f, worldWidth, worldHeight, BB_RADIUS);
        if (DEBUG_DRAW) {
            mDebugDraw.onSurfaceChanged(width, height, worldWidth, worldHeight);
            World world = mPhysicsEngine.getWorld();
            world.setDebugDraw(mDebugDraw);
        }

        float radius = 2 * (BB_RADIUS / worldHeight);
        mBeachballImage = new BeachballImage(mContext, radius);
    }

    /**
     * Called to draw the current frame.
     *
     * @param gl the GL interface
     */
    public void onDrawFrame(GL10 gl) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Enable blending for alpha channel images - enable for DebugDraw
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mPhysicsEngine.update();

        // Draw background image
        mBackgroundImage.draw(mPVMatrix);

        // Draw the water particles
        ParticleSystem particleSystem = mPhysicsEngine.getParticleSystem();
        if (particleSystem != null) {
            mWaterImage.draw(particleSystem, mPVMatrix, mTransformFromWorldMatrix);
        }

        // Draw the beachball
        Body dynamicBody = mPhysicsEngine.getDynamicBody();
        if (dynamicBody != null) {
            // position is in world coordinates
            Vec2 position = dynamicBody.getPosition();
            float angle = dynamicBody.getAngle();

            // convert position to opengl coordinates
            mWVec[0] = position.getX();
            mWVec[1] = position.getY();
            mWVec[2] = 0.0f;
            mWVec[3] = 1.0f;
            Matrix.multiplyMV(mTFWVec, 0, mTransformFromWorldMatrix, 0, mWVec, 0);

            // use new position and angle in model matrix -> apply model
            // matrix to beach ball image.
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, mTFWVec[0], mTFWVec[1], 0.0f);
            Matrix.rotateM(mModelMatrix, 0, angle * RADTODEG, 0.0f, 0.0f, 1.0f);
            Matrix.multiplyMM(mScratch, 0, mPVMatrix, 0, mModelMatrix, 0);

            mBeachballImage.draw(mScratch);
        }

        if (DEBUG_DRAW) {
            World world = mPhysicsEngine.getWorld();
            mDebugDraw.draw(world);
        }

        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
