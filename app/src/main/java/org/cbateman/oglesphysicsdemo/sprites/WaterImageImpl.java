package org.cbateman.oglesphysicsdemo.sprites;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleSystem;

import org.cbateman.oglesphysicsdemo.Constants;
import org.cbateman.oglesphysicsdemo.R;
import org.cbateman.oglesphysicsdemo.engine.PhysicsEngine;
import org.cbateman.oglesphysicsdemo.util.GraphicUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Renders water image using default frame buffer.
 */
class WaterImageImpl implements WaterImage {
    private static final String TAG = Constants.TAG;

    private ByteBuffer mParticlePositionBuffer;

    private int mScreenWidth;
    private int mScreenHeight;

    private float mWorldWidth;
    private float mWorldHeight;

    private final float[] mScratch = new float[16];

    private int mParticleProgram;
    private int mParticleBlurredTexId;
    private int mParticlePositionLocation;
    private int mParticleTransformLocation;
    private int mParticlePointSizeLocation;
    private int mParticleDiffuseTextureLocation;

    WaterImageImpl(Context context) {
        mParticlePositionBuffer = ByteBuffer
                .allocateDirect(2 * 4 * PhysicsEngine.MAX_PARTICLE_COUNT)
                .order(ByteOrder.nativeOrder());

        final String particleVSCode =
            "attribute vec4 aPosition;" +
            "uniform mat4 uTransform;" +
            "uniform float uPointSize;" +
            "void main() {" +
            "    gl_Position = uTransform * aPosition;" +
            "    gl_PointSize = uPointSize;" +
            "}";

        final String particleFSCode =
            "precision lowp float;" +
            "uniform sampler2D uDiffuseTexture;" +
            "void main() {" +
            "    gl_FragColor = texture2D(uDiffuseTexture, gl_PointCoord);" +
            "}";

        mParticleProgram = GraphicUtils.loadProgram(particleVSCode, particleFSCode);
        Resources res = context.getResources();
        mParticleBlurredTexId = GraphicUtils.loadTexture(res.openRawResource(R.raw.bubble));

        mParticlePositionLocation = GLES20.glGetAttribLocation(mParticleProgram, "aPosition");
        mParticleTransformLocation = GLES20.glGetUniformLocation(mParticleProgram, "uTransform");
        mParticlePointSizeLocation = GLES20.glGetUniformLocation(mParticleProgram, "uPointSize");
        mParticleDiffuseTextureLocation = GLES20.glGetUniformLocation(mParticleProgram, "uDiffuseTexture");

        Log.i(TAG, "WaterImageImpl constructed");
    }

    /**
     * Called when the surface changes size.
     *
     * @param sWidth the surface width
     * @param sHeight the surface height
     * @param wWidth the world width
     * @param wHeight the world height
     */
    public void onSurfaceChanged(int sWidth, int sHeight, float wWidth, float wHeight) {
        mScreenWidth = sWidth;
        mScreenHeight = sHeight;

        mWorldWidth = wWidth;
        mWorldHeight = wHeight;
    }

    /**
     * Render the water image.
     *
     * @param particleSystem provides particles to render water
     * @param vpMatrix projection view matrix
     * @param worldMatrix world transformation matrix
     */
    public void draw(ParticleSystem particleSystem, float[] vpMatrix, float[] worldMatrix) {
        mParticlePositionBuffer.rewind();

        int worldParticleCount = particleSystem.getParticleCount();
        particleSystem.copyPositionBuffer(0, worldParticleCount, mParticlePositionBuffer);

        GLES20.glUseProgram(mParticleProgram);

        mParticlePositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mParticlePositionLocation, 2, GLES20.GL_FLOAT, false, 0,
                mParticlePositionBuffer);
        GLES20.glEnableVertexAttribArray(mParticlePositionLocation);

        GLES20.glUniform1f(mParticlePointSizeLocation, getPointSize(PhysicsEngine.PARTICLE_RADIUS));

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mParticleBlurredTexId);
        GLES20.glUniform1i(mParticleDiffuseTextureLocation, 0);

        Matrix.multiplyMM(mScratch, 0, vpMatrix, 0, worldMatrix, 0);
        GLES20.glUniformMatrix4fv(mParticleTransformLocation, 1, false, mScratch, 0);

        ParticleGroup currGroup = particleSystem.getParticleGroupList();
        int particleCount = currGroup.getParticleCount();
        int instanceOffset = currGroup.getBufferIndex();
        GLES20.glDrawArrays(GLES20.GL_POINTS, instanceOffset, particleCount);
    }

    // Private methods -----------------------------------------------------------------------------

    /**
     * Returns point size based on particle radius.
     *
     * @param radius the particle radius
     * @return point size
     */
    private float getPointSize(float radius) {
        float maxScreenSize = Math.min(mScreenWidth, mScreenHeight);
        float maxWorldSize = Math.min(mWorldWidth, mWorldHeight);

        return Math.max(1.0f, maxScreenSize * (2.0f * radius / maxWorldSize));
    }
}

