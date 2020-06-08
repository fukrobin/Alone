package pers.crobin.engine.ui.text;

import org.lwjgl.nanovg.NanoVG;

/**
 * @author Administrator
 */

public enum TextAlignment {
    /** 左上角对齐 */
    TOP_LEFT(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP),
    /** 右上角对齐 */
    TOP_RIGHT(NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_TOP),
    /** 左下角对齐 */
    BOTTOM_LEFT(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_BOTTOM),
    /** 右下角对齐 */
    BOTTOM_RIGHT(NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_BOTTOM),
    /** 正中心对齐 */
    CENTER(NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_CENTER),
    /** 左边界中心对齐 */
    CENTER_LEFT(NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT),
    /** 右边界中心对齐 */
    CENTER_RIGHT(NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_RIGHT),
    /** 上边界中心对齐 */
    CENTER_TOP(NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_TOP),
    /** 下边界中心对齐 */
    CENTER_BOTTOM(NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_BOTTOM),
    /** 默认，垂直于基线对齐 */
    BASELINE(NanoVG.NVG_ALIGN_BASELINE);
    private final int align;

    TextAlignment(int align) {
        this.align = align;
    }

    public int getAlign() {
        return align;
    }
}
