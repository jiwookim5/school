package method_override;
public class Cat extends Animal {
    Cat(String s1) {
        super(s1);
    }

    @Override 
    void speak () {
        System.out.println(this.name + "가 짖습니다.");
    }
}