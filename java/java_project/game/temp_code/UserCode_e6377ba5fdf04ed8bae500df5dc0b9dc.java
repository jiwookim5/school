class Animal {
    String name;
    public Animal(String name) {
        this.name = name;
    }
    public void speak() {
        System.out.println(name + "이 운다");
    }
}

import java.util.Scanner;
class Animal {
    String name;
    public Animal(String name) {
        this.name = name;
    }
    public void speak() {
        System.out.println(name + "이 운다");
    }
}
class Dog extends Animal {
    public Dog(String name) {
        super(name);
    }
    @Override
    public void speak() {
        System.out.println(name + "이 멍멍 운다");
    }
}

public class UserCode_e6377ba5fdf04ed8bae500df5dc0b9dc {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String dogName = sc.nextLine();
        Dog dog = new Dog(dogName);
        dog.speak();
    }
}
