package method_override;
public class Animal {
    String name;

    Animal(String s1) {
        this.name = s1;
    }

    void speak() {
        System.out.println(this.name + "은 나오면 안돼.");
    }
}