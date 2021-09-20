package per.alone.engine.scene;

import per.alone.engine.util.GLHelp;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/4/19 01:05
 **/
public class Mesh {
    protected int     vertexCount;

    protected int     vao       = -1;

    protected int     textureId = -1;

    private   boolean useIndices;

    public Mesh() {
    }

    protected void setupMesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        vao = glGenVertexArrays();
        int vbo = GLHelp.getGLBuffer();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (positions.length + normals.length + texCoords.length) * 4, GL_STATIC_DRAW);

        glBufferSubData(GL_ARRAY_BUFFER, 0, positions);
        glBufferSubData(GL_ARRAY_BUFFER, positions.length * 4, normals);
        glBufferSubData(GL_ARRAY_BUFFER, (positions.length + normals.length) * 4, texCoords);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * 4, positions.length * 4);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, (positions.length + normals.length) * 4);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        if (indices.length > 0) {
            useIndices  = true;
            vertexCount = indices.length;
            int ebo = GLHelp.getGLBuffer();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        } else {
            vertexCount = positions.length / 3;
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void setupMesh2D(float[] positions, float[] texCoords) {
        useIndices  = false;
        vertexCount = positions.length >> 1;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = GLHelp.getGLBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (positions.length + texCoords.length) * 4, GL_STATIC_DRAW);

        glBufferSubData(GL_ARRAY_BUFFER, 0, positions);
        glBufferSubData(GL_ARRAY_BUFFER, positions.length * 4, texCoords);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * 4, positions.length * 4);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindVertexArray(0);
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public void draw() {
        if (this.textureId != -1) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
        }

        glBindVertexArray(vao);
        if (useIndices) {
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        }
        glBindVertexArray(0);
    }
}
