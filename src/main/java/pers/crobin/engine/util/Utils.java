package pers.crobin.engine.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.joml.Vector2f;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.crobin.engine.event.MouseEvent;
import pers.crobin.engine.geometry.Bounds;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memUTF8;

/**
 * Created by Administrator on 2020/4/4.
 *
 * @author fkobin
 * @date 2020/4/4 19:49
 * @Description
 **/
public class Utils {
    private static final Logger                   LOGGER                = LoggerFactory.getLogger("Util");

    private static final ThreadFactory            FACTORY
                                                                        = new ThreadFactoryBuilder().setNameFormat(
            "alone-pool-%d").build();

    private static final ThreadPoolExecutor       THREAD_POOL_EXECUTOR  = new ThreadPoolExecutor(10, 100, 1L,
                                                                                                 TimeUnit.SECONDS,
                                                                                                 new LinkedBlockingDeque<>(
                                                                                                         1000),
                                                                                                 FACTORY,
                                                                                                 new ThreadPoolExecutor.AbortPolicy());

    private static final ScheduledExecutorService SCHEDULED_THREAD_POOL = Executors.newScheduledThreadPool(2);

    private static final LinkedList<ByteBuffer> BUFFERS = new LinkedList<>();

    private static final Pattern HEX_COLOR_PATTERN  = Pattern.compile("^#[0-9a-f]{6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern HEX_STRING_PATTERN = Pattern.compile("^[0-9a-f]+", Pattern.CASE_INSENSITIVE);

    public static void submitTask(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public static void timerTask(TimerTask task) {
        SCHEDULED_THREAD_POOL.scheduleAtFixedRate(task, 1000, 500, TimeUnit.MILLISECONDS);
    }

    public static String loadResource(String fileName) throws IOException {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }

    public static String loadResource(InputStream stream) throws IOException {
        String result;
        try (Scanner scanner = new Scanner(stream, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }

    /**
     * 加载资源，以ByteBuffer返回结果
     *
     * @param resourcePath 资源路径
     * @return {@link java.nio.ByteBuffer}
     */
    public static ByteBuffer loadResourceToByteBuffer(String resourcePath) throws IOException {
        URL url = Utils.class.getResource(resourcePath);

        int resourceSize = url.openConnection().getContentLength();

        ByteBuffer resource = memAlloc(resourceSize);

        try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
            int b;
            do {
                b = bis.read();
                if (b != -1) {
                    resource.put((byte) (b & 0xff));
                }
            } while (b != -1);
        }

        resource.flip();

        return resource;
    }

    public static int loadCubeTexture(String[] faces) throws IOException {
        int cubeTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeTexture);

        for (int i = 0; i < 6; i++) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                ByteBuffer data = stbi_load(faces[i], w, h, channels, 4);
                if (data == null) {
                    throw new IOException("Image file [" + faces[i] + "] not loaded: " + stbi_failure_reason());
                }

                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X +
                             i, 0, GL_RGBA, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
                stbi_image_free(data);
            }
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        return cubeTexture;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);

        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {
                }
            }
        } else {
            try (InputStream source = Utils.class.getClassLoader().getResourceAsStream(resource);
                 ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public static float[] listFloatToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static ByteBuffer stringToByteBuffer(String data) {
        if (data != null && !data.isEmpty()) {
            ByteBuffer buffer = memUTF8(data, false);
            BUFFERS.add(buffer);
            return buffer;
        }
        return null;
    }

    /******************************************************************************
     *
     *  NanoVG的一些实用方法
     *
     ******************************************************************************/

    public static ByteBuffer cpToUtf8(int codePoint) {
        ByteBuffer data = memUTF8(new String(Character.toChars(codePoint)), false);
        BUFFERS.add(data);
        return data;
    }

    public static boolean isHover(float x, float y, float width, float height, MouseEvent event) {
        double cx = event.getCursorPosX();
        double cy = event.getCursorPosY();
        return (cx >= x && cx <= x + width) && (cy >= y && cy <= y + height);
    }

    public static boolean isHover(Vector2f position, Vector2f size, MouseEvent event) {
        return isHover(position.x, position.y, size.x, size.y, event);
    }

    public static boolean isHover(Bounds controlBounds, MouseEvent event) {
        return controlBounds.contains(event.getCursorPosX(), event.getCursorPosY());
    }

    public static NVGColor rgba(int r, int g, int b, int a, NVGColor result) {
        result.r(r / 255.0f);
        result.g(g / 255.0f);
        result.b(b / 255.0f);
        result.a(a / 255.0f);

        return result;
    }

    public static NVGColor rgba(Vector4i color, NVGColor result) {
        result.r(color.x / 255.0f);
        result.g(color.y / 255.0f);
        result.b(color.z / 255.0f);
        result.a(color.w / 255.0f);

        return result;
    }


    /**
     * 将16进制的颜色字符串转换为RGBA表示的颜色。
     *
     * @param hexColor 16进制的rgba颜色字符串，如 <code>#ecff20</code>
     * @return 以 {@link Vector4i}表示的RGBA颜色，x、y、z、w分别对应r、g、b、a。
     */
    public static Vector4i hexColorToRgba(String hexColor) {
        Vector4i color = new Vector4i();
        return hexStringToVector(hexColor, color);
    }

    /**
     * 将16进制的颜色字符串转换为RGBA表示的颜色。
     *
     * @param hexColor 16进制的rgba颜色字符串，如 <code>#ecff20</code>
     * @param color    RGBA颜色结果
     * @return 以 {@link Vector4i}表示的RGBA颜色，x、y、z、w分别对应r、g、b、a。
     */
    public static Vector4i hexColorToRgba(String hexColor, Vector4i color) {
        return hexStringToVector(hexColor, color);
    }

    private static Vector4i hexStringToVector(String hexColor, Vector4i color) {
        if (HEX_COLOR_PATTERN.matcher(hexColor).matches()) {
            hexColor = hexColor.substring(1);
            hexColor = hexColor.toLowerCase();
            char[] chars = hexColor.toCharArray();
            int[] ints = new int[6];
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] >= 'a') {
                    ints[i] = chars[i] - 'a' + 10;
                } else {
                    ints[i] = chars[i] - '0';
                }
            }
            color.set(ints[0] << 4 | ints[1], ints[2] << 4 | ints[3], ints[4] << 4 | ints[5], 255);
        } else {
            throw new IllegalArgumentException("Incorrect hexadecimal color string.");
        }

        return color;
    }

    public static String rgbToHexColorString(Vector4i color) {
        StringBuilder hexColor = new StringBuilder(7);
        hexColor.insert(0, '#');

        int a = (color.x & 0xf0) >> 4;
        hexColor.append((char) (a > 9 ? 'a' + a - 10 : '0' + a));
        a = color.x & 0xf;
        hexColor.append((char) (a > 9 ? 'a' + a - 10 : '0' + a));

        a = (color.y & 0xf0) >> 4;
        hexColor.append((char) (a > 9 ? 'a' + a - 10 : '0' + a));
        a = color.y & 0xf;
        hexColor.append((char) (a > 9 ? 'a' + a - 10 : '0' + a));

        a = (color.z & 0xf0) >> 4;
        hexColor.append((char) (a > 9 ? 'a' + a - 10 : '0' + a));
        a = color.z & 0xf;
        hexColor.append((char) (a > 9 ? 'a' + a - 10 : '0' + a));

        return hexColor.toString();
    }

    public static void addBufferToAutoFree(ByteBuffer buffer) {
        BUFFERS.add(buffer);
    }

    public static void freeBuffer(Buffer buffer) {
        MemoryUtil.memFree(buffer);
    }

    public static void freeBuffers(Buffer[] buffers) {
        if (buffers != null) {
            for (Buffer buffer : buffers) {
                freeBuffer(buffer);
            }
        }
    }

    /**
     * 以统一的格式输出错误日志，错误日志包括：错误代码位置信息、错误信息
     *
     * @param source  发生错误的代码位置
     * @param message 错误信息
     */
    public static void errLog(String source, String message) {
        LOGGER.error("\nSource: " + source + "\n" + "Error Message: " + message);
    }

    public static void warnLog(String source, String message) {
        LOGGER.warn("\n!!WARN!!\nSource: " + source + "\nMessage: " + message);
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void cleanUp() {
        for (ByteBuffer buffer : BUFFERS) {
            MemoryUtil.memFree(buffer);
        }

        SCHEDULED_THREAD_POOL.shutdownNow();
        THREAD_POOL_EXECUTOR.shutdown();
        try {
            if (!SCHEDULED_THREAD_POOL.awaitTermination(2, TimeUnit.SECONDS)) {
                LOGGER.warn("结束所有定时任务！");
            }
            if (!THREAD_POOL_EXECUTOR.awaitTermination(2, TimeUnit.SECONDS)) {
                LOGGER.error("Thread pool shutdown timeout！！");
            }
        } catch (InterruptedException e) {
            LOGGER.error("Thread pool has threads that are interrupt.");
        } finally {
            SCHEDULED_THREAD_POOL.shutdownNow();
            THREAD_POOL_EXECUTOR.shutdownNow();
        }
    }
}
