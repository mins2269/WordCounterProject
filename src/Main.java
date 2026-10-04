package kr.sesac.wordcounter;

import java.util.Scanner;
import kr.sesac.wordcounter.analyzer.DocumentWordAnalyzer;
import kr.sesac.wordcounter.analyzer.FileAnalyzer;

/**
 * 첫 실행용 코드입니다. TXT 내용을 읽은 뒤 TODO를 채우며 기능을 추가하세요.
 * 구현 기준은 docs/requirements.md에 있습니다.
 */
public class Main {
    public static void main(String[] args){
        // 선택 받고 전달
        run();
    }

    private static void printGuideline(){
        System.out.println("문서 단어 분석기");
        System.out.println("1. 새 분석 시작");
        System.out.println("2. 상위 N개 단어 보기");
        System.out.println("3. 특정 단어 횟수 찾기");
        System.out.println("4. 전체 결과 저장");
        System.out.println("5. 최근 분석 요약 보기");
        System.out.println("0. 종료");
    }

    private static void run(){
        Scanner sc = new Scanner(System.in);
        FileAnalyzer fileAnalyzer = new FileAnalyzer();

        DocumentWordAnalyzer dwa = new DocumentWordAnalyzer(fileAnalyzer);

        boolean isRunning = true;

        while(isRunning) {
            printGuideline();
            System.out.print("선택 > ");
            String input = sc.nextLine().trim();
            // 정상 입력 범위를 넘으면 곧바로 다음 반복으로 넘어간다.
            if (!input.matches("[0-5]")) {
                System.out.println("정상 입력 범위는 0~5입니다.");
                continue;
            }
            // 정상 입력 범위를 받았을 때
            int num = Integer.parseInt(input);
            isRunning = dwa.numCheck(num);
        }
    }
}
