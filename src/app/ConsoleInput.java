package kr.sesac.wordcounter.app;

import kr.sesac.wordcounter.analyzer.Tokenizer;
import kr.sesac.wordcounter.storage.ResultWriter;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** 사용자 입력을 읽고 형식을 검증한다. 무엇을 할지(메뉴 처리)는 알지 못한다. */
public class ConsoleInput {
    public static final int INVALID_CHOICE = -1;
    private static final int DEFAULT_TOP_COUNT = 10;

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** 0~5 사이 숫자면 그 값, 아니면 INVALID_CHOICE */
    public int readMenuChoice() {
        String s = readLine("선택 > ");
        return s.matches("[0-5]") ? Integer.parseInt(s) : INVALID_CHOICE;
    }

    /** 경로를 입력받는다. 빈 입력(Enter)이면 취소 -> Optional.empty() */
    public Optional<Path> readPath() {
        while (true) {
            String s = readLine("파일 또는 폴더 경로 > ");
            if (s.isEmpty()) {
                return Optional.empty();
            }
            try {
                return Optional.of(Path.of(s));
            } catch (InvalidPathException e) {
                System.out.println("올바르지 않은 경로 형식입니다: " + s);
            }
        }
    }

    /** 상위 몇 개를 볼지 입력받는다. Enter면 기본값 10, 1 미만이거나 숫자가 아니면 다시 입력 */
    public int readTopCount() {
        while (true) {
            String s = readLine("몇 개를 볼까요? (기본 " + DEFAULT_TOP_COUNT + ") > ");
            if (s.isEmpty()) {
                return DEFAULT_TOP_COUNT;
            }
            try {
                int topCount = Integer.parseInt(s);
                if (topCount >= 1) {
                    return topCount;
                }
            } catch (NumberFormatException ignored) {
                // 아래 안내 메시지로 처리
            }
            System.out.println("1 이상의 정수를 입력하세요.");
        }
    }

    /** 찾을 단어를 입력받는다. 집계 규칙(Tokenizer)으로 단어 하나가 될 때까지 다시 입력 */
    public String readWord() {
        while (true) {
            List<String> tokens = Tokenizer.tokenize(readLine("찾을 단어 > "));
            if (tokens.size() == 1) {
                return tokens.get(0);
            }
            System.out.println("단어 하나를 입력하세요");
        }
    }
}
