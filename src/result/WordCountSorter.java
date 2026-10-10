package kr.sesac.wordcounter.result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// [추가] 상위 N개 보기와 저장에서 똑같이 쓰던 정렬 코드를 한 곳으로 모음
// 정렬 기준: 빈도 내림차순, 동점이면 단어 사전순
public final class WordCountSorter {
    private static final Comparator<Map.Entry<String, Integer>> ORDER =
            Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                    .thenComparing(Map.Entry::getKey);

    private WordCountSorter() {}

    public static List<Map.Entry<String, Integer>> sortedEntries(Map<String, Integer> wordCounts) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordCounts.entrySet());
        list.sort(ORDER);
        return list;
    }
}
