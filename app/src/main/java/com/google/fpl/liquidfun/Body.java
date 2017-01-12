/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.google.fpl.liquidfun;

public class Body {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected Body(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Body obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        throw new UnsupportedOperationException("C++ destructor does not have public access");
      }
      swigCPtr = 0;
    }
  }

  public Fixture createFixture(FixtureDef def) {
    long cPtr = liquidfunJNI.Body_createFixture__SWIG_0(swigCPtr, this, FixtureDef.getCPtr(def), def);
    return (cPtr == 0) ? null : new Fixture(cPtr, false);
  }

  public Fixture createFixture(Shape shape, float density) {
    long cPtr = liquidfunJNI.Body_createFixture__SWIG_1(swigCPtr, this, Shape.getCPtr(shape), shape, density);
    return (cPtr == 0) ? null : new Fixture(cPtr, false);
  }

  public void destroyFixture(Fixture fixture) {
    liquidfunJNI.Body_destroyFixture(swigCPtr, this, Fixture.getCPtr(fixture), fixture);
  }

  public void setTransform(Vec2 position, float angle) {
    liquidfunJNI.Body_setTransform__SWIG_0(swigCPtr, this, Vec2.getCPtr(position), position, angle);
  }

  public Transform getTransform() {
    return new Transform(liquidfunJNI.Body_getTransform(swigCPtr, this), false);
  }

  public Vec2 getPosition() {
    return new Vec2(liquidfunJNI.Body_getPosition(swigCPtr, this), false);
  }

  public float getAngle() {
    return liquidfunJNI.Body_getAngle(swigCPtr, this);
  }

  public Vec2 getWorldCenter() {
    return new Vec2(liquidfunJNI.Body_getWorldCenter(swigCPtr, this), false);
  }

  public Vec2 getLocalCenter() {
    return new Vec2(liquidfunJNI.Body_getLocalCenter(swigCPtr, this), false);
  }

  public void setLinearVelocity(Vec2 v) {
    liquidfunJNI.Body_setLinearVelocity(swigCPtr, this, Vec2.getCPtr(v), v);
  }

  public Vec2 getLinearVelocity() {
    return new Vec2(liquidfunJNI.Body_getLinearVelocity(swigCPtr, this), false);
  }

  public void setAngularVelocity(float omega) {
    liquidfunJNI.Body_setAngularVelocity(swigCPtr, this, omega);
  }

  public float getAngularVelocity() {
    return liquidfunJNI.Body_getAngularVelocity(swigCPtr, this);
  }

  public void applyForce(Vec2 force, Vec2 point, boolean wake) {
    liquidfunJNI.Body_applyForce(swigCPtr, this, Vec2.getCPtr(force), force, Vec2.getCPtr(point), point, wake);
  }

  public void applyForceToCenter(Vec2 force, boolean wake) {
    liquidfunJNI.Body_applyForceToCenter(swigCPtr, this, Vec2.getCPtr(force), force, wake);
  }

  public void applyTorque(float torque, boolean wake) {
    liquidfunJNI.Body_applyTorque(swigCPtr, this, torque, wake);
  }

  public void applyLinearImpulse(Vec2 impulse, Vec2 point, boolean wake) {
    liquidfunJNI.Body_applyLinearImpulse(swigCPtr, this, Vec2.getCPtr(impulse), impulse, Vec2.getCPtr(point), point, wake);
  }

  public void applyAngularImpulse(float impulse, boolean wake) {
    liquidfunJNI.Body_applyAngularImpulse(swigCPtr, this, impulse, wake);
  }

  public float getMass() {
    return liquidfunJNI.Body_getMass(swigCPtr, this);
  }

  public float getInertia() {
    return liquidfunJNI.Body_getInertia(swigCPtr, this);
  }

  public void getMassData(MassData data) {
    liquidfunJNI.Body_getMassData(swigCPtr, this, MassData.getCPtr(data), data);
  }

  public void setMassData(MassData data) {
    liquidfunJNI.Body_setMassData(swigCPtr, this, MassData.getCPtr(data), data);
  }

  public void resetMassData() {
    liquidfunJNI.Body_resetMassData(swigCPtr, this);
  }

  public Vec2 getWorldPoint(Vec2 localPoint) {
    return new Vec2(liquidfunJNI.Body_getWorldPoint(swigCPtr, this, Vec2.getCPtr(localPoint), localPoint), true);
  }

  public Vec2 getWorldVector(Vec2 localVector) {
    return new Vec2(liquidfunJNI.Body_getWorldVector(swigCPtr, this, Vec2.getCPtr(localVector), localVector), true);
  }

  public Vec2 getLocalPoint(Vec2 worldPoint) {
    return new Vec2(liquidfunJNI.Body_getLocalPoint(swigCPtr, this, Vec2.getCPtr(worldPoint), worldPoint), true);
  }

  public Vec2 getLocalVector(Vec2 worldVector) {
    return new Vec2(liquidfunJNI.Body_getLocalVector(swigCPtr, this, Vec2.getCPtr(worldVector), worldVector), true);
  }

  public Vec2 getLinearVelocityFromWorldPoint(Vec2 worldPoint) {
    return new Vec2(liquidfunJNI.Body_getLinearVelocityFromWorldPoint(swigCPtr, this, Vec2.getCPtr(worldPoint), worldPoint), true);
  }

  public Vec2 getLinearVelocityFromLocalPoint(Vec2 localPoint) {
    return new Vec2(liquidfunJNI.Body_getLinearVelocityFromLocalPoint(swigCPtr, this, Vec2.getCPtr(localPoint), localPoint), true);
  }

  public float getLinearDamping() {
    return liquidfunJNI.Body_getLinearDamping(swigCPtr, this);
  }

  public void setLinearDamping(float linearDamping) {
    liquidfunJNI.Body_setLinearDamping(swigCPtr, this, linearDamping);
  }

  public float getAngularDamping() {
    return liquidfunJNI.Body_getAngularDamping(swigCPtr, this);
  }

  public void setAngularDamping(float angularDamping) {
    liquidfunJNI.Body_setAngularDamping(swigCPtr, this, angularDamping);
  }

  public float getGravityScale() {
    return liquidfunJNI.Body_getGravityScale(swigCPtr, this);
  }

  public void setGravityScale(float scale) {
    liquidfunJNI.Body_setGravityScale(swigCPtr, this, scale);
  }

  public void setType(BodyType type) {
    liquidfunJNI.Body_setType(swigCPtr, this, type.swigValue());
  }

  public BodyType getType() {
    return BodyType.swigToEnum(liquidfunJNI.Body_getType(swigCPtr, this));
  }

  public void setBullet(boolean flag) {
    liquidfunJNI.Body_setBullet(swigCPtr, this, flag);
  }

  public boolean isBullet() {
    return liquidfunJNI.Body_isBullet(swigCPtr, this);
  }

  public void setSleepingAllowed(boolean flag) {
    liquidfunJNI.Body_setSleepingAllowed(swigCPtr, this, flag);
  }

  public boolean isSleepingAllowed() {
    return liquidfunJNI.Body_isSleepingAllowed(swigCPtr, this);
  }

  public void setAwake(boolean flag) {
    liquidfunJNI.Body_setAwake(swigCPtr, this, flag);
  }

  public boolean isAwake() {
    return liquidfunJNI.Body_isAwake(swigCPtr, this);
  }

  public void setActive(boolean flag) {
    liquidfunJNI.Body_setActive(swigCPtr, this, flag);
  }

  public boolean isActive() {
    return liquidfunJNI.Body_isActive(swigCPtr, this);
  }

  public void setFixedRotation(boolean flag) {
    liquidfunJNI.Body_setFixedRotation(swigCPtr, this, flag);
  }

  public boolean isFixedRotation() {
    return liquidfunJNI.Body_isFixedRotation(swigCPtr, this);
  }

  public Fixture getFixtureList() {
    long cPtr = liquidfunJNI.Body_getFixtureList__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new Fixture(cPtr, false);
  }

  public SWIGTYPE_p_b2JointEdge getJointList() {
    long cPtr = liquidfunJNI.Body_getJointList__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_b2JointEdge(cPtr, false);
  }

  public SWIGTYPE_p_b2ContactEdge getContactList() {
    long cPtr = liquidfunJNI.Body_getContactList__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_b2ContactEdge(cPtr, false);
  }

  public Body getNext() {
    long cPtr = liquidfunJNI.Body_getNext__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new Body(cPtr, false);
  }

  public World getWorld() {
    long cPtr = liquidfunJNI.Body_getWorld__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new World(cPtr, false);
  }

  public void dump() {
    liquidfunJNI.Body_dump(swigCPtr, this);
  }

  public float getPositionX() {
    return liquidfunJNI.Body_getPositionX(swigCPtr, this);
  }

  public float getPositionY() {
    return liquidfunJNI.Body_getPositionY(swigCPtr, this);
  }

  public void setTransform(float positionX, float positionY, float angle) {
    liquidfunJNI.Body_setTransform__SWIG_1(swigCPtr, this, positionX, positionY, angle);
  }

}
