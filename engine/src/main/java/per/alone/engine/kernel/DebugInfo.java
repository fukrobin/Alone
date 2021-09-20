package per.alone.engine.kernel;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/7 16:10
 **/
public class DebugInfo {
    private static DebugInfo debugInfo = null;

    private final Map<String, StringBuilder> gameInfo;

    private final Map<String, StringBuilder> engineInfo;

    private final GlobalMemory memory;

    private DebugInfo() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();

        memory = hardware.getMemory();
        List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
        CentralProcessor processor = hardware.getProcessor();
        CentralProcessor.ProcessorIdentifier identifier = processor.getProcessorIdentifier();

        Properties systemProperties = System.getProperties();

        gameInfo   = new HashMap<>();
        engineInfo = new HashMap<>();
        int i = 0;
        for (GraphicsCard graphicsCard : graphicsCards) {
            engineInfo.put(String.format("GPU[%d]", ++i), new StringBuilder(graphicsCard.getName()));
        }
        engineInfo.put("CPU", new StringBuilder(String.format("CPU: %d核 %s",
                                                              processor.getLogicalProcessorCount(),
                                                              identifier.getName())));
        engineInfo.put("os.name", new StringBuilder((String) systemProperties.get("os.name")));
        engineInfo.put("os.arch", new StringBuilder((String) systemProperties.get("os.arch")));
        engineInfo.put("os.version", new StringBuilder((String) systemProperties.get("os.version")));
        engineInfo.put("java.version", new StringBuilder(systemProperties.getProperty("java.runtime.name") + " " +
                                                         systemProperties.get("java.version")));
        engineInfo.put("Memory", new StringBuilder(getMemoryInfo()));
    }

    public static DebugInfo getInstance() {
        if (debugInfo == null) {
            debugInfo = new DebugInfo();
        }
        return debugInfo;
    }

    void updateEngineDebugInfo() {
        StringBuilder builder = engineInfo.get("Memory");
        builder.replace(0, builder.length(), getMemoryInfo());
    }

    private String getMemoryInfo() {
        long total = memory.getTotal();
        return String.format("Remaining memory: %-6dM, Total: %-6dM",
                             (total - memory.getAvailable()) >> 20, total >> 20);
    }

    public Map<String, StringBuilder> getEngineInfo() {
        return engineInfo;
    }

    public void addGameDebugInfo(String source, Object debugInfo) {
        StringBuilder stringBuilder = gameInfo.computeIfAbsent(source, s -> new StringBuilder(16));

        stringBuilder.replace(0, stringBuilder.length(), debugInfo.toString());
    }

    public Map<String, StringBuilder> getGameInfo() {
        return gameInfo;
    }
}
