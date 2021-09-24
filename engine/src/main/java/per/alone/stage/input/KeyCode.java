package per.alone.stage.input;

import java.util.HashMap;
import java.util.Map;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/16 18:25
 */
@SuppressWarnings("unused")
public enum KeyCode {
    UNKNOWN(-1, "Unknown"),
    SPACE(32, "SPACE"),
    APOSTROPHE(39, "APOSTROPHE"),
    COMMA(44, "COMMA"),
    MINUS(45, "MINUS"),
    PERIOD(46, "PERIOD"),
    SLASH(47, "SLASH"),
    DIGIT0(48, "0"),
    DIGIT1(49, "1"),
    DIGIT2(50, "2"),
    DIGIT3(51, "3"),
    DIGIT4(52, "4"),
    DIGIT5(53, "5"),
    DIGIT6(54, "6"),
    DIGIT7(55, "7"),
    DIGIT8(56, "8"),
    DIGIT9(57, "9"),
    SEMICOLON(59, "SEMICOLON"),
    EQUAL(61, "EQUAL"),
    A(65, "A"),
    B(66, "B"),
    C(67, "C"),
    D(68, "D"),
    E(69, "E"),
    F(70, "F"),
    G(71, "G"),
    H(72, "H"),
    I(73, "I"),
    J(74, "J"),
    K(75, "K"),
    L(76, "L"),
    M(77, "M"),
    N(78, "N"),
    O(79, "O"),
    P(80, "P"),
    Q(81, "Q"),
    R(82, "R"),
    S(83, "S"),
    T(84, "T"),
    U(85, "U"),
    V(86, "V"),
    W(87, "W"),
    X(88, "X"),
    Y(89, "Y"),
    Z(90, "Z"),
    LEFT_BRACKET(91, "LEFT_BRACKET"),
    BACKSLASH(92, "BACKSLASH"),
    RIGHT_BRACKET(93, "RIGHT_BRACKET"),
    GRAVE_ACCENT(96, "GRAVE_ACCENT"),
    WORLD_1(161, "WORLD_1"),
    WORLD_2(162, "WORLD_2"),
    ESCAPE(256, "ESCAPE"),
    ENTER(257, "ENTER"),
    TAB(258, "TAB"),
    BACKSPACE(259, "BACKSPACE"),
    INSERT(260, "INSERT"),
    DELETE(261, "DELETE"),
    RIGHT(262, "RIGHT"),
    LEFT(263, "LEFT"),
    DOWN(264, "DOWN"),
    UP(265, "UP"),
    PAGE_UP(266, "PAGE_UP"),
    PAGE_DOWN(267, "PAGE_DOWN"),
    HOME(268, "HOME"),
    END(269, "END"),
    CAPS_LOCK(280, "CAPS_LOCK"),
    SCROLL_LOCK(281, "SCROLL_LOCK"),
    NUM_LOCK(282, "NUM_LOCK"),
    PRINT_SCREEN(283, "PRINT_SCREEN"),
    PAUSE(284, "PAUSE"),
    F1(290, "F1"),
    F2(291, "F2"),
    F3(292, "F3"),
    F4(293, "F4"),
    F5(294, "F5"),
    F6(295, "F6"),
    F7(296, "F7"),
    F8(297, "F8"),
    F9(298, "F9"),
    F10(299, "F10"),
    F11(300, "F11"),
    F12(301, "F12"),
    F13(302, "F13"),
    F14(303, "F14"),
    F15(304, "F15"),
    F16(305, "F16"),
    F17(306, "F17"),
    F18(307, "F18"),
    F19(308, "F19"),
    F20(309, "F20"),
    F21(310, "F21"),
    F22(311, "F22"),
    F23(312, "F23"),
    F24(313, "F24"),
    F25(314, "F25"),
    KP_0(320, "KP_0"),
    KP_1(321, "KP_1"),
    KP_2(322, "KP_2"),
    KP_3(323, "KP_3"),
    KP_4(324, "KP_4"),
    KP_5(325, "KP_5"),
    KP_6(326, "KP_6"),
    KP_7(327, "KP_7"),
    KP_8(328, "KP_8"),
    KP_9(329, "KP_9"),
    KP_DECIMAL(330, "KP_DECIMAL"),
    KP_DIVIDE(331, "KP_DIVIDE"),
    KP_MULTIPLY(332, "KP_MULTIPLY"),
    KP_SUBTRACT(333, "KP_SUBTRACT"),
    KP_ADD(334, "KP_ADD"),
    KP_ENTER(335, "KP_ENTER"),
    KP_EQUAL(336, "KP_EQUAL"),
    LEFT_SHIFT(340, "LEFT_SHIFT"),
    LEFT_CONTROL(341, "LEFT_CONTROL"),
    LEFT_ALT(342, "LEFT_ALT"),
    LEFT_SUPER(343, "LEFT_SUPER"),
    RIGHT_SHIFT(344, "RIGHT_SHIFT"),
    RIGHT_CONTROL(345, "RIGHT_CONTROL"),
    RIGHT_ALT(346, "RIGHT_ALT"),
    RIGHT_SUPER(347, "RIGHT_SUPER"),
    MENU(348, "MENU");

    private static final Map<String, KeyCode> nameMap;

    private static final Map<Integer, KeyCode> codeMap;

    static {
        nameMap = new HashMap<>(KeyCode.values().length);
        codeMap = new HashMap<>(KeyCode.values().length);
        for (KeyCode c : KeyCode.values()) {
            nameMap.put(c.name, c);
            codeMap.put(c.code, c);
        }
    }

    /**
     * key code
     */
    int code;

    /**
     * key 的可读名称
     */
    String name;

    KeyCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 通过 name 获取 keycode
     *
     * @param name Textual representation of the key
     * @return KeyCode for the key with the given name, null if the string
     * is unknown.
     */
    public static KeyCode fromName(String name) {
        return nameMap.get(name);
    }

    /**
     * 通过 code 获取 keycode
     *
     * @param code key 的 code 表示
     * @return 返回 code 对应的 keycode ，如果 code 不在[32,348]范围内则返回 null
     */
    public static KeyCode fromCode(int code) {
        return codeMap.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
