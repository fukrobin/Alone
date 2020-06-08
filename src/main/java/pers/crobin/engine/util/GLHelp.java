package pers.crobin.engine.util;/**
 * Created by Administrator on 2020/4/5.
 */

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import pers.crobin.engine.renderer.ShaderProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * @Author CRobin
 * @Date 2020/4/5 00:54
 * @Description $
 **/
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming") public class GLHelp {
    public static final int VERTEX_SHADER   = GL_VERTEX_SHADER;
    public static final int FRAGMENT_SHADER = GL_FRAGMENT_SHADER;

    private static final LinkedList<Integer>       GL_BUFFERS           = new LinkedList<>();
    private static final HashMap<String, Integer>  TEXTURE_MAP          = new HashMap<>();
    private static final LinkedList<ShaderProgram> SHADER_PROGRAM_LIST  = new LinkedList<>();
    private static       ShaderProgram             currentShaderProgram = null;

    private static final Vector4f CLEAR_COLOR = new Vector4f(0.1f, 0.1f, 0.1f, 1.0f);

    public static void deleteTexture(int id) {
        GL11.glDeleteTextures(id);
    }

    public static void setGLState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_STENCIL_TEST);

        glEnable(GL_CULL_FACE);
    }

    /**
     * <p>统一分配GL缓冲，以便于统一释放内存，所有被分配的缓冲都会加入链表
     * 并在程序结束时统一释放，所以请不要自行释放内存。</p>
     */
    public static int getGLBuffer() {
        int id = glGenBuffers();
        GL_BUFFERS.add(id);
        return id;
    }

    /**
     * 生成新的着色器程序类，此方法存在的唯一目的仅仅是为了能够统一释放着色器程序所占用的内存
     *
     * @return 着色器程序类
     */
    public static ShaderProgram genShaderProgram() {
        ShaderProgram program = new ShaderProgram();
        SHADER_PROGRAM_LIST.add(program);
        currentShaderProgram = program;

        return program;
    }

    public static void createShader(int shaderType, String renderer, String shaderName) throws IOException {
        if (shaderType == VERTEX_SHADER) {
            currentShaderProgram.createVertexShader(Utils.loadResource("/shader/" + renderer + "/vs_" + shaderName + ".glsl"));
        } else if (shaderType == FRAGMENT_SHADER) {
            currentShaderProgram.createFragmentShader(Utils.loadResource("/shader/" + renderer + "/fs_" + shaderName + ".glsl"));
        }
    }

    public static ShaderProgram setupShaderProgram(String dir, String shaderName, String... uniforms) throws IOException {
        ShaderProgram program = genShaderProgram();
        createShader(VERTEX_SHADER, dir, shaderName);
        createShader(FRAGMENT_SHADER, dir, shaderName);
        program.link();

        for (String uniform : uniforms) {
            program.createUniform(uniform);
        }

        return program;
    }

    /**
     * 加载纹理，每一个纹理成功加载后都会被存入一个Map中，
     * 一个纹理如果已经加载将直接返回ID。
     *
     * @param texturePath 纹理资源路径
     * @return 纹理ID
     * @throws IOException 如果纹理路径错误或是其它原因导致无法打开文件，将会抛出异常
     */
    public static int loadTexture(String texturePath) throws IOException {
        if (TEXTURE_MAP.containsKey(texturePath)) {
            return TEXTURE_MAP.get(texturePath);
        }

        int        textureId;
        ByteBuffer buf;
        // Load Texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w        = stack.mallocInt(1);
            IntBuffer h        = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(texturePath, w, h, channels, 4);
            if (buf == null) {
                throw new IOException("Image file [" + texturePath + "] not loaded: " + stbi_failure_reason());
            }

            textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);

            glBindTexture(GL_TEXTURE_2D, 0);
        }

        stbi_image_free(buf);

        TEXTURE_MAP.put(texturePath, textureId);
        return textureId;
    }

    public static int loadNullTexture(int width, int height) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, MemoryUtil.NULL);

        return textureId;
    }

    public static void setClearColor(Vector4f color) {
        CLEAR_COLOR.set(color);
    }

    public static void frameBufferClearColor() {
        glClearColor(CLEAR_COLOR.x, CLEAR_COLOR.y, CLEAR_COLOR.z, CLEAR_COLOR.w);
    }


    public void cleanUp() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int id : GL_BUFFERS) {
            glDeleteBuffers(id);
        }

        for (Integer id : TEXTURE_MAP.values()) {
            glDeleteTextures(id);
        }

        for (ShaderProgram program : SHADER_PROGRAM_LIST) {
            program.cleanup();
        }
    }
}
