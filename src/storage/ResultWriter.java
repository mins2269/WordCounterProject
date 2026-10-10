package kr.sesac.wordcounter.storage;

import kr.sesac.wordcounter.result.AnalysisResult;
import kr.sesac.wordcounter.result.WordCountSorter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** 분석 결과를 파일(TSV)로 저장한다. */
public class ResultWriter {
    private static final Path OUTPUT_PATH = Path.of("out", "counts.tsv");

    /** 저장한 경로를 반환한다. 실패하면 IOException */
    public Path save(AnalysisResult r) throws IOException {
        if (OUTPUT_PATH.getParent() != null) {
            Files.createDirectories(OUTPUT_PATH.getParent());
        }
        List<Map.Entry<String, Integer>> sorted = WordCountSorter.sortedEntries(r.getWordCountMap());

        try (BufferedWriter writer = Files.newBufferedWriter(OUTPUT_PATH, StandardCharsets.UTF_8)) {
            writer.write("word\tcount");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : sorted) {
                writer.write(entry.getKey() + "\t" + entry.getValue());
                writer.newLine();
            }
        }
        return OUTPUT_PATH;
    }
}
