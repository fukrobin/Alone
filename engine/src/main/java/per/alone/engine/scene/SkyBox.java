package per.alone.engine.scene;
/**
 * Created by Administrator on 2020/4/5.
 */

import per.alone.engine.util.GLHelp;
import per.alone.engine.util.Utils;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author fkobin
 * @date 2020/4/5 00:21
 * @Description
 **/
public class SkyBox {
    private static final float[]  SKYBOX_VERTICES =
            {-20.0f, 20.0f, -20.0f, -20.0f, -20.0f, -20.0f, 20.0f, -20.0f, -20.0f, 20.0f, -20.0f, -20.0f, 20.0f, 20.0f, -20.0f, -20.0f,
                    20.0f, -20.0f,

                    -20.0f, -20.0f, 20.0f, -20.0f, -20.0f, -20.0f, -20.0f, 20.0f, -20.0f, -20.0f, 20.0f, -20.0f, -20.0f, 20.0f, 20.0f, -20.0f,
                    -20.0f, 20.0f,

                    20.0f, -20.0f, -20.0f, 20.0f, -20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, -20.0f, 20.0f, -20.0f,
                    -20.0f,

                    -20.0f, -20.0f, 20.0f, -20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, -20.0f, 20.0f, -20.0f, -20.0f,
                    20.0f,

                    -20.0f, 20.0f, -20.0f, 20.0f, 20.0f, -20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, -20.0f, 20.0f, 20.0f, -20.0f, 20.0f,
                    -20.0f,

                    -20.0f, -20.0f, -20.0f, -20.0f, -20.0f, 20.0f, 20.0f, -20.0f, -20.0f, 20.0f, -20.0f, -20.0f, -20.0f, -20.0f, 20.0f, 20.0f,
                    -20.0f, 20.0f};

    private final        String[] cubeTexturePath;

    private              int      cubeTexture     = -1;

    private              int      vao;

    public SkyBox(String[] cubeTexturePath) {
        this.cubeTexturePath = cubeTexturePath;
    }

    private void setupVao() {
        vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, SKYBOX_VERTICES, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void init() throws IOException {
        loadCubeTexture();
        setupVao();
    }

    private void loadCubeTexture() throws IOException {
        cubeTexture = Utils.loadCubeTexture(cubeTexturePath);
    }

    public int getVao() {
        return vao;
    }

    public int getCubeTexture() {
        return cubeTexture;
    }

    public void cleanUp() {
        GLHelp.deleteTexture(cubeTexture);
    }
}
