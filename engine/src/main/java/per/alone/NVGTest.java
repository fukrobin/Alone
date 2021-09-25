package per.alone;

import per.alone.engine.context.EngineContext;
import per.alone.engine.kernel.BaseEngine;
import per.alone.stage.Window;

import java.io.IOException;

/**
 * //TODO
 *
 * @author fkrobin
 * @date 2021/9/25 16:12
 */
public class NVGTest extends BaseEngine {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void errorCallback(Exception e) {

    }

    @Override
    public void start(String[] args, EngineContext engineContext, Window window) {
        window.show();
    }

    @Override
    public void run() {

    }
}
