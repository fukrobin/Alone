package per.alone.engine.util;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/4/26 23:25
 **/
public class Profiler {
    private final LinkedList<String>    sectionList;

    private final LinkedList<Long>      timestampList;

    private final HashMap<String, Long> profilingMap;

    private final Multiset<String>      sectionCount;

    private String profilingSection = "";

    public Profiler() {
        sectionList   = new LinkedList<>();
        timestampList = new LinkedList<>();
        sectionCount  = HashMultiset.create(16);
        profilingMap  = new HashMap<>();
    }

    public void startSection(String sectionName) {
        Objects.requireNonNull(sectionCount);
        profilingSection = sectionName;
        sectionList.push(sectionName);
        this.timestampList.push(System.nanoTime());
        sectionCount.add(sectionName);
    }

    public void endSection() {
        long currentTime = System.nanoTime();
        long startTime = timestampList.pop();
        sectionList.pop();

        long elapsed = currentTime - startTime;
        if (profilingMap.containsKey(profilingSection)) {
            profilingMap.put(profilingSection, profilingMap.get(profilingSection) + elapsed);
        } else {
            profilingMap.put(profilingSection, elapsed);
        }

        if (elapsed > 100000000L) {
            Utils.warnLog("Profiler",
                          String.format("Something's taking too long! '%s' took about %f ms", profilingSection,
                                        elapsed / 1000000.0D));
        }
        profilingSection = sectionList.isEmpty() ? "" : sectionList.peekLast();
    }

    public void endStartSection(String sectionName) {
        endSection();
        startSection(sectionName);
    }

    public long getSectionTimeStamp(String sectionName) {
        return profilingMap.get(sectionName);
    }

    public void outputToFile(File file) throws IOException {
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(file))) {
            jsonWriter.setIndent("    ");
            jsonWriter.beginArray();

            long time;
            long count;
            for (Map.Entry<String, Long> entry : profilingMap.entrySet()) {
                jsonWriter.beginObject();

                time  = entry.getValue();
                count = sectionCount.count(entry.getKey());
                jsonWriter.name("section_name").value(entry.getKey());
                jsonWriter.name("total_time").value(time);
                jsonWriter.name("section_count").value(count);
                jsonWriter.name("average_time").value(String.format("%.3f", time / (count * 1000000.d)));

                jsonWriter.endObject();
            }


            jsonWriter.endArray();
        }
    }
}
