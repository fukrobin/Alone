package pers.crobin.engine.ui.control;

import pers.crobin.engine.ui.text.Font;
import pers.crobin.engine.util.Utils;

public class
TextInputControl {
    /**
     * 此{@link TextInputControl}的文本内容
     */
    protected final StringBuilder text;

    /**
     * 此{@link TextInputControl}中用于文本的默认字体。
     */
    protected final Font          font;

    /**
     * 插入符号在文本中的当前位置
     */
    protected       int           caretPosition;

    /**
     * {@link TextInputControl} 需要显示的提示文本，如果未设置，则为null
     */
    protected       String        promptText;

    /**
     * 指示此{@link TextInputControl}是否可由用户编辑
     */
    protected       boolean       edit;

    public TextInputControl() {
        caretPosition = 0;
        promptText    = null;
        edit          = true;
        text          = new StringBuilder();
        font          = new Font(14, "sans", Utils.hexColorToRgba("#000000"));
    }

    public int getLength() {
        return this.text.length();
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public TextInputControl setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
        return this;
    }

    public String getText() {
        return text.toString();
    }

    public TextInputControl setText(String text) {
        this.text.replace(0, this.text.length(), text);
        return this;
    }

    public void clear() {
        text.setLength(0);
    }

    public String getPromptText() {
        return promptText;
    }

    public TextInputControl setPromptText(String promptText) {
        this.promptText = promptText;
        return this;
    }

    public boolean isEdit() {
        return edit;
    }

    public TextInputControl setEdit(boolean edit) {
        this.edit = edit;
        return this;
    }

    public Font getFont() {
        return font;
    }

    public String getText(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("The start must be <= the end");
        }

        if (start < 0 || end > getLength()) {
            throw new IndexOutOfBoundsException();
        }

        return this.text.substring(start, end);
    }

    public void appendText(String text) {
        insertText(getLength(), text);
    }

    public void appendText(char c) {
        insertText(getLength(), String.valueOf(c));
    }

    public void insertText(int index, String text) {
        replaceText(index, index, text);
    }

    public void insertText(int index, char c) {
        this.text.insert(index, c);
    }

    public void deleteText(IndexRange range) {
        replaceText(range, "");
    }

    public void deleteText(int start, int end) {
        replaceText(start, end, "");
    }

    public void deleteCharAt(int index) {
        replaceText(index, index + 1, "");
    }

    /**
     * 删除{@link TextInputControl#caretPosition}之前的一个字符
     */
    public void deletePreviousChar() {
        if (isEdit()) {
            // caretPosition为0时代表已到达home
            if (caretPosition > 0) {
                deleteCharAt(caretPosition - 1);
                caretPosition--;
            }
        }
    }

    /**
     * 删除{@link TextInputControl#caretPosition}之后的一个字符
     */
    public void deleteNextChar() {
        if (isEdit()) {
            // caretPosition大于等于length代表已经到达end
            if (caretPosition < getLength()) {
                deleteCharAt(caretPosition);
            }
        }
    }

    public void replaceText(IndexRange range, String text) {
        final int start = range.getStart();
        final int end = start + range.getLength();
        replaceText(start, end, text);
    }

    /**
     * 用给定的字符串替换范围内的字符。
     *
     * @param start 范围内的开始索引， 包含。必须 &gt;=0 and &lt; end。
     * @param end   范围内的结束索引，不包含。这是最后一个要删除的字符。必须 &gt; start,
     *              并且 &lt;= text的长度.
     * @param text  用于替换范围内的字符串。不能为空
     */
    public void replaceText(final int start, final int end, final String text) {
        if (start > end) {
            throw new IllegalArgumentException();
        }

        if (text == null) {
            throw new IllegalArgumentException();
        }

        if (start < 0 || end > getLength()) {
            throw new IndexOutOfBoundsException();
        }

        this.text.replace(start, end, text);
    }
}
