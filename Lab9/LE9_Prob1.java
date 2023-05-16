import java.util.Arrays;
import java.util.regex.*;
class XYZ{
public static void main(String[] args) {
String result="prateek h@a.in a pand123eyprat@yahoo.com kjjkjk@jk.in";
String arr[]=result.split(" ");
System.out.println(Arrays.toString(arr));
Pattern p=Pattern.compile("([A-Za-z0-9]+@[A-Za-z0-9]+[.].{2,3})");
Matcher m=p.matcher(result);
while(m.find())
System.out.println(m.group(1));
}
}