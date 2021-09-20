package per.alone.stage;

import lombok.Getter;
import lombok.ToString;

/**
 * Window 的数据承载对象
 *
 * @author fkrobin
 * @date 2021/9/17 23:25
 */
@Getter
@ToString
public class WindowData {

    /**
     * 窗口宽度
     */
    private final int width;

    /**
     * 窗口高度
     */
    private final int height;

    /**
     * 帧缓冲宽度
     */
    private final int frameBufferWidth;

    /**
     * 帧缓冲高度
     */
    private final int frameBufferHeight;

    /**
     * 像素比例
     */
    private final float pixelRadio;

    /**
     * 是否开启垂直同步
     */
    private final boolean vsync;

    /**
     * 窗口尺寸是否改变（上一次更新到此次）
     */
    private final boolean resized;

    /**
     * 窗口是否可见
     */
    private final boolean visible;

    /**
     * 窗口是否最小化
     */
    private final boolean iconified;

    /**
     * 窗口是否聚焦
     */
    private final boolean focused;

    /**
     * 窗口是否最大化
     */
    private final boolean maximized;

    /**
     * 窗口是否全屏
     */
    private final boolean fullScreen;

    /**
     * 窗口标题
     */
    private final String title;

    /**
     * 窗口是否是调试模式
     */
    private final boolean debugMode;

    private final boolean hiddenCursor;

    public WindowData(int width, int height, int frameBufferWidth, int frameBufferHeight, float pixelRadio,
                      boolean vsync, boolean resized, boolean visible, boolean iconified, boolean focused,
                      boolean maximized, boolean fullScreen, String title, boolean debugMode, boolean hiddenCursor) {
        this.width             = width;
        this.height            = height;
        this.frameBufferWidth  = frameBufferWidth;
        this.frameBufferHeight = frameBufferHeight;
        this.pixelRadio        = pixelRadio;
        this.vsync             = vsync;
        this.resized           = resized;
        this.visible           = visible;
        this.iconified         = iconified;
        this.focused           = focused;
        this.maximized         = maximized;
        this.fullScreen        = fullScreen;
        this.title             = title;
        this.debugMode         = debugMode;
        this.hiddenCursor      = hiddenCursor;
    }

}
