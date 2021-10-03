package per.alone.engine.renderer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL31;
import per.alone.engine.Ordered;
import per.alone.engine.annotation.Order;
import per.alone.engine.core.DebugInfo;
import per.alone.engine.core.EngineContext;
import per.alone.engine.global.GlobalVariable;
import per.alone.engine.scene.*;
import per.alone.engine.scene.voxel.Agent;
import per.alone.engine.scene.voxel.Chunk;
import per.alone.engine.scene.voxel.VoxelBehavior;
import per.alone.engine.util.GLHelp;
import per.alone.engine.util.Utils;
import per.alone.stage.Window;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * Created by Administrator on 2020/4/5.
 *
 * @author fkobin
 * @date 2020/4/5 01:12
 **/
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SceneRenderer implements RendererComponent {
    private static final Matrix4f TEMP = new Matrix4f().translate(0, -5, -5);

    private final Agent agent;

    private final VoxelScene voxelScene;

    /**
     * 使用帧缓冲渲染时，渲染到屏幕需要使用的着色器
     */
    private ShaderProgram screenShaderProgram;

    private ShaderProgram skyBoxShaderProgram;

    private ShaderProgram voxelShaderProgram;

    private ShaderProgram modelShaderProgram;

    private int uniformBufferObjectMatrices;

    private boolean useFrameBuffer = true;

    private int frameBuffer = -1;

    private int renderBuffer;

    private Mesh screenQuad;

    private InstancedMesh instancedMesh;

    private Window window;

    private int colorAttachment;

    private int preWidth, preHeight;

    public SceneRenderer() {
        agent      = new Agent();
        voxelScene = new VoxelScene();
    }

    public VoxelScene getScene() {
        return voxelScene;
    }

    /**
     * 初始化
     * todo
     */
    public void initialize() {
        try {
            setupSkyBoxShader();
            setupVoxelShader();
            setupModelShader();
            setupScreenShader();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error! The necessary shader files for the per.fkrobin.engine have been lost.");
        }

        setupUniformObject();

        screenQuad = new Mesh();
        screenQuad.setupMesh2D(new float[]{-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                                       -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,},
                               new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                                       0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f});

        Model model = new Model("asserts/models/standardBlock.obj", "asserts/models/");
        model.loadInstanceModel();
        instancedMesh = (InstancedMesh) model.getMeshList().get(0);

        // setupFrameBuffer();
    }

    private void setupUniformObject() {
        // 绑定点0已被NanoVG使用
        skyBoxShaderProgram.bindUniformBlock("Matrices", 1);
        modelShaderProgram.bindUniformBlock("Matrices", 1);
        voxelShaderProgram.bindUniformBlock("Matrices", 1);
        uniformBufferObjectMatrices = GLHelp.getGLBuffer();
        glBindBuffer(GL31.GL_UNIFORM_BUFFER, uniformBufferObjectMatrices);
        glBufferData(GL31.GL_UNIFORM_BUFFER, 2 * 16 * 4, GL_STATIC_DRAW);
        glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);

        glBindBufferRange(GL31.GL_UNIFORM_BUFFER, 1, uniformBufferObjectMatrices, 0, 2 * 16 * 4);
    }

    private void setupSkyBoxShader() throws IOException {
        String[] uniforms = new String[]{"skybox"};
        skyBoxShaderProgram = GLHelp.setupShaderProgram("scene", "skybox", uniforms);
    }

    private void setupVoxelShader() throws IOException {
        String[] uniforms = new String[]{"Texture"};
        voxelShaderProgram = GLHelp.setupShaderProgram("scene", "instancedMesh", uniforms);
    }

    private void setupModelShader() throws IOException {
        String[] uniforms = new String[]{"modelMtx", "modelTexture"};
        modelShaderProgram = GLHelp.setupShaderProgram("scene", "model", uniforms);
    }

    private void setupScreenShader() throws IOException {
        screenShaderProgram = GLHelp.setupShaderProgram("scene", "renderScreen", "screenTexture");
    }

    /**
     * 设置帧缓冲
     */
    private void setupFrameBuffer() {
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        colorAttachment = GLHelp.loadNullTexture(window.getWidth(), window.getHeight());
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment, 0);

        // 使用渲染缓冲对象作为深度、模板缓冲附件
        renderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, window.getWidth(), window.getHeight());
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Utils.errLog("SceneRender: setupFrameBuffer()", "FrameBuffer setup failed.");
            useFrameBuffer = false;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * 渲染天空盒
     *
     * @param voxelScene 渲染的场景
     */
    private void renderSkyBox(VoxelScene voxelScene) {
        SkyBox skyBox = voxelScene.getSkyBox();
        if (skyBox != null) {
            glDepthFunc(GL_LEQUAL);
            skyBoxShaderProgram.bind();

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getCubeTexture());
            skyBoxShaderProgram.setUniform("skybox", 0);

            glBindVertexArray(skyBox.getVao());
            glDrawArrays(GL_TRIANGLES, 0, 36);
            glBindVertexArray(0);
            skyBoxShaderProgram.unbind();

            glDepthFunc(GL_LESS);
        }
    }

    /**
     * 渲染所有可见体素。此方法会默认尽最大努力进行视锥剔除。
     *
     * @param voxelScene 需要进行渲染的场景
     */
    private void renderVoxel(VoxelScene voxelScene, DebugInfo debugInfo) {
        agent.reset(voxelScene.getCamera());

        int instancedBuffer = instancedMesh.getInstancedBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, instancedBuffer);

        voxelShaderProgram.bind();
        voxelShaderProgram.setUniform("Texture", 0);
        int count = 0;
        for (Chunk chunk : voxelScene.getChunkList()) {
            if (agent.testChunk(chunk)) {
                for (int i = 0; i < Chunk.ROOT_COUNT; i++) {
                    if (agent.filterVoxel(chunk, i)) {
                        for (int behavior : agent.getBehaviors()) {
                            if (agent.getInstanceCount(behavior) > 0) {
                                count += agent.getInstanceCount(behavior);
                                FloatBuffer buffer = agent.getInstanceBuffer(behavior);

                                instancedMesh.setTextureId(VoxelBehavior.getTextureId((byte) behavior));
                                glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
                                instancedMesh.draw(agent.getInstanceCount(behavior));
                            }
                        }
                    }
                }
            }
        }

        debugInfo.addGameDebugInfo("voxel.render.count", count);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        voxelShaderProgram.unbind();
    }

    private void renderModel(VoxelScene voxelScene) {
        TEMP.identity().translate(0, 0, -5);
        modelShaderProgram.bind();
        modelShaderProgram.setUniform("modelMtx", TEMP);
        modelShaderProgram.setUniform("modelTexture", 0);
        voxelScene.getModels().forEach(Model::draw);
        modelShaderProgram.unbind();
    }

    private void beforeRender() {
        if (!window.isResized() && useFrameBuffer) {
            // 如果当前窗口尺寸发生改变，删除并重设帧缓冲
            if (preWidth != window.getWidth() || preHeight != window.getHeight()) {
                glDeleteTextures(colorAttachment);
                glDeleteFramebuffers(frameBuffer);
                glDeleteRenderbuffers(renderBuffer);
                setupFrameBuffer();
            }
            if (useFrameBuffer) {
                glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
                GLHelp.frameBufferClearColor();
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            }
            preWidth  = window.getWidth();
            preHeight = window.getHeight();
        }
    }

    private void endRender() {
        if (!window.isResized() && useFrameBuffer) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            GLHelp.frameBufferClearColor();
            glClear(GL_COLOR_BUFFER_BIT);
            glDisable(GL_DEPTH_TEST);

            screenShaderProgram.bind();

            screenShaderProgram.setUniform("screenTexture", 0);
            screenQuad.setTextureId(colorAttachment);
            screenQuad.draw();

            screenShaderProgram.unbind();
        }
    }

    /**
     * 设置投影矩阵和视图矩阵
     *
     * @param camera 当前场景渲染的摄像机。
     */
    private void setupProjectAndView(Camera camera) {
        glBindBuffer(GL31.GL_UNIFORM_BUFFER, uniformBufferObjectMatrices);
        glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, camera.getProjectionMtxBuffer());
        glBufferSubData(GL31.GL_UNIFORM_BUFFER, 16 * 4, camera.getViewMtxBuffer());
        glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
    }

    @Override
    public void render(Window window, EngineContext engineContext) {
        this.window = window;
        beforeRender();

        Camera camera = voxelScene.getCamera();
        if (window.isDebugMode()) {
            GlobalVariable.ENGINE_DEBUG_INFO.push(
                    String.format("Position: [x: %.3f, y: %.3f, z: %.3f]", camera.getPosition().x,
                                  camera.getPosition().y, camera.getPosition().z));
            GlobalVariable.ENGINE_DEBUG_INFO.push(
                    String.format("Rotation: [x: %.3f, y: %.3f]", camera.getRotation().x, camera.getRotation().y));
        }

        setupOpenGLState();
        setupProjectAndView(camera);

        // 渲染所有体素
        renderVoxel(voxelScene, engineContext.getDebugInfo());

        // 渲染所有模型
        renderModel(voxelScene);

        // 渲染当前的天空盒
        renderSkyBox(voxelScene);
        endRender();
    }

    private void setupOpenGLState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_CULL_FACE);
    }

    @Override
    public void close() {
        glDeleteTextures(colorAttachment);
        glDeleteFramebuffers(frameBuffer);
        glDeleteRenderbuffers(renderBuffer);

        voxelScene.cleanUp();
        agent.cleanup();
    }
}


