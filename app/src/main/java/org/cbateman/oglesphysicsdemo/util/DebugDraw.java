package org.cbateman.oglesphysicsdemo.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.fpl.liquidfun.Color;
import com.google.fpl.liquidfun.Draw;
import com.google.fpl.liquidfun.Transform;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import org.cbateman.oglesphysicsdemo.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Provides debug drawing of physics entities. Based on DebugRenderer class from LiquidFunPaint
 * (https://google.github.io/LiquidFunPaint/)
 */
public class DebugDraw extends Draw {
    private static final String TAG = Constants.TAG;

    private static final int DEBUG_CAPACITY = 20000;
    private static final float DEBUG_OPACITY = 0.8f;
    private static final float DEBUG_AXIS_SCALE = 0.3f;

    private final float[] mTransformFromWorld = new float[16];

    private int mScreenWidth = 1;
    private int mScreenHeight = 1;

    private float mWorldWidth = 1.0f;
    private float mWorldHeight = 1.0f;

    private ByteBuffer mPolygonPositionBuffer;
    private ByteBuffer mPolygonColorBuffer;

    private ByteBuffer mCirclePositionBuffer;
    private ByteBuffer mCircleColorBuffer;
    private ByteBuffer mCirclePointSizeBuffer;

    private ByteBuffer mLinePositionBuffer;
    private ByteBuffer mLineColorBuffer;

    private StringBuilder sb = new StringBuilder();

    // Shader program used to draw polygons and segments.
    private int mNoTextureProgram;
    private int mNoTexturePositionLocation;
    private int mNoTextureColorLocation;
    private int mNoTextureTransformLocation;

    // Shader program used to draw circles and particles.
    private int mParticleProgram;
    private int mParticlePositionLocation;
    private int mParticleColorLocation;
    private int mParticlePointSizeLocation;
    private int mParticleTransformLocation;
    private int mParticleDiffuseTextureLocation;
    private int mParticleTexId;

    public DebugDraw() {
        mPolygonPositionBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());
        mPolygonColorBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());

        mCirclePositionBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());
        mCircleColorBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());
        mCirclePointSizeBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());

        mLinePositionBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());
        mLineColorBuffer = ByteBuffer.allocateDirect(DEBUG_CAPACITY)
                .order(ByteOrder.nativeOrder());

        Log.i(TAG, "DebugDraw constructed");
    }

    @Override
    public void drawPolygon(byte[] vertices, int vertexCount, Color color) {
        // This is equivalent to drawing lines with the same color at each
        // vertex
        int elementSize = 8; // We are dealing with 2 floats per vertex
        mLinePositionBuffer.put(vertices, 0 /* * elementSize */, elementSize);
        mLinePositionBuffer.put(vertices, /*1 * */elementSize, elementSize);

        mLinePositionBuffer.put(vertices, /*1 * */elementSize, elementSize);
        mLinePositionBuffer.put(vertices, 2 * elementSize, elementSize);

        mLinePositionBuffer.put(vertices, 2 * elementSize, elementSize);
        mLinePositionBuffer.put(vertices, 3 * elementSize, elementSize);

        mLinePositionBuffer.put(vertices, 3 * elementSize, elementSize);
        mLinePositionBuffer.put(vertices, 0 /* * elementSize */, elementSize);

        for (int i = 0; i < 8; ++i) {
            addColorToBuffer(mLineColorBuffer, color);
        }
    }

    @Override
    public void drawSolidPolygon(byte[] vertices, int vertexCount, Color color) {
        // Create 2 triangles from the vertices. Not using TRIANGLE_FAN due to
        // batching. Could optimize using TRIANGLE_STRIP.
        // 0, 1, 2, 3 -> (0, 1, 2), (0, 2, 3)
        int elementSize = 8; // We are dealing with 2 floats per vertex
        mPolygonPositionBuffer.put(vertices, 0 /* * elementSize */, elementSize);
        mPolygonPositionBuffer.put(vertices, /* 1 * */elementSize, elementSize);
        mPolygonPositionBuffer.put(vertices, 2 * elementSize, elementSize);

        mPolygonPositionBuffer.put(vertices, 0 /* * elementSize */, elementSize);
        mPolygonPositionBuffer.put(vertices, 2 * elementSize, elementSize);
        mPolygonPositionBuffer.put(vertices, 3 * elementSize, elementSize);

        for (int i = 0; i < 6; ++i) {
            addColorToBuffer(mPolygonColorBuffer, color);
        }
    }

    @Override
    public void drawCircle(Vec2 center, float radius, Color color) {
        mCirclePositionBuffer.putFloat(center.getX());
        mCirclePositionBuffer.putFloat(center.getY());
        addColorToBuffer(mCircleColorBuffer, color);

        float pointSize = getPointSize(radius);
        mCirclePointSizeBuffer.putFloat(pointSize);
    }

    @Override
    public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color color) {
        drawCircle(center, radius, color);

        // Draw the axis line
        float centerX = center.getX();
        float centerY = center.getY();
        addSegmentPoint(centerX, centerY, color.getR(), color.getG(), color.getB());
        addSegmentPoint(centerX + radius * axis.getX(),
                        centerY + radius * axis.getY(),
                        color.getR(),
                        color.getG(),
                        color.getB());
    }

    @Override
    public void drawParticles(byte[] centers, float radius, byte[] colors, int count) {
        // Draw them as circles
        mCirclePositionBuffer.put(centers);
        mCircleColorBuffer.put(colors);

        float pointSize = getPointSize(radius);
        for (int i = 0; i < count; ++i) {
            mCirclePointSizeBuffer.putFloat(pointSize);
        }
    }

    @Override
    public void drawSegment(Vec2 p1, Vec2 p2, Color color) {
        float r = color.getR();
        float g = color.getG();
        float b = color.getB();
        addSegmentPoint(p1.getX(), p1.getY(), r, g, b);
        addSegmentPoint(p2.getX(), p2.getY(), r, g, b);
    }

    @Override
    public void drawTransform(Transform xf) {
        float posX = xf.getPositionX();
        float posY = xf.getPositionY();

        float sine = xf.getRotationSin();
        float cosine = xf.getRotationCos();

        // X axis -- see b2Vec2::GetXAxis()
        addSegmentPoint(posX, posY, 1.0f, 0.0f, 0.0f);
        addSegmentPoint(posX + DEBUG_AXIS_SCALE * cosine,
                        posY + DEBUG_AXIS_SCALE * sine,
                        1.0f, 0.0f, 0.0f);

        // Y axis -- see b2Vec2::GetYAxis()
        addSegmentPoint(posX, posY, 0.0f, 1.0f, 0.0f);
        addSegmentPoint(posX + DEBUG_AXIS_SCALE * -sine,
                        posY + DEBUG_AXIS_SCALE * cosine,
                        0.0f, 1.0f, 0.0f);
    }

    /**
     * Called when the surface is created or recreated.
     */
    public void onSurfaceCreated() {
        final String noTextureVSCode =
            "attribute vec4 aPosition;" +
            "attribute vec4 aColor;" +
            "uniform mat4 uTransform;" +
            "varying vec4 vColor;" +
            "void main() {" +
                "gl_Position = uTransform * aPosition;" +
                "vColor = aColor;" +
            "}";

        final String noTextureFSCode =
            "precision lowp float;" +
            "varying vec4 vColor;" +
            "void main() {" +
                "gl_FragColor = vColor;" +
            "}";

        mNoTextureProgram = GraphicUtils.loadProgram(noTextureVSCode, noTextureFSCode);
        mNoTexturePositionLocation = GLES20.glGetAttribLocation(mNoTextureProgram, "aPosition");
        mNoTextureColorLocation = GLES20.glGetAttribLocation(mNoTextureProgram, "aColor");
        mNoTextureTransformLocation = GLES20.glGetUniformLocation(mNoTextureProgram, "uTransform");

        final String pointSpriteVSCode =
            "attribute vec4 aPosition;" +
            "attribute vec4 aColor;" +
            "attribute float aPointSize;" +
            "uniform mat4 uTransform;" +
            "varying vec4 vColor;" +
            "void main() {" +
                "gl_Position = uTransform * aPosition;" +
                "gl_PointSize = aPointSize;" +
                "vColor = aColor;" +
            "}";

        final String particleFSCode =
            "precision lowp float;" +
            "uniform sampler2D uDiffuseTexture;" +
            "varying vec4 vColor;" +
            "void main() {" +
                  "gl_FragColor = texture2D(uDiffuseTexture, gl_PointCoord).w * vColor;" +
            "}";

        mParticleProgram = GraphicUtils.loadProgram(pointSpriteVSCode, particleFSCode);
        mParticlePositionLocation = GLES20.glGetAttribLocation(mParticleProgram, "aPosition");
        mParticleColorLocation = GLES20.glGetAttribLocation(mParticleProgram, "aColor");
        mParticlePointSizeLocation = GLES20.glGetAttribLocation(mParticleProgram, "aPointSize");
        mParticleTransformLocation = GLES20.glGetUniformLocation(mParticleProgram, "uTransform");
        mParticleDiffuseTextureLocation = GLES20.glGetUniformLocation(mParticleProgram, "uDiffuseTexture");

        Bitmap bm = createCircleBitmap(64, 64);
        mParticleTexId = GraphicUtils.loadTexture(bm);
        bm.recycle();
    }

    /**
     * Called when the surface changed size.
     *
     * @param sWidth the surface width
     * @param sHeight the surface height
     * @param wWidth the world width
     * @param wHeight the world height
     */
    public void onSurfaceChanged(int sWidth, int sHeight, float wWidth, float wHeight) {
        mScreenWidth = sWidth;
        mScreenHeight = sHeight;

        GLES20.glViewport(0, 0, mScreenWidth, mScreenHeight);

        mWorldWidth = wWidth;
        mWorldHeight = wHeight;

        Matrix.setIdentityM(mTransformFromWorld, 0);
        Matrix.translateM(mTransformFromWorld, 0, -1, -1, 0);
        Matrix.scaleM(mTransformFromWorld,
                      0,
                      2f / mWorldWidth,
                      2f / mWorldHeight,
                      1);
    }

    public void draw(World world) {
        resetAllBuffers();

        world.drawDebugData();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        drawPolygons(mTransformFromWorld);
        drawCircles(mTransformFromWorld);
        drawSegments(mTransformFromWorld);
    }

    // Private methods -----------------------------------------------------------------------------

    private void addColorToBuffer(ByteBuffer buffer, float r, float g, float b) {
        buffer.put((byte)(r * 255));
        buffer.put((byte)(g * 255));
        buffer.put((byte)(b * 255));
        buffer.put((byte)(DEBUG_OPACITY * 255));
    }

    private void addColorToBuffer(ByteBuffer buffer, Color color) {
        addColorToBuffer(buffer, color.getR(), color.getG(), color.getB());
    }

    private void addSegmentPoint(float x, float y, float r, float g, float b) {
        mLinePositionBuffer.putFloat(x);
        mLinePositionBuffer.putFloat(y);
        addColorToBuffer(mLineColorBuffer, r, g, b);
    }

    private float getPointSize(float radius) {
        float maxScreenSize = Math.min(mScreenWidth, mScreenHeight);
        float maxWorldSize = Math.min(mWorldWidth, mWorldHeight);

        return Math.max(1.0f, maxScreenSize * (2.0f * radius / maxWorldSize));
    }

    private void resetAllBuffers() {
        mPolygonPositionBuffer.clear();
        mPolygonColorBuffer.clear();

        mCirclePositionBuffer.clear();
        mCircleColorBuffer.clear();
        mCirclePointSizeBuffer.clear();

        mLinePositionBuffer.clear();
        mLineColorBuffer.clear();
    }

    private void drawPolygons(float[] transformFromWorld) {
        GLES20.glUseProgram(mNoTextureProgram);

        int numElements = mPolygonPositionBuffer.position() / (4 * 2);

        mPolygonPositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mNoTexturePositionLocation,
                                     2, GLES20.GL_FLOAT, false, 0,
                                     mPolygonPositionBuffer);
        GLES20.glEnableVertexAttribArray(mNoTexturePositionLocation);

        mPolygonColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mNoTextureColorLocation,
                                     4, GLES20.GL_UNSIGNED_BYTE, true, 0,
                                     mPolygonColorBuffer);
        GLES20.glEnableVertexAttribArray(mNoTextureColorLocation);

        GLES20.glUniformMatrix4fv(mNoTextureTransformLocation, 1, false, transformFromWorld, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numElements);

        GLES20.glDisableVertexAttribArray(mNoTexturePositionLocation);
        GLES20.glDisableVertexAttribArray(mNoTextureColorLocation);
    }

    private void drawCircles(float[] transformFromWorld) {
        GLES20.glUseProgram(mParticleProgram);

        int numElements = mCirclePointSizeBuffer.position() / 4;

        mCirclePositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mParticlePositionLocation,
                                     2, GLES20.GL_FLOAT, false, 0,
                                     mCirclePositionBuffer);
        GLES20.glEnableVertexAttribArray(mParticlePositionLocation);

        mCircleColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mParticleColorLocation,
                                     4, GLES20.GL_UNSIGNED_BYTE, true, 0,
                                     mCircleColorBuffer);
        GLES20.glEnableVertexAttribArray(mParticleColorLocation);

        mCirclePointSizeBuffer.position(0);
        GLES20.glVertexAttribPointer(mParticlePointSizeLocation,
                                     1, GLES20.GL_FLOAT, false, 0,
                                     mCirclePointSizeBuffer);
        GLES20.glEnableVertexAttribArray(mParticlePointSizeLocation);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mParticleTexId);
        GLES20.glUniform1i(mParticleDiffuseTextureLocation, 0);

        GLES20.glUniformMatrix4fv(mParticleTransformLocation, 1, false, transformFromWorld, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numElements);

        GLES20.glDisableVertexAttribArray(mParticlePositionLocation);
        GLES20.glDisableVertexAttribArray(mParticleColorLocation);
        GLES20.glDisableVertexAttribArray(mParticlePointSizeLocation);
    }

    private void drawSegments(float[] transformFromWorld) {
        GLES20.glUseProgram(mNoTextureProgram);

        int numElements = mLinePositionBuffer.position() / (4 * 2);

        mLinePositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mNoTexturePositionLocation,
                2, GLES20.GL_FLOAT, false, 0,
                mLinePositionBuffer);
        GLES20.glEnableVertexAttribArray(mNoTexturePositionLocation);

        mLineColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mNoTextureColorLocation,
                4, GLES20.GL_UNSIGNED_BYTE, true, 0,
                mLineColorBuffer);
        GLES20.glEnableVertexAttribArray(mNoTextureColorLocation);

        GLES20.glUniformMatrix4fv(mNoTextureTransformLocation, 1, false, transformFromWorld, 0);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, numElements);

        GLES20.glDisableVertexAttribArray(mNoTexturePositionLocation);
        GLES20.glDisableVertexAttribArray(mNoTextureColorLocation);
    }

    /**
     * Creates a circle bitmap with transparent background.
     *
     * @param width the width of bitmap
     * @param height the height of bitmap (width should equal height)
     * @return Bitmap object
     */
    @SuppressWarnings("SameParameterValue")
    private Bitmap createCircleBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);

        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        canvas.drawCircle(width / 2, height / 2, (width / 2) - 2, paint);

        return bitmap;
    }

    /**
     * Converts and logs bytes as float values.
     *
     * @param buffer the byte buffer object
     */
    @SuppressWarnings("unused")
    private void logBytesAsFloats(ByteBuffer buffer) {
        // 4 bytes per float times 2 floats (x and y)
        int numElements = buffer.position() / (4 * 2);
        Log.i(TAG, "DebugDraw.logBytesAsFloats : numElements = " + numElements);

        int count = 0;
        sb.setLength(0);
        FloatBuffer fb = ((ByteBuffer)buffer.rewind()).asFloatBuffer();

        sb.append("[");
        while (fb.hasRemaining() && (count < numElements)) {
            sb.append("|");
            sb.append(fb.position());
            sb.append(" -> ");
            sb.append(fb.get());
            count++;
        }
        sb.append("]");
        Log.w(TAG, "DebugDraw.logBytesAsFloats : float buffer = " + sb.toString());
    }
}
