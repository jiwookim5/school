public class Person {
    String name;
    int age;

    Person() {
        System.out.println("Person() 기본 생성자");
        this.name = "이름없음";
        this.age = 0;
    }

    Person(String name, int age) {
        System.out.println("Person(String name, int age) 생성자");
        this.name = name;
        this.age = age;
    }

    void printInfo() {
        System.out.println("이름 : " + name + ", 나이 : " + age);
    }
}
