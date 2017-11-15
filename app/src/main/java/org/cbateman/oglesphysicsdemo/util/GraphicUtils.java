package org.cbateman.oglesphysicsdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.InputStream;

import org.cbateman.oglesphysicsdemo.Constants;

/**
 * Helper methods for setting up and rendering graphics.
 */
@SuppressWarnings("WeakerAccess")
public class GraphicUtils {
    private static final String TAG = Constants.TAG;

    private GraphicUtils() {}

    /**
     * Load texture from stream.
     *
     * @param is object streaming texture
     * @return texture id
     */
    public static int loadTexture(InputStream is) {
        return loadTexture(BitmapFactory.decodeStream(is));
    }

    /**
     * Load texture from bitmap.
     *
     * @param bitmap the Bitmap object
     * @return texture id
     */
    public static int loadTexture(Bitmap bitmap) {
        int[] textureId = new int[1];

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        int format = GLUtils.getInternalFormat(bitmap);
        int type = GLUtils.getType(bitmap);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, format, bitmap, type, 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return textureId[0];
    }

    /**
     * Loads vertex and fragment shaders, creates program object, links program, returns
     * program id.
     *
     * @param vShaderCode vertex shader code as string
     * @param fShaderCode fragment shader as string
     * @return program id of created program; otherwise 0 if error occurs
     */
    public static int loadProgram(String vShaderCode, String fShaderCode) {
        int program;
        int vertexShader;
        int fragmentShader;
        int[] params = new int[1];

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vShaderCode);
        if (vertexShader == 0)
            return 0;

        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderCode);
        if (fragmentShader == 0) {
            GLES20.glDeleteShader(vertexShader);
            return 0;
        }

        program = GLES20.glCreateProgram();

        if (program == 0) {
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);
            return 0;
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);

        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, params, 0);

        if (params[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);
            GLES20.glDeleteProgram(program);
            return 0;
        }

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return program;
    }

    /**
     * Create, load and compile a shader of type.
     * @param type the type of shader
     * @param shaderCode the shader code
     * @return shader id of created shader; otherwise 0 if error occurs
     */
    private static int loadShader(int type, String shaderCode) {
        int shader;
        int[] params = new int[1];

        shader = GLES20.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);

        if (params[0] == 0) {
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }
}
