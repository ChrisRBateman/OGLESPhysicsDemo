package org.cbateman.oglesphysicsdemo.sprites;

import android.content.Context;
import android.os.Build;

/**
 * Factory class that returns a WaterImage object.
 */
public class WaterImageFactory {
    public static WaterImage getWaterImage(Context context) {
        // There seems to be an issue with some older android devices where using frame buffer
        // objects can cause the device to lockup or reboot. My Samsung Galaxy S (with Android
        // 2.3.3) has this issue so I wrote two implementations to render water particles.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Return the FBO version for Android 4.1 or higher.
            return new WaterImageFBOImpl(context);
        }
        return new WaterImageImpl(context);
    }
}
