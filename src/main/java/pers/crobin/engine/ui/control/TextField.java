package pers.crobin.engine.ui.control;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGTextRow;
import pers.crobin.engine.event.EventHandler;
import pers.crobin.engine.event.KeyEvent;
import pers.crobin.engine.kernel.EngineThread;
import pers.crobin.engine.ui.Canvas;
import pers.crobin.engine.ui.text.TextAlignment;

import java.util.Queue;

/**
 * @author Administrator
 */
public class TextField extends Region {
    private static final NVGTextRow.Buffer      BUFFER      = NVGTextRow.create(2);
    protected final      TextInputControl       inputControl;
    protected            EventHandler<KeyEvent> charUpdate;
    protected            EventHandler<KeyEvent> cursorUpdateHandler;

    protected boolean focus;

    public TextField() {
        border = 3;
        borderColor.set(21, 133, 255, 255);
        backgroundColor.set(87, 197, 198, 255);

        inputControl = new TextInputControl();
        inputControl.font.setFontSize(16);
        focus = false;
    }

    public boolean isFocus() {
        return focus;
    }

    public TextField setFocus(boolean focus) {
        this.focus = focus;
        return this;
    }

    public EventHandler<KeyEvent> getCharUpdateHandler() {
        if (charUpdate == null) {
            charUpdate = keyEvent -> {
                if (focus) {
                    // input
                    Queue<Integer> codePointList = EngineThread.getThreadWindow().getInputQueue();
                    int            insertPos     = inputControl.caretPosition;
                    Canvas.fontSize(inputControl.font.getFontSize());
                    for (Integer integer : codePointList) {
                        char[] chars = Character.toChars(integer);

                        for (char aChar : chars) {
                            inputControl.insertText(insertPos++, aChar);
                        }

                        // 只有插入字符后才能得知是否已经超出字符区域
                        // 限制文字内容区域
                        int rowLines = Canvas.textBreakLines(inputControl.text, size.x - 4, BUFFER);
                        if (rowLines > 1) {
                            for (int i = 0; i < chars.length; i++) {
                                // 迭代结束后caretPosition会被覆盖，此处需要更新insertPos；
                                insertPos--;
                                inputControl.deletePreviousChar();
                            }
                            break;
                        }
                    }
                    inputControl.caretPosition = insertPos;


                    double currentTime = GLFW.glfwGetTime();
                    if ((currentTime - lastTime) >= FIRE_INTERVAL) {
                        // delete char
                        if (isDeleted(keyEvent)) {
                            inputControl.deletePreviousChar();
                        }
                    }
                }
            };
        }

        return charUpdate;
    }

    private boolean isDeleted(KeyEvent event) {
        return inputControl.getLength() > 0 &&
               (event.isKeyPressed(GLFW.GLFW_KEY_DELETE) ||
                event.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE));
    }

    /** 按键触发间隔，单位为ms*/
    private static final double FIRE_INTERVAL = 0.1;
    private double lastTime = 0;
    public EventHandler<KeyEvent> getCursorUpdateHandler() {
        if (cursorUpdateHandler == null) {
            cursorUpdateHandler = keyEvent -> {
                double currentTime = GLFW.glfwGetTime();
                if ((currentTime - lastTime) >= FIRE_INTERVAL) {
                    if (keyEvent.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
                        if (inputControl.caretPosition > 0) {
                            inputControl.caretPosition--;
                        }
                    }

                    if (keyEvent.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
                        if (inputControl.caretPosition < inputControl.getLength()) {
                            inputControl.caretPosition++;
                        }
                    }
                    lastTime = currentTime;
                }
            };
        }
        return cursorUpdateHandler;
    }

    @Override
    public void draw(float offsetX, float offsetY) {
        super.draw(offsetX, offsetY);
        float x = offsetX + position.x + 2;
        float y = offsetY + position.y;

        Canvas.setFont(inputControl.font, TextAlignment.CENTER_LEFT);
        Canvas.drawText(inputControl.text, new Vector2f(x, y + size.y * 0.5f));
        if (focus) {
            // 绘制光标
            int caret = inputControl.caretPosition;
            // 无字符串时，此处的代码有问题，需要清空BUFFER内的数据
            if (caret > 0) {
                Canvas.textBreakLines(inputControl.getText(0, inputControl.caretPosition), size.x - 4, BUFFER);
                NVGTextRow row = BUFFER.get(0);
                Canvas.drawLine(x + row.maxx(), y + 5, x + row.maxx(), y - 5 + size.y);
            } else {
                Canvas.drawLine(x, y + 5, x, y - 5 + size.y);
            }
        }
    }
}
