/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.google.fpl.liquidfun;

public class BodyDef {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected BodyDef(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(BodyDef obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        liquidfunJNI.delete_BodyDef(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public BodyDef() {
    this(liquidfunJNI.new_BodyDef(), true);
  }

  public void setPosition(float positionX, float positionY) {
    liquidfunJNI.BodyDef_setPosition(swigCPtr, this, positionX, positionY);
  }

  public void setType(BodyType value) {
    liquidfunJNI.BodyDef_type_set(swigCPtr, this, value.swigValue());
  }

  public BodyType getType() {
    return BodyType.swigToEnum(liquidfunJNI.BodyDef_type_get(swigCPtr, this));
  }

  public void setPosition(Vec2 value) {
    liquidfunJNI.BodyDef_position_set(swigCPtr, this, Vec2.getCPtr(value), value);
  }

  public Vec2 getPosition() {
    return new Vec2(liquidfunJNI.BodyDef_position_get(swigCPtr, this), false);
  }

  public void setAngle(float value) {
    liquidfunJNI.BodyDef_angle_set(swigCPtr, this, value);
  }

  public float getAngle() {
    return liquidfunJNI.BodyDef_angle_get(swigCPtr, this);
  }

  public void setLinearVelocity(Vec2 value) {
    liquidfunJNI.BodyDef_linearVelocity_set(swigCPtr, this, Vec2.getCPtr(value), value);
  }

  public Vec2 getLinearVelocity() {
    return new Vec2(liquidfunJNI.BodyDef_linearVelocity_get(swigCPtr, this), false);
  }

  public void setAngularVelocity(float value) {
    liquidfunJNI.BodyDef_angularVelocity_set(swigCPtr, this, value);
  }

  public float getAngularVelocity() {
    return liquidfunJNI.BodyDef_angularVelocity_get(swigCPtr, this);
  }

  public void setLinearDamping(float value) {
    liquidfunJNI.BodyDef_linearDamping_set(swigCPtr, this, value);
  }

  public float getLinearDamping() {
    return liquidfunJNI.BodyDef_linearDamping_get(swigCPtr, this);
  }

  public void setAngularDamping(float value) {
    liquidfunJNI.BodyDef_angularDamping_set(swigCPtr, this, value);
  }

  public float getAngularDamping() {
    return liquidfunJNI.BodyDef_angularDamping_get(swigCPtr, this);
  }

  public void setAllowSleep(boolean value) {
    liquidfunJNI.BodyDef_allowSleep_set(swigCPtr, this, value);
  }

  public boolean getAllowSleep() {
    return liquidfunJNI.BodyDef_allowSleep_get(swigCPtr, this);
  }

  public void setAwake(boolean value) {
    liquidfunJNI.BodyDef_awake_set(swigCPtr, this, value);
  }

  public boolean getAwake() {
    return liquidfunJNI.BodyDef_awake_get(swigCPtr, this);
  }

  public void setFixedRotation(boolean value) {
    liquidfunJNI.BodyDef_fixedRotation_set(swigCPtr, this, value);
  }

  public boolean getFixedRotation() {
    return liquidfunJNI.BodyDef_fixedRotation_get(swigCPtr, this);
  }

  public void setBullet(boolean value) {
    liquidfunJNI.BodyDef_bullet_set(swigCPtr, this, value);
  }

  public boolean getBullet() {
    return liquidfunJNI.BodyDef_bullet_get(swigCPtr, this);
  }

  public void setActive(boolean value) {
    liquidfunJNI.BodyDef_active_set(swigCPtr, this, value);
  }

  public boolean getActive() {
    return liquidfunJNI.BodyDef_active_get(swigCPtr, this);
  }

  public void setGravityScale(float value) {
    liquidfunJNI.BodyDef_gravityScale_set(swigCPtr, this, value);
  }

  public float getGravityScale() {
    return liquidfunJNI.BodyDef_gravityScale_get(swigCPtr, this);
  }

}
