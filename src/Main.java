import java.io.*;

public class Main {
    public static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static EmployeeList list;

    public static void main(String[] args) throws IOException {
        list = Menu.createList();
        Menu.chooseOptions();
        reader.close();
    }
}
