import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

public class Menu {
    public static EmployeeList createList() {
        System.out.println("Please, enter 0, if you want create new file or enter XML path");
        File path;
        while (true) {
            try {
                String answer = Main.reader.readLine();
                if (answer.equals("0"))
                    return new EmployeeList();
                path = new File(answer);
                if (path.isFile() && path.toPath().toString().toLowerCase().endsWith(".xml"))
                    break;
                else
                    System.out.println("Wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new EmployeeList(lineProcessing(".xml").getPath());
    }

    public static void chooseOptions() {
        ArrayList<String> options = new ArrayList<>();
        Collections.addAll(options, "Add new employee, Delete employee, Change post, Appoint manager, Sort by last name, Sort by employment date, Write to XML, Show list, Show manager subs, Exit".split(", "));
        System.out.println("\nSelect option:");
        for (String value : options) {
            System.out.println(">" + value);
        }

        String option;
        mainLoop:
        while (true) {
            try {
                option = Main.reader.readLine().toLowerCase();
                for (String value : options) {
                    if (value.toLowerCase().equals(option))
                        break mainLoop;
                }
                System.out.println("Wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (option) {
            case "add new employee" -> addNewEmployee();
            case "delete employee" -> deleteEmployee();
            case "change post" -> changePost();
            case "appoint manager" -> appointManager();
            case "sort by last name" -> sortByLastName();
            case "sort by employment date" -> sortByEmploymentDate();
            case "write to xml" -> writeToXML();
            case "show list" -> showList();
            case "show manager subs" -> showManagerSubs();
            case "exit" -> System.exit(0);
        }
        chooseOptions();
    }

    private static void addNewEmployee() {
        System.out.println("Please, enter TXT path");

        try {
            Main.list.add(lineProcessing(".txt").getPath());
            System.out.println("Employee added");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void deleteEmployee() {
        System.out.println("Please, enter employee name");

        Employee employee = lineProcessing();

        Main.list.delete(employee);

        System.out.println("Employee deleted");
    }

    private static void changePost() {
        System.out.println("Please, enter employee name");

        Employee employee = lineProcessing();

        System.out.println("Please, enter new post");

        String post;
        while (true) {
            try {
                post = Main.reader.readLine();
                if (post.equals("worker") || post.equals("manager") || post.equals("other"))
                    break;
                else
                    System.out.println("Wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (post) {
            case "worker" -> employee.makeWorker();
            case "manager" -> employee.makeManager();
            case "other" -> {
                System.out.println("Enter description");
                try {
                    employee.makeOtherEmp(Main.reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Position changed");
    }

    private static void appointManager() {
        System.out.println("Please, enter employee name");

        Employee worker = lineProcessing();

        System.out.println("Please, enter manager name");

        Employee manager = lineProcessing();

        if (worker.equals(manager)) {
            System.out.println("You can't assign himself");
            appointManager();
        } else if (worker.getHead() != null) {
            if (worker.getHead().equals(manager)) {
                System.out.println("Employee already assigned to this manager");
            } else {
                System.out.println("This employee already have manager. Do you want to change?");
                String answer;
                while (true) {
                    try {
                        answer = Main.reader.readLine();
                        if (answer.equals("yes")) {
                            worker.getHead().getManagerSubs().remove(worker);
                            worker.setHead(manager);
                            System.out.println("Employee assigned");
                            break;
                        } else if (answer.equals("no"))
                            break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else if (manager.isManager()) {
            worker.setHead(manager);
            System.out.println("Employee assigned");
        } else {
            System.out.println("This is not a manager");
            appointManager();
        }
    }

    private static void sortByLastName() {
        Main.list.sortByName();
        System.out.println("Sorted");
    }

    private static void sortByEmploymentDate() {
        Main.list.sortByEmploymentDay();
        System.out.println("Sorted");
    }

    private static void writeToXML() {
        System.out.println("Please, enter XML path");

        try {
            Main.list.writeXML(lineProcessing(".xml").getPath());
            System.out.println("File saved");
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void showList() {
        System.out.println(Main.list.getEmployees().toString());
    }

    private static void showManagerSubs() {
        System.out.println("Please, enter manager name");
        Employee manager = lineProcessing();
        if (manager.isManager())
            System.out.println(manager.getManagerSubs().toString());
    }

    private static File lineProcessing(String extension) {
        File path;
        while (true) {
            try {
                path = new File(Main.reader.readLine());
                if (path.isFile() && path.toPath().toString().toLowerCase().endsWith(extension))
                    break;
                else
                    System.out.println("Wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    private static Employee lineProcessing() {
        Employee employee;
        mainLoop:
        while (true) {
            try {
                String name = Main.reader.readLine();
                for (Employee value : Main.list.getEmployees()) {
                    if (value.getName().equals(name)) {
                        employee = value;
                        break mainLoop;
                    }
                }
                System.out.println("Wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return employee;
    }
}
