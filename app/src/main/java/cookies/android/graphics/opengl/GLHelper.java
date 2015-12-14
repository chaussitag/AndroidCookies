package cookies.android.graphics.opengl;

import android.opengl.GLES20;
import android.util.Log;

public class GLHelper {
    private static final String TAG = GLHelper.class.getSimpleName();

    public static final int BYTE_SIZE_IN_BYTES = 1;
    public static final int FLOAT_SIZE_IN_BYTES = 4;
    public static final int INT_SIZE_IN_BYTES = 4;

    public static int loadShader(int shaderType, String shaderSource) {
        boolean[] supportShaderCompiler = new boolean[1];
        GLES20.glGetBooleanv(GLES20.GL_SHADER_COMPILER, supportShaderCompiler, 0);
        if (!supportShaderCompiler[0]) {
            throw new RuntimeException("Shader compiler is unavailable on the OpenGL ES implementation of your device");
        }

        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, shaderSource);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "failed to compile shader " + shaderType + ": ");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int createProgram(String vertexShaderSource, String fragmentShaderSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
        if (fragmentShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGLError("glAttachShader");

            GLES20.glAttachShader(program, fragmentShader);
            checkGLError("glAttachShader");

            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                Log.e(TAG, "failed to link program:");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public static void checkGLError(String glCommand) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glCommand + ": glError " + error);
            throw new RuntimeException(glCommand + ": glError " + error);
        }
    }

    public static void logActiveAttribInfo(int program) {
        if (!GLES20.glIsProgram(program)) {
            Log.d(TAG, program + " is not a valid program");
        }

        int[] activeAttribs = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_ATTRIBUTES, activeAttribs, 0);

        int[] maxAttribLen = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, maxAttribLen, 0);

        Log.d(TAG, "number of active attribs: " + activeAttribs[0]);
        Log.d(TAG, "max attrib length: " + maxAttribLen[0]);

        byte[] name = new byte[maxAttribLen[0]];
        int[] sizeAndType = new int[2];
        int[] length = new int[1];
        for (int index = 0; index < activeAttribs[0]; ++index) {
            GLES20.glGetActiveAttrib(program, index, name.length, length, 0,
                    sizeAndType, 0, sizeAndType, 1, name, 0);
            Log.d(TAG, "name: " + String.valueOf(name));
            Log.d(TAG, "size: " + sizeAndType[0]);
            Log.d(TAG, "type: " + gles20TypeToString(sizeAndType[1]));
        }
    }

    public static void logActiveUniformInfo(int program) {
        if (!GLES20.glIsProgram(program)) {
            Log.d(TAG, program + " is not a valid program");
            return;
        }

        int[] activeUniforms = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_UNIFORMS, activeUniforms, 0);

        int[] maxUniformLen = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxUniformLen, 0);

        Log.d(TAG, "number of active uniforms: " + activeUniforms[0]);
        Log.d(TAG, "max uniform length: " + maxUniformLen[0]);

        byte[] name = new byte[maxUniformLen[0]];
        int[] sizeAndType = new int[2];
        int[] length = new int[1];
        for (int index = 0; index < activeUniforms[0]; ++index) {
            GLES20.glGetActiveUniform(program, index, name.length, length, 0,
                    sizeAndType, 0, sizeAndType, 1, name, 0);
            Log.d(TAG, "name: " + String.valueOf(name));
            Log.d(TAG, "size: " + sizeAndType[0]);
            Log.d(TAG, "type: " + gles20TypeToString(sizeAndType[1]));
        }

    }

    public static String gles20TypeToString(int gles20Type) {
        String typeString = "unknown";
        switch (gles20Type) {
        case GLES20.GL_FLOAT:
            typeString = "GL_FLOAT";
            break;
        case GLES20.GL_FLOAT_VEC2:
            typeString = "GL_FLOAT_VEC2";
            break;
        case GLES20.GL_FLOAT_VEC3:
            typeString = "GL_FLOAT_VEC3";
            break;
        case GLES20.GL_FLOAT_VEC4:
            typeString = "GL_FLOAT_VEC4";
            break;
        case GLES20.GL_INT:
            typeString = "GL_INT";
            break;
        case GLES20.GL_INT_VEC2:
            typeString = "GL_INT_VEC2";
            break;
        case GLES20.GL_INT_VEC3:
            typeString = "GL_INT_VEC3";
            break;
        case GLES20.GL_INT_VEC4:
            typeString = "GL_INT_VEC4";
            break;
        case GLES20.GL_BOOL:
            typeString = "GL_BOOL";
            break;
        case GLES20.GL_BOOL_VEC2:
            typeString = "GL_BOOL_VEC2";
            break;
        case GLES20.GL_BOOL_VEC3:
            typeString = "GL_BOOL_VEC3";
            break;
        case GLES20.GL_BOOL_VEC4:
            typeString = "GL_BOOL_VEC4";
            break;
        case GLES20.GL_FLOAT_MAT2:
            typeString = "GL_FLOAT_MAT2";
            break;
        case GLES20.GL_FLOAT_MAT3:
            typeString = "GL_FLOAT_MAT3";
            break;
        case GLES20.GL_FLOAT_MAT4:
            typeString = "GL_FLOAT_MAT4";
            break;
        case GLES20.GL_SAMPLER_2D:
            typeString = "GL_SAMPLER_2D";
            break;
        case GLES20.GL_SAMPLER_CUBE:
            typeString = "GL_SAMPLER_CUBE";
            break;
        default:
            break;
        }
        return typeString;
    }
}
