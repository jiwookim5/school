package method_override;
public class Main {
    public static void main(String[] args) {
        Animal[] animals = {
            new Dog("뽀삐"),
            new Cat("나비"),
            new Dog("초코"),
            new Cat("루나")
        };

        for (Animal a : animals) {
            a.speak();
        }
    }
}