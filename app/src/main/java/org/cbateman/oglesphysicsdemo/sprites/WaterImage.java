package org.cbateman.oglesphysicsdemo.sprites;

import com.google.fpl.liquidfun.ParticleSystem;

/**
 * Defines an object to render water particles.
 */
public interface WaterImage {

    /**
     * Called when the surface changes size.
     *
     * @param sWidth the surface width
     * @param sHeight the surface height
     * @param wWidth the world width
     * @param wHeight the world height
     */
    void onSurfaceChanged(int sWidth, int sHeight, float wWidth, float wHeight);

    /**
     * Render the water image.
     *
     * @param particleSystem provides particles to render water
     * @param vpMatrix projection view matrix
     * @param worldMatrix world transformation matrix
     */
    void draw(ParticleSystem particleSystem, float[] vpMatrix, float[] worldMatrix);
}
