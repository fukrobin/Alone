package per.alone.engine.scene;

import org.lwjgl.opengl.GL15;
import per.alone.engine.global.GlobalVariable;
import per.alone.engine.util.GLHelp;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * 实例化Mesh
 *
 * @date 2020/4/10 12:43
 **/
public class InstancedMesh extends Mesh {
    private int instancedBuffer;

    @Override
    protected void setupMesh(float[] positions, float[] texCoords, float[] normals, int[] indices) {
        vao = glGenVertexArrays();
        int vbo = GLHelp.getGLBuffer();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, (positions.length + normals.length + texCoords.length) * 4L, GL_STATIC_DRAW);

        glBufferSubData(GL_ARRAY_BUFFER, 0, positions);
        glBufferSubData(GL_ARRAY_BUFFER, positions.length * 4L, normals);
        glBufferSubData(GL_ARRAY_BUFFER, (positions.length + normals.length) * 4L, texCoords);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * 4, positions.length * 4L);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, (positions.length + normals.length) * 4L);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        // 设置实例顶点属性
        instancedBuffer = GLHelp.getGLBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, instancedBuffer);
        GL15.glBufferData(GL_ARRAY_BUFFER, GlobalVariable.INSTANCE_BUFFER_SIZE, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(3, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3, 1);

        // 设置EBO
        int ebo = GLHelp.getGLBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        vertexCount = indices.length;

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void draw(int count) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);

        glBindVertexArray(vao);
        glDrawElementsInstanced(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0, count);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getInstancedBuffer() {
        return instancedBuffer;
    }
}
