package org.cbateman.oglesphysicsdemo.sprites;

import android.content.Context;
import android.util.Log;

import org.cbateman.oglesphysicsdemo.R;
import org.cbateman.oglesphysicsdemo.util.GraphicUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Renders a beachball image.
 */
public class BeachballImage extends Image {

    /**
     * BeachballImage constructor.
     *
     * @param context interface to resources
     * @param radius desired radius of beachball
     */
    public BeachballImage(Context context, float radius) {
        // Setup vertices data for beachball.
        final float[] verticesData = {
            -radius, radius, 0.0f, // Position 0
             0.0f, 0.0f, // TexCoord 0
            -radius, -radius, 0.0f, // Position 1
             0.0f, 1.0f, // TexCoord 1
             radius, -radius, 0.0f, // Position 2
             1.0f, 1.0f, // TexCoord 2
             radius, radius, 0.0f, // Position 3
             1.0f, 0.0f // TexCoord 3
        };

        mTexId = GraphicUtils.loadTexture(context.getResources().openRawResource(R.raw.beachball));

        mVertices = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(verticesData).position(0);

        // Setup data after defining vertices and texture(s).
        setupData();

        Log.i(TAG, "BeachballImage constructed");
    }
}
