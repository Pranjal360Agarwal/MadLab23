
import java.io.*;
class Geek {
    // data members of the class.
    String name;
    int id;
    Geek(String name, int id)
    {
        this.name = name;
        this.id = id;
    }
}
class GFG {
    public static void main(String[] args)
    {
        // This would invoke the parameterized constructor.
        Geek geek1 = new Geek("avinash", 68);
        System.out.println("GeekName :" + geek1.name  + " and GeekId :" + geek1.id);
    }
}