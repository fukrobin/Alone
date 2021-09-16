package pers.crobin.engine.scene;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import pers.crobin.engine.util.GLHelp;
import pers.crobin.engine.util.Utils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/4/20 14:11
 **/
@SuppressWarnings("ConstantConditions")
public class Model {
    private final LinkedList<Mesh> meshList;

    private final String           textureDir;

    private final String           modelPath;

    public Model(String path, String textureDir) {
        this.textureDir = textureDir;
        this.modelPath  = path;
        this.meshList   = new LinkedList<>();
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D texCoordinate = textCoords.get();
            textures.add(texCoordinate.x());
            textures.add(1 - texCoordinate.y());
        }
    }

    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

    public void draw() {
        for (Mesh mesh : meshList) {
            mesh.draw();
        }
    }

    private AIScene loadScene() {
        // 将所有非三角形图形转换为三角形、翻转UV、合并所有相同的顶点数据（未指定时渲染不需要索引）
        AIScene aiScene = aiImportFile(modelPath,
                                       aiProcess_Triangulate | aiProcess_FindInvalidData |
                                       aiProcess_JoinIdenticalVertices | aiProcess_FixInfacingNormals);
        if (aiScene == null || aiScene.mFlags() == AI_SCENE_FLAGS_INCOMPLETE || aiScene.mRootNode() == null) {
            Utils.errLog("Model.loadScene()", aiGetErrorString());
            return null;
        }
        return aiScene;
    }

    public void loadInstanceModel() {
        AIScene aiScene = loadScene();
        if (aiScene != null) {
            int numMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                InstancedMesh mesh = new InstancedMesh();
                processMesh(aiMesh, aiScene, mesh);
                if (mesh.vao != -1) {
                    meshList.add(mesh);
                }
            }
        }
    }

    public void loadStaticModel() {
        AIScene aiScene = loadScene();
        if (aiScene != null) {
            int numMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                Mesh mesh = new Mesh();
                processMesh(aiMesh, aiScene, mesh);
                if (mesh.vao != -1) {
                    meshList.add(mesh);
                }
            }
        }
    }

    public LinkedList<Mesh> getMeshList() {
        return meshList;
    }

    private void processNode(AINode node, AIScene aiScene, Mesh mesh) {
        PointerBuffer meshesBuffer = aiScene.mMeshes();
        for (int i = 0; i < node.mNumMeshes(); i++) {
            AIMesh aiMesh = AIMesh.create(meshesBuffer.get(node.mMeshes().get(i)));
            processMesh(aiMesh, aiScene, mesh);
            if (mesh.vao != -1) {
                meshList.add(mesh);
            }
        }

        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode childNode = AINode.create(node.mChildren().get(i));
            this.processNode(childNode, aiScene, new Mesh());
        }
    }

    /**
     * 解析纹理文件路径，将其转化为本引擎可用的位置
     *
     * @param texturePath 纹理路径，也许是相对路径，也许是绝对路径，但本方法都只会截取文件名字，路径位置使用实例化时传入的参数
     * @return 解析后可用的纹理路径位置
     */
    private String parseTexturePath(String texturePath) {
        texturePath = texturePath.replaceAll("\\\\", "/");
        texturePath = texturePath.replaceAll("//", "/");
        int lastIdx = texturePath.lastIndexOf("/");
        if (lastIdx > 0) {
            texturePath = texturePath.substring(lastIdx);
        }

        return texturePath;
    }

    private void processMesh(AIMesh aiMesh, AIScene aiScene, Mesh resultMesh) {
        LinkedList<Float> vertices = new LinkedList<>();
        LinkedList<Float> textures = new LinkedList<>();
        LinkedList<Float> normals = new LinkedList<>();
        LinkedList<Integer> indices = new LinkedList<>();

        processTextCoords(aiMesh, textures);
        if (textures.isEmpty()) {
            Utils.errLog("Model.processMesh()", "Don't have texture coordinate, remove");
        } else {
            processVertices(aiMesh, vertices);
            processNormals(aiMesh, normals);
            processIndices(aiMesh, indices);
            resultMesh.setupMesh(Utils.listFloatToArray(vertices), Utils.listFloatToArray(textures),
                                 Utils.listFloatToArray(normals),
                                 Utils.listIntToArray(indices));
            if (aiMesh.mMaterialIndex() >= 0) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    AIString path = AIString.mallocStack(stack);
                    AIMaterial material = AIMaterial.create(aiScene.mMaterials().get(aiMesh.mMaterialIndex()));
                    Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null,
                                                null, null, null);
                    String textPath = path.dataString();

                    if (textPath.length() > 0) {
                        textPath = parseTexturePath(textPath);
                        int textureId = GLHelp.loadTexture(this.textureDir + textPath);
                        resultMesh.setTextureId(textureId);
                    }
                } catch (IOException e) {
                    Utils.errLog("Model.processMesh()", aiMesh.mName().dataString() + "\n" + e);
                }
            }
        }
    }
}

