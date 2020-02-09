package net.appseed.egl01;


import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class JavaRenderer implements GLSurfaceView.Renderer  {

   // private final FloatBuffer mTriangle1Vertices;
    private  FloatBuffer mVertexBuffer;
    private  FloatBuffer mColorBuffer;

    private  int muMVPMatrixHandler;
    private  int programHandle;
    private  int mPosHandler;
    private  int mColorHandler;

    private float[] frustumM = new float[16];
    private float[] lookAtM = new float[16];
    private float[] mMVPMatrix = new float[16];

    public JavaRenderer(){


        float[] triangleCoords = new float[] {
                0.0f, 1, 0.0f,
                -1.0f, -1, 0.0f,
                1.0f, -1, 0.0f
        };
        mVertexBuffer = floatArray2FloatBuffer(triangleCoords);

        float[] color = new float[] {
                1, 0, 0, 0,
                0, 0, 1, 0,
                0, 1, 0, 0
        };
        mColorBuffer = floatArray2FloatBuffer(color);
/*
        final float[] vertexPoints = new float[]{
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        //分配内存空间,每个浮点型占4字节空间
        mTriangle1Vertices = ByteBuffer.allocateDirect(vertexPoints.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //传入指定的坐标数据
        mTriangle1Vertices.put(vertexPoints);
        mTriangle1Vertices.position(0);
*/
    }

    public static FloatBuffer floatArray2FloatBuffer(float[] array) {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(array).position(0);
        return floatBuffer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
       // GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        final String vertexShader =
                "#version 300 es \n"
                        + "uniform mat4 uMVPMatrix;\n"
                        + "layout (location = 0) in vec4 aPosition;\n"
                        + "layout (location = 1) in vec4 aColor;\n"
                        + "out vec4 vColor;\n"
                        + "void main() { \n"
                        + "gl_Position = uMVPMatrix * aPosition;\n"
                        + "vColor = aColor;\n"
                        + "}\n";

        final String fragmentShader =
                "#version 300 es \n" +
                "precision mediump float;\n"
                + "in vec4 vColor;\n"
                + "out vec4 fragColor;\n"
                + "void main() { \n"
                + "fragColor = vColor;\n"
                + "}\n";



        // Load in the vertex shader.
        int vertexShaderHandle = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES30.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES30.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(vertexShaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES30.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES30.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES30.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(fragmentShaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES30.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0)
        {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        programHandle = GLES30.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES30.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES30.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
        //    GLES30.glBindAttribLocation(programHandle, 0, "a_Position");
         //   GLES30.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES30.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES30.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.
   //     mMVPMatrixHandle = GLES30.glGetUniformLocation(programHandle, "u_MVPMatrix");
   //     mPositionHandle = GLES30.glGetAttribLocation(programHandle, "a_Position");
    //    mColorHandle = GLES30.glGetAttribLocation(programHandle, "a_Color");

        // Tell OpenGL to use this program when rendering.
      //  GLES30.glUseProgram(programHandle);


        muMVPMatrixHandler = GLES30.glGetUniformLocation(programHandle, "uMVPMatrix");
        mPosHandler = GLES30.glGetAttribLocation(programHandle, "aPosition");
        mColorHandler = GLES30.glGetAttribLocation(programHandle, "aColor");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
       // GLES30.glViewport(0,0,width,height);

        float ratio = (float) height / width;
        Matrix.frustumM(frustumM, 0, -1, 1, -ratio, ratio, 3, 7);
        Matrix.setLookAtM(lookAtM, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, frustumM, 0, lookAtM, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(programHandle);
        GLES30.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, mMVPMatrix, 0);
        GLES30.glVertexAttribPointer(mPosHandler, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES30.glEnableVertexAttribArray(mPosHandler);
        GLES30.glVertexAttribPointer(mColorHandler, 4, GLES30.GL_FLOAT, false, 4 * 4, mColorBuffer);
        GLES30.glEnableVertexAttribArray(mColorHandler);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }

    /**
     * 链接小程序
     *
     * @param vertexShaderId 顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId);
            //链接着色器程序
            GLES30.glLinkProgram(programId);
            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String logInfo = GLES30.glGetProgramInfoLog(programId);
                System.err.println(logInfo);
                GLES30.glDeleteProgram(programId);
                return 0;
            }
            return programId;
        } else {
            //创建失败
            return 0;
        }
    }
}
