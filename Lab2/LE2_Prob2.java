class Mother {
    int x=2;
    void show(){
        System.out.println(x);
    }
}
class Child extends Mother{
    
}
class Application {
    public static void main(String[] args) {
        Mother m =new Mother();
        m.show();
        Child c=new Child();
        c.show();
       
    }
}