package org.cbateman.oglesphysicsdemo.sprites;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleSystem;

import org.cbateman.oglesphysicsdemo.Constants;
import org.cbateman.oglesphysicsdemo.R;
import org.cbateman.oglesphysicsdemo.engine.PhysicsEngine;
import org.cbateman.oglesphysicsdemo.util.FrameBufferObject;
import org.cbateman.oglesphysicsdemo.util.GraphicUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Renders water image using frame buffer objects.
 */
class WaterImageFBOImpl implements WaterImage {
    private static final String TAG = Constants.TAG;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    private static final float WATER_COLOR_RED = 66.0f / 255.0f;
    private static final float WATER_COLOR_GREEN = 94.0f / 255.0f;
    private static final float WATER_COLOR_BLUE = 255.0f / 255.0f;
    private static final float WATER_COLOR_ALPHA = 0.6f;

    private ByteBuffer mParticlePositionBuffer;

    private int mScreenWidth;
    private int mScreenHeight;

    private float mWorldWidth;
    private float mWorldHeight;

    private final int[] mPrevBlend = new int[2];
    private final float[] mScratch = new float[16];

    private FrameBufferObject mParticleFBO;

    private int mBlendProgram;
    private int mBlendBlurredTexId;
    private int mBlendPositionLocation;
    private int mBlendTransformLocation;
    private int mBlendPointSizeLocation;
    private int mBlendDiffuseTextureLocation;

    private int mThresholdProgram;
    private int mThresholdPositionLocation;
    private int mThresholdTexCoordLocation;
    private int mThresholdMVPMatrixLocation;
    private int mThresholdColorLocation;
    private int mThresholdSamplerLocation;

    private final int[] vbo = new int[1];
    private final int[] ibo = new int[1];

    WaterImageFBOImpl(Context context) {
        mParticlePositionBuffer = ByteBuffer
                .allocateDirect(2 * 4 * PhysicsEngine.MAX_PARTICLE_COUNT)
                .order(ByteOrder.nativeOrder());

        final String blendVSCode =
            "attribute vec4 aPosition;" +
            "uniform mat4 uTransform;" +
            "uniform float uPointSize;" +
            "void main() {" +
                "gl_Position = uTransform * aPosition;" +
                "gl_PointSize = uPointSize;" +
            "}";

        final String blendFSCode =
            "precision lowp float;" +
            "uniform sampler2D uDiffuseTexture;" +
            "void main() {" +
                "gl_FragColor = texture2D(uDiffuseTexture, gl_PointCoord);" +
            "}";

        mBlendProgram = GraphicUtils.loadProgram(blendVSCode, blendFSCode);
        Resources res = context.getResources();
        mBlendBlurredTexId = GraphicUtils.loadTexture(res.openRawResource(R.raw.metaparticle));

        mBlendPositionLocation = GLES20.glGetAttribLocation(mBlendProgram, "aPosition");
        mBlendTransformLocation = GLES20.glGetUniformLocation(mBlendProgram, "uTransform");
        mBlendPointSizeLocation = GLES20.glGetUniformLocation(mBlendProgram, "uPointSize");
        mBlendDiffuseTextureLocation = GLES20.glGetUniformLocation(mBlendProgram, "uDiffuseTexture");

        final String thresholdVSCode =
            "attribute vec4 aPosition;" +
            "attribute vec2 aTexCoord;" +
            "uniform mat4 uMVPMatrix;" +
            "uniform vec4 uColor;" +
            "varying vec2 vTexCoord;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "    gl_Position = uMVPMatrix * aPosition;" +
            "    vTexCoord = aTexCoord;" +
            "    vColor = uColor;" +
            "}";

        final String thresholdFSCode =
            "precision lowp float;" +
            "uniform sampler2D sTexture;" +
            "varying vec2 vTexCoord;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "    vec4 tex = texture2D(sTexture, vTexCoord);" +
            "    if (tex.a > 0.7) {" +
            "        gl_FragColor = vColor;" +
            "    } else {" +
            "        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);" +
            "    }" +
            "}";

        // Create program from shaders
        mThresholdProgram = GraphicUtils.loadProgram(thresholdVSCode, thresholdFSCode);

        // Get locations
        mThresholdPositionLocation = GLES20.glGetAttribLocation(mThresholdProgram, "aPosition");
        mThresholdTexCoordLocation = GLES20.glGetAttribLocation(mThresholdProgram, "aTexCoord");
        mThresholdMVPMatrixLocation = GLES20.glGetUniformLocation(mThresholdProgram, "uMVPMatrix");
        mThresholdColorLocation = GLES20.glGetUniformLocation(mThresholdProgram, "uColor");
        mThresholdSamplerLocation = GLES20.glGetUniformLocation(mThresholdProgram, "sTexture");

        final short[] indicesData = {
            0, 1, 2, 0, 2, 3
        };
        ShortBuffer indices = ByteBuffer.allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indices.put(indicesData).position(0);

        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glGenBuffers(1, ibo, 0);

        if (vbo[0] > 0 && ibo[0] > 0) {
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * BYTES_PER_SHORT,
                    indices, GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        Log.i(TAG, "WaterImageFBOImpl constructed");
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

        // Setup vertices data for particles.
        float ratio = (float)sWidth / (float)sHeight;
        final float[] verticesData = {
            -ratio, 1.0f, 0.0f, // Position 0
            0.0f, 1.0f, // TexCoord 0
            -ratio, -1.0f, 0.0f, // Position 1
            0.0f, 0.0f, // TexCoord 1
            ratio, -1.0f, 0.0f, // Position 2
            1.0f, 0.0f, // TexCoord 2
            ratio, 1.0f, 0.0f, // Position 3
            1.0f, 1.0f // TexCoord 3
        };

        FloatBuffer vertices = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);

        if (vbo[0] > 0 && ibo[0] > 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.capacity() * BYTES_PER_FLOAT,
                    vertices, GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        mParticleFBO = new FrameBufferObject(sWidth, sHeight);
        mParticleFBO.setClearColor(Color.argb(0, 255, 255, 255));
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

        // Save previous blending function
        GLES20.glGetIntegerv(GLES20.GL_BLEND_SRC_ALPHA, mPrevBlend, 0);
        GLES20.glGetIntegerv(GLES20.GL_BLEND_DST_ALPHA, mPrevBlend, 1);

        // Setup the frame buffer to render the particles using additive blending
        mParticleFBO.beginRender(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mBlendProgram);

        // Enable additive blending
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

        mParticlePositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mBlendPositionLocation, 2, GLES20.GL_FLOAT, false, 0,
                mParticlePositionBuffer);
        GLES20.glEnableVertexAttribArray(mBlendPositionLocation);

        GLES20.glUniform1f(mBlendPointSizeLocation, getPointSize(PhysicsEngine.PARTICLE_RADIUS));

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlendBlurredTexId);
        GLES20.glUniform1i(mBlendDiffuseTextureLocation, 0);

        Matrix.multiplyMM(mScratch, 0, vpMatrix, 0, worldMatrix, 0);
        GLES20.glUniformMatrix4fv(mBlendTransformLocation, 1, false, mScratch, 0);

        ParticleGroup currGroup = particleSystem.getParticleGroupList();
        int particleCount = currGroup.getParticleCount();
        int instanceOffset = currGroup.getBufferIndex();
        GLES20.glDrawArrays(GLES20.GL_POINTS, instanceOffset, particleCount);

        mParticleFBO.endRender();

        GLES20.glBlendFunc(mPrevBlend[0], mPrevBlend[1]);

        // Adjust the pixel colors of the texture from frame buffer
        GLES20.glViewport(0, 0, mScreenWidth, mScreenHeight);
        int texId = mParticleFBO.getTextureId();
        if (vbo[0] > 0 && ibo[0] > 0) {
            GLES20.glUseProgram(mThresholdProgram);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

            GLES20.glVertexAttribPointer(mThresholdPositionLocation, 3, GLES20.GL_FLOAT,
                    false, 5 * 4, 0);
            GLES20.glEnableVertexAttribArray(mThresholdPositionLocation);

            GLES20.glVertexAttribPointer(mThresholdTexCoordLocation, 2, GLES20.GL_FLOAT, false,
                    5 * 4, 12);
            GLES20.glEnableVertexAttribArray(mThresholdTexCoordLocation);

            // Bind the texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

            // Set the sampler texture unit to 0
            GLES20.glUniform1i(mThresholdSamplerLocation, 0);

            // Set color/alpha for each pixel
            GLES20.glUniform4f(mThresholdColorLocation,
                    WATER_COLOR_RED,
                    WATER_COLOR_GREEN,
                    WATER_COLOR_BLUE,
                    WATER_COLOR_ALPHA);

            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mThresholdMVPMatrixLocation, 1, false, vpMatrix, 0);

            // Draw
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
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

        return Math.max(1.0f, 2.5f * maxScreenSize * (2.0f * radius / maxWorldSize));
    }
}
