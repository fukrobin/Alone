package per.alone.stage.input;

import java.util.HashMap;
import java.util.Map;

/**
 * 鼠标按键的枚举
 *
 * @author fkrobin
 * @date 2021/9/16 16:10
 */
public enum MouseButton {

    /**
     * 表示没有按键
     */
    NONE(-1, "NONE"),
    BUTTON_4(3, "BUTTON_4"),
    BUTTON_5(4, "BUTTON_5"),
    BUTTON_6(5, "BUTTON_6"),
    BUTTON_7(6, "BUTTON_7"),
    BUTTON_8(7, "BUTTON_8"),
    BUTTON_LEFT(0, "BUTTON_LEFT"),
    BUTTON_RIGHT(1, "BUTTON_RIGHT"),
    BUTTON_MIDDLE(2, "BUTTON_MIDDLE");

    private static final Map<Integer, MouseButton> codeMap;

    static {
        codeMap = new HashMap<>();
        for (MouseButton mouseButton : MouseButton.values()) {
            codeMap.put(mouseButton.code, mouseButton);
        }
    }

    int code;

    String name;

    MouseButton(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据 code 返回 mouse button，
     *
     * @param code 代表一个 mouse button
     * @return code 返回关联的 MouseButton，或者返回null，如果code不合法
     */
    public static MouseButton fromCode(int code) {
        return codeMap.get(code);
    }
}
