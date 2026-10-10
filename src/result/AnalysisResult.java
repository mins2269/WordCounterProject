package kr.sesac.wordcounter.result;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 분석 1회의 결과 데이터. 출력은 ResultOutput이 담당한다. */
public class AnalysisResult {
    private final Path inputPath;

    private int attempt = 0;
    private int success = 0;
    private int fail = 0;
    private int skip = 0;
    private long totalWordCount = 0;
    private double elapsedTimeMs = 0.0;

    private final Map<String, Integer> wordCountMap = new LinkedHashMap<>();
    private final List<String> failMessages = new ArrayList<>();

    public AnalysisResult(Path inputPath) { this.inputPath = inputPath; }

    public void attemptIncrease() { attempt++; }
    public void successIncrease() { success++; }
    public void failIncrease() { fail++; }
    public void skipIncrease() { skip++; }
    public void addFailMessage(String message) { failMessages.add(message); }
    public void setElapsedTimeMs(double elapsedTimeMs) { this.elapsedTimeMs = elapsedTimeMs; }

    public void addWordCount(String word, int count) {
        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + count);
        totalWordCount += count;
    }

    /** 파일 하나의 임시 집계 결과를 전체 결과에 병합 */
    public void mergeFileCounts(Map<String, Integer> fileCounts) {
        for (Map.Entry<String, Integer> e : fileCounts.entrySet()) {
            addWordCount(e.getKey(), e.getValue());
        }
    }

    public Path getInputPath() { return inputPath; }
    public int getAttempted() { return attempt; }
    public int getSuccess() { return success; }
    public int getFail() { return fail; }
    public int getSkip() { return skip; }
    public long getTotalWordCount() { return totalWordCount; }
    public int getDistinctCount() { return wordCountMap.size(); }
    public double getElapsedTimeMs() { return elapsedTimeMs; }
    public Map<String, Integer> getWordCountMap() { return wordCountMap; }
    public List<String> getFailMessages() { return Collections.unmodifiableList(failMessages); }
}
