/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.google.fpl.liquidfun;

public class ParticleTriad {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ParticleTriad(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ParticleTriad obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        liquidfunJNI.delete_ParticleTriad(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setIndexA(int value) {
    liquidfunJNI.ParticleTriad_indexA_set(swigCPtr, this, value);
  }

  public int getIndexA() {
    return liquidfunJNI.ParticleTriad_indexA_get(swigCPtr, this);
  }

  public void setIndexB(int value) {
    liquidfunJNI.ParticleTriad_indexB_set(swigCPtr, this, value);
  }

  public int getIndexB() {
    return liquidfunJNI.ParticleTriad_indexB_get(swigCPtr, this);
  }

  public void setIndexC(int value) {
    liquidfunJNI.ParticleTriad_indexC_set(swigCPtr, this, value);
  }

  public int getIndexC() {
    return liquidfunJNI.ParticleTriad_indexC_get(swigCPtr, this);
  }

  public void setFlags(long value) {
    liquidfunJNI.ParticleTriad_flags_set(swigCPtr, this, value);
  }

  public long getFlags() {
    return liquidfunJNI.ParticleTriad_flags_get(swigCPtr, this);
  }

  public void setStrength(float value) {
    liquidfunJNI.ParticleTriad_strength_set(swigCPtr, this, value);
  }

  public float getStrength() {
    return liquidfunJNI.ParticleTriad_strength_get(swigCPtr, this);
  }

  public void setPa(Vec2 value) {
    liquidfunJNI.ParticleTriad_pa_set(swigCPtr, this, Vec2.getCPtr(value), value);
  }

  public Vec2 getPa() {
    return new Vec2(liquidfunJNI.ParticleTriad_pa_get(swigCPtr, this), false);
  }

  public void setPb(Vec2 value) {
    liquidfunJNI.ParticleTriad_pb_set(swigCPtr, this, Vec2.getCPtr(value), value);
  }

  public Vec2 getPb() {
    return new Vec2(liquidfunJNI.ParticleTriad_pb_get(swigCPtr, this), false);
  }

  public void setPc(Vec2 value) {
    liquidfunJNI.ParticleTriad_pc_set(swigCPtr, this, Vec2.getCPtr(value), value);
  }

  public Vec2 getPc() {
    return new Vec2(liquidfunJNI.ParticleTriad_pc_get(swigCPtr, this), false);
  }

  public void setKa(float value) {
    liquidfunJNI.ParticleTriad_ka_set(swigCPtr, this, value);
  }

  public float getKa() {
    return liquidfunJNI.ParticleTriad_ka_get(swigCPtr, this);
  }

  public void setKb(float value) {
    liquidfunJNI.ParticleTriad_kb_set(swigCPtr, this, value);
  }

  public float getKb() {
    return liquidfunJNI.ParticleTriad_kb_get(swigCPtr, this);
  }

  public void setKc(float value) {
    liquidfunJNI.ParticleTriad_kc_set(swigCPtr, this, value);
  }

  public float getKc() {
    return liquidfunJNI.ParticleTriad_kc_get(swigCPtr, this);
  }

  public void setS(float value) {
    liquidfunJNI.ParticleTriad_s_set(swigCPtr, this, value);
  }

  public float getS() {
    return liquidfunJNI.ParticleTriad_s_get(swigCPtr, this);
  }

  public ParticleTriad() {
    this(liquidfunJNI.new_ParticleTriad(), true);
  }

}
