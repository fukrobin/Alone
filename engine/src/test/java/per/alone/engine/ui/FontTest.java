package per.alone.engine.ui;

import org.junit.jupiter.api.Test;

import java.awt.*;

public class FontTest {
    @Test
    void testGetSystemFonts() throws Exception {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (Font font : environment.getAllFonts()) {
            System.out.println(font);

        }
    }
}
