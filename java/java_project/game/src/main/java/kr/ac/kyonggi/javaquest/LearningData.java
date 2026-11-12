package kr.ac.kyonggi.javaquest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Static Java lesson content used by the Swing learning app. */
public final class LearningData {
    private LearningData() {
    }

    public static LearningService createDemoService() {
        LearningService service = new LearningService(
                new User("Java 탐험가", 7),
                List.of(
                        chapter(1, "변수와 자료형", "값을 저장하는 변수와 자료형을 익혀요.",
                                pairs(
                                        "int score = 10;", "값을 저장하는 변수",
                                        "void attack() { }", "객체가 수행할 동작을 정의한 코드 묶음",
                                        "package javaquest;", "관련 클래스를 묶어 관리하는 단위",
                                        "class Hero { }", "객체를 만들기 위한 설계도",
                                        "Hero hero = new Hero();", "클래스를 바탕으로 만들어진 실체",
                                        "Hero(String name) { }", "객체를 생성할 때 초기값을 설정하는 생성자",
                                        "class Mage extends Hero { }", "기존 클래스의 기능을 물려받는 것",
                                        "private int hp;", "데이터를 숨기고 접근을 제한하는 것"),
                                "int level = 1;",
                                List.of("int score = 0;", "score = score + 10;", "System.out.println(score);"),
                                "int age = 20;\nSystem.out.println(age);",
                                List.of("int age = 20;", "String name = 10;", "boolean ready = true;"), 1,
                                "변수는 자료형, 이름, 값의 순서로 선언해요."),
                        chapter(2, "조건문과 반복문", "조건에 따라 분기하고 같은 작업을 반복해요.",
                                pairs("if (score >= 60)", "조건이 참일 때 실행한다", "for (int i = 0; i < 3; i++)", "정해진 횟수만큼 반복한다", "else", "조건이 거짓일 때 실행한다"),
                                "for (int i = 0; i < 3; i++) {\n    System.out.println(i);\n}",
                                List.of("if (score >= 60) {", "System.out.println(\"PASS\");", "}"),
                                "if (ready) {\n    System.out.println(\"GO\");\n}",
                                List.of("if (ready) {", "for (int i = 0; i < 3; i++)", "if (score = 100) {"), 2,
                                "if의 조건식은 true 또는 false가 되어야 해요."),
                        chapter(3, "메소드", "반복되는 기능을 메소드로 묶고 호출해요.",
                                pairs("int sum(int a, int b)", "메소드를 선언한다", "return a + b;", "반환값을 지정한다", "sum(3, 5);", "메소드를 호출한다"),
                                "int sum(int a, int b) {\n    return a + b;\n}",
                                List.of("int add(int a, int b) {", "return a + b;", "}"),
                                "boolean ready = true;\nif (ready) {\n    System.out.println(\"GO\");\n}",
                                List.of("if (ready) {", "if (ready = true) {", "System.out.println(\"GO\");"), 1,
                                "메소드 선언에는 반환형, 이름, 매개변수가 포함돼요."),
                        chapter(4, "배열", "여러 값을 순서대로 저장하고 꺼내 써요.",
                                pairs("int[] scores = {90, 80};", "정수 배열을 만든다", "scores[0]", "첫 번째 값을 가져온다", "scores.length", "배열의 길이를 확인한다"),
                                "int[] scores = {90, 80, 70};",
                                List.of("int[] scores = {90, 80};", "for (int score : scores) {", "System.out.println(score);", "}"),
                                "int[] scores = {90, 80};\nSystem.out.println(scores[0]);",
                                List.of("scores[0]", "scores[2]", "scores.length"), 1,
                                "배열의 인덱스는 0부터 시작해요."),
                        chapter(5, "클래스", "객체의 속성과 행동을 클래스로 설계해요.",
                                pairs("class Dragon {", "클래스를 선언한다", "Dragon giryong = new Dragon();", "객체를 생성한다", "giryong.study();", "객체의 메소드를 호출한다"),
                                "class Dragon {\n    String name = \"기룡이\";\n}",
                                List.of("class Dragon {", "void study() {", "System.out.println(\"Java!\");", "}", "}"),
                                "class Dragon {\n    String name;\n}",
                                List.of("class Dragon {", "Dragon giryong = new Dragon();", "class Dragon = new Dragon();"), 2,
                                "클래스는 객체를 만들기 위한 설계도예요."),
                        chapter(6, "패키지", "관련 클래스를 패키지로 묶고 가져와 사용해요.",
                                pairs("package quest.java;", "클래스가 속한 패키지를 선언한다", "import java.util.ArrayList;", "다른 패키지의 클래스를 가져온다", "java.util.Scanner", "패키지를 포함한 클래스 이름이다"),
                                "import java.util.ArrayList;\n\nArrayList<String> quests = new ArrayList<>();",
                                List.of("package quest.java;", "import java.util.ArrayList;", "public class Quest {", "}"),
                                "package quest.java;\n\npublic class Quest {\n}",
                                List.of("import java.util.ArrayList;", "package java.quest;", "import = java.util.Scanner;"), 2,
                                "package 선언은 소스 파일의 가장 위에 한 번만 작성해요.")));
        service.loadLocalProgress();
        return service;
    }

    private static Chapter chapter(
            int number,
            String title,
            String summary,
            Map<String, String> matching,
            String typingCode,
            List<String> blocks,
            String reference,
            List<String> bugs,
            int bugAnswer,
            String hint) {
        return new Chapter(number, title, summary, List.of(
                new Mission(number, 1, GameType.MATCHING, hint, matching, "", List.of(), "", List.of(), -1),
                new Mission(number, 2, GameType.TYPING, hint, Map.of(), typingCode, List.of(), "", List.of(), -1),
                new Mission(number, 3, GameType.STACK, hint, Map.of(), "", blocks, "", List.of(), -1),
                new Mission(number, 4, GameType.HAND_CODING, hint, Map.of(), "", List.of(), "10" + number, List.of(), -1)));
    }

    private static Map<String, String> pairs(String... values) {
        LinkedHashMap<String, String> pairs = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            pairs.put(values[index], values[index + 1]);
        }
        return pairs;
    }
}
