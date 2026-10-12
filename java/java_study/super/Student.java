public class Student extends Person {
    String major;

    Student() {
        super();
        System.out.println("Student() 생성자");
        this.major = "미정";
    }

    Student(String name, int age, String major) {
        super(name, age);
        System.out.println("Student(String, int, String) 생성자");
        this.major = major;
    }

    void printStudent() {
        System.out.println("전공 : " + major);
    }
}
