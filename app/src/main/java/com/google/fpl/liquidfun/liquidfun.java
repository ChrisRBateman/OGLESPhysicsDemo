/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.google.fpl.liquidfun;

public class liquidfun implements liquidfunConstants {
  public static void setB2ParticleColor_zero(ParticleColor value) {
    liquidfunJNI.b2ParticleColor_zero_set(ParticleColor.getCPtr(value), value);
  }

  public static ParticleColor getB2ParticleColor_zero() {
    return new ParticleColor(liquidfunJNI.b2ParticleColor_zero_get(), false);
  }

  public static int b2CalculateParticleIterations(float gravity, float radius, float timeStep) {
    return liquidfunJNI.b2CalculateParticleIterations(gravity, radius, timeStep);
  }

}
