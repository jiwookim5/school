public class Main {
    public static void main(String[] args) {
        System.out.println("=== super() 예제 ===\n");

        System.out.println("▶ Student s1 = new Student();");
        Student s1 = new Student();
        System.out.println();

        System.out.println("▶ Student s2 = new Student(\"김지우\", 20, \"컴퓨터과학\");");
        Student s2 = new Student("김지우", 20, "컴퓨터과학");
        System.out.println();

        System.out.println("▶ s2 정보 출력:");
        s2.printInfo();
        s2.printStudent();
    }
}
