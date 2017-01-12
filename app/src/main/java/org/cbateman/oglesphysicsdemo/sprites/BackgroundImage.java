package org.cbateman.oglesphysicsdemo.sprites;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.cbateman.oglesphysicsdemo.R;
import org.cbateman.oglesphysicsdemo.util.GraphicUtils;

/**
 * Renders a background image.
 */
public class BackgroundImage extends Image {

    /**
     * BackgroundImage constructor.
     *
     * @param context interface to resources
     */
    public BackgroundImage(Context context) {
        // Setup vertices data for stars.
        final float[] verticesData = {
            -1.0f, 1.0f, 0.0f, // Position 0
            0.0f, 0.0f, // TexCoord 0
            -1.0f, -1.0f, 0.0f, // Position 1
            0.0f, 1.0f, // TexCoord 1
            1.0f, -1.0f, 0.0f, // Position 2
            1.0f, 1.0f, // TexCoord 2
            1.0f, 1.0f, 0.0f, // Position 3
            1.0f, 0.0f // TexCoord 3
        };

        mTexId = GraphicUtils.loadTexture(context.getResources().openRawResource(R.raw.sand));

        mVertices = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(verticesData).position(0);

        // Setup data after defining vertices and texture(s).
        setupData();

        Log.i(TAG, "BackgroundImage constructed");
    }
}
