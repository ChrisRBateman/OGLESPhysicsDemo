package org.cbateman.oglesphysicsdemo.engine;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.ParticleColor;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import org.cbateman.oglesphysicsdemo.Constants;

/**
 * Class uses the LiquidFun lib to provide 2D rigid-body simulation.
 * All interactions with the library will be through this class.
 */
public class PhysicsEngine {
    private static final String TAG = Constants.TAG;

    private static final float TIME_STEP = 1 / 60f; // 60 fps
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final int PARTICLE_ITERATIONS = 5;

    // Adjusting this value will increase/decrease number of particles.
    public static final float PARTICLE_RADIUS = 0.27f;

    private static final float PARTICLE_REPULSIVE_STRENGTH = 0.5f;
    public static final int MAX_PARTICLE_COUNT = 5000;

    private static final float BOUNDARY_WIDTH = 0.5f;
    private static final float BOUNDARY_HEIGHT = 0.5f;
    private static final float BOUNDARY_OFFSET = 0.5f;

    private World mWorld = null;
    private ParticleSystem mParticleSystem = null;
    private Body mBoundaryBody = null;
    private Body mDynamicBody = null;

    public PhysicsEngine() {
        Log.i(TAG, "PhysicsEngine constructed");
    }

    /**
     * Set up engine when surface changes. I set up the coordinate system where (0,0) is the
     * lower left corner and (world width, world height) is the upper right
     * corner.
     *
     * @param x1 lower left x coordinate
     * @param y1 lower left y coordinate
     * @param x2 upper right x coordinate
     * @param y2 upper right y coordinate
     * @param radius the radius of dynamic body
     */
    @SuppressWarnings("SameParameterValue")
    public void onSurfaceChanged(float x1, float y1, float x2, float y2, float radius) {
        cleanUp();

        // Create a new world.
        mWorld = new World(0, 0);

        // Create a new particle system.
        ParticleSystemDef psDef = new ParticleSystemDef();
        psDef.setRadius(PARTICLE_RADIUS);
        psDef.setRepulsiveStrength(PARTICLE_REPULSIVE_STRENGTH);
        mParticleSystem = mWorld.createParticleSystem(psDef);
        mParticleSystem.setMaxParticleCount(MAX_PARTICLE_COUNT);
        psDef.delete();

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(0, 0);
        PolygonShape boundaryPolygon = new PolygonShape();

        mBoundaryBody = mWorld.createBody(bodyDef);

        float width = x2 - x1;
        float height = y2 - y1;

        // ground
        boundaryPolygon.setAsBox(width, BOUNDARY_HEIGHT, x2 / 2, y1 - BOUNDARY_OFFSET, 0);
        mBoundaryBody.createFixture(boundaryPolygon, 0.0f);

        // ceiling
        boundaryPolygon.setAsBox(width, BOUNDARY_HEIGHT, x2 / 2, y2 + BOUNDARY_OFFSET, 0);
        mBoundaryBody.createFixture(boundaryPolygon, 0.0f);

        // left wall
        boundaryPolygon.setAsBox(BOUNDARY_WIDTH, height, x1 - BOUNDARY_OFFSET, y2 / 2, 0);
        mBoundaryBody.createFixture(boundaryPolygon, 0.0f);

        // right wall
        boundaryPolygon.setAsBox(BOUNDARY_WIDTH, height, x2 + BOUNDARY_OFFSET, y2 / 2, 0);
        mBoundaryBody.createFixture(boundaryPolygon, 0.0f);

        boundaryPolygon.delete();

        bodyDef.setType(BodyType.dynamicBody);
        bodyDef.setPosition(x2 / 2, y2 / 2);

        mDynamicBody = mWorld.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setPosition(0, 0);
        circleShape.setRadius(radius);

        mDynamicBody.createFixture(circleShape, 0.5f);
        mDynamicBody.setSleepingAllowed(false);

        circleShape.delete();
        bodyDef.delete();

        ParticleGroupDef pgd = new ParticleGroupDef();
        pgd.setFlags(ParticleFlag.colorMixingParticle);
        pgd.setGroupFlags(ParticleGroupFlag.particleGroupCanBeEmpty);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, 1.0f, width / 2, 2.0f, 0);
        pgd.setShape(polygonShape);

        Vec2 velocity = new Vec2(0, 0);
        pgd.setLinearVelocity(velocity);
        velocity.delete();

        // This is the color that DebugDraw will use for particles
        ParticleColor color = new ParticleColor((short)0, (short)0, (short)128, (short)255);
        pgd.setColor(color);
        color.delete();

        mParticleSystem.createParticleGroup(pgd);

        Log.i(TAG, "particleCount [" + mParticleSystem.getParticleCount() + "]");
        Log.i(TAG, "particleGroupCount [" + mParticleSystem.getParticleGroupCount() + "]");

        polygonShape.delete();
        pgd.delete();
    }

    /**
     * Sets world gravity along x y axis.
     *
     * @param gravityX the gravity in x direction
     * @param gravityY the gravity in y direction
     */
    public void setWorldGravity(float gravityX, float gravityY) {
        if (mWorld != null) {
            mWorld.setGravity(gravityX, gravityY);
        }
    }

    /**
     * Returns the world object.
     *
     * @return world object
     */
    public World getWorld() {
        return mWorld;
    }

    /**
     * Returns the particle system object.
     *
     * @return particle system object
     */
    public ParticleSystem getParticleSystem() {
        return mParticleSystem;
    }

    /**
     * Returns the dynamic body object.
     *
     * @return dynamic body object
     */
    public Body getDynamicBody() {
        return mDynamicBody;
    }

    /**
     * Update components in physics engine.
     */
    public void update() {
        if (mWorld != null) {
            mWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
        }
    }

    /**
     * Cleanup resources.
     */
    public void cleanUp() {
        if (mBoundaryBody != null) {
            mBoundaryBody.delete();
            mBoundaryBody = null;
        }
        if (mDynamicBody != null) {
            mDynamicBody.delete();
            mDynamicBody = null;
        }
        if (mParticleSystem != null) {
            mParticleSystem.delete();
            mParticleSystem = null;
        }
        if (mWorld != null) {
            mWorld.delete();
            mWorld = null;
        }
    }
}
