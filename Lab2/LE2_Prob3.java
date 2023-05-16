public class Application {
    public static void main(String[] args) {
        Mother m1 = new Child();
        m1.show();
    }
}
public class Mother {
    public static void show() {
        System.out.println("Mother's static show() method");
    }
}
public class Child extends Mother {
    public static void show() {
        System.out.println("Child's static show() method");
    }
}