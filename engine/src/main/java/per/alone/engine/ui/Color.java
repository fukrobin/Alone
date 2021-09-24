package per.alone.engine.ui;

import lombok.Getter;
import org.lwjgl.nanovg.NVGColor;

import java.util.regex.Pattern;

/**
 * @author jamie
 */
@Getter
public class Color {
    private static final NVGColor TEMP = NVGColor.create();
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-f]{6,8}$", Pattern.CASE_INSENSITIVE);

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public static Color rgba(int red, int green, int blue, int alpha) {
        checkRGB(red, green, blue);
        return new Color(
                red / 255.0,
                green / 255.0,
                blue / 255.0,
                alpha / 255.0);
    }

    public static Color rgb(int red, int green, int blue) {
        checkRGB(red, green, blue);
        return new Color(
                red / 255.0,
                green / 255.0,
                blue / 255.0,
                1.0);
    }

    /**
     * 将16进制的颜色字符串转换为RGBA表示的颜色
     *
     * @param hexColor 16进制的rgba颜色字符串，如 <code>#ecff20</code>
     * @return {@link Color}
     */
    public static Color parseHexColor(String hexColor) {
        Color color;
        if (HEX_COLOR_PATTERN.matcher(hexColor).matches()) {
            hexColor = hexColor.substring(1);
            hexColor = hexColor.toLowerCase();
            char[] chars = hexColor.toCharArray();
            int[] ints = new int[chars.length - 1];
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] >= 'a') {
                    ints[i] = chars[i] - 'a' + 10;
                } else {
                    ints[i] = chars[i] - '0';
                }
            }
            int alpha = ints.length == 6 ? 255 : ints[4] << 6 | ints[7];
            color = rgba(ints[0] << 4 | ints[1],
                         ints[2] << 4 | ints[3],
                         ints[4] << 4 | ints[5],
                         alpha);
        } else {
            throw new IllegalArgumentException("Incorrect hexadecimal color string.");
        }

        return color;
    }

    private static void checkRGB(int red, int green, int blue) {
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Color.rgb's red parameter (" + red + ") expects color values 0-255");
        }
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Color.rgb's green parameter (" + green + ") expects color values 0-255");
        }
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Color.rgb's blue parameter (" + blue + ") expects color values 0-255");
        }
    }

    public static NVGColor toNVGColor(float red, float green, float blue, float alpha) {
        return TEMP.r(red).g(green).b(blue).a(alpha);
    }

    public static NVGColor toNVGColor(Color color) {
        return TEMP.r(color.red).g(color.green).b(color.blue).a(color.alpha);
    }

    public Color(double red, double green, double blue, double alpha) {
        if (red < 0 || red > 1) {
            throw new IllegalArgumentException("Color's red value (" + red + ") must be in the range 0.0-1.0");
        }
        if (green < 0 || green > 1) {
            throw new IllegalArgumentException("Color's green value (" + green + ") must be in the range 0.0-1.0");
        }
        if (blue < 0 || blue > 1) {
            throw new IllegalArgumentException("Color's blue value (" + blue + ") must be in the range 0.0-1.0");
        }
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Color's opacity value (" + alpha + ") must be in the range 0.0-1.0");
        }
        this.red = (float) red;
        this.green = (float) green;
        this.blue = (float) blue;
        this.alpha = (float) alpha;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Color) {
            Color other = (Color) obj;
            return red == other.red
                    && green == other.green
                    && blue == other.blue
                    && alpha == other.alpha;
        } else return false;
    }

    /**
     * Returns a hash code for this {@code Color} object.
     *
     * @return a hash code for this {@code Color} object.
     */
    @Override
    public int hashCode() {
        // construct the 32bit integer representation of this color
        int r = (int) Math.round(red * 255.0);
        int g = (int) Math.round(green * 255.0);
        int b = (int) Math.round(blue * 255.0);
        int a = (int) Math.round(alpha * 255.0);
        int i = r;
        i = i << 8;
        i = i | g;
        i = i << 8;
        i = i | b;
        i = i << 8;
        i = i | a;
        return i;
    }

    @Override
    public String toString() {
        int r = (int) Math.round(red * 255.0);
        int g = (int) Math.round(green * 255.0);
        int b = (int) Math.round(blue * 255.0);
        int o = (int) Math.round(alpha * 255.0);
        return String.format("0x%02x%02x%02x%02x", r, g, b, o);
    }
}
