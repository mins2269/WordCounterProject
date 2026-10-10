package kr.sesac.wordcounter;

import kr.sesac.wordcounter.analyzer.FileAnalyzer;
import kr.sesac.wordcounter.app.ConsoleInput;
import kr.sesac.wordcounter.app.MenuController;
import kr.sesac.wordcounter.output.ResultOutput;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 객체 조립만 담당한다. Scanner는 하나만 만들어 공유한다.
        FileAnalyzer analyzer = new FileAnalyzer();
        ConsoleInput input = new ConsoleInput(new Scanner(System.in));
        ResultOutput output = new ResultOutput();

        new MenuController(analyzer, input, output).run();
    }
}
