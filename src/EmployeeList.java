import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.util.*;

public class EmployeeList {
    private List<Employee> employees = new ArrayList<>();

    public EmployeeList() {}

    public EmployeeList(String pathXML) {
        try {
            readXML(pathXML);
        } catch (ParserConfigurationException | IOException | SAXException | ParseException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void add(String path) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String name = null;
        Date birthdate = null;
        Date employmentDate = null;
        String description = null;
        Map<String, String> lines = new HashMap<>();
        String line = reader.readLine();
        while (line != null) {
            String[] values = line.split("=");
            lines.put(values[0], values[1]);
            line = reader.readLine();
        }

        for (Map.Entry<String, String> value : lines.entrySet()) {
            if (value.getKey().equals("name"))
                name = value.getValue();
            if (value.getKey().equals("birthday"))
                birthdate = Employee.format.parse(value.getValue());
            if (value.getKey().equals("employmentDate"))
                employmentDate = Employee.format.parse(value.getValue());
            if (value.getKey().equals("description"))
                description = value.getValue();
        }

        Employee employee = new Employee(name, birthdate, employmentDate);
        if (description != null)
            employee.setDescription(description);
        employees.add(employee);
    }

    public void delete(Employee employee) {
        employee.getHead().getManagerSubs().remove(employee);
        employees.remove(employee);
    }

    public void sortByName() {
        List<String> names = new ArrayList<>();
        List<Employee> correct = new ArrayList<>();
        for (Employee employee : employees) {
            names.add(employee.getName());
        }
        Collections.sort(names);

        for (String name : names) {
            for (Employee employee : employees) {
                if (name.equals(employee.getName()))
                    correct.add(employee);
            }
        }

        employees = correct;
    }

    public void sortByEmploymentDay() {
        List<Date> dates = new ArrayList<>();
        List<Employee> correct = new ArrayList<>();
        for (Employee employee : employees) {
            dates.add(employee.getEmploymentDate());
        }
        Collections.sort(dates);

        for (Date date : dates) {
            for (Employee employee : employees) {
                if (date.equals(employee.getEmploymentDate()))
                    correct.add(employee);
            }
        }
        employees = correct;
    }

    private void readXML(String pathXML) throws ParserConfigurationException, IOException, SAXException, ParseException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(pathXML));
        NodeList employeeElements = document.getDocumentElement().getElementsByTagName("employee");

        for (int i = 0; i < employeeElements.getLength(); i++) {
            Node employee = employeeElements.item(i);
            NamedNodeMap attributes = employee.getAttributes();

            String name = attributes.getNamedItem("name").getNodeValue();
            Date birthday = Employee.format.parse(attributes.getNamedItem("birthday").getNodeValue());
            Date employmentDate = Employee.format.parse(attributes.getNamedItem("employmentDate").getNodeValue());

            if (!attributes.getNamedItem("post").getNodeValue().equals("manager")) {
                if (attributes.getNamedItem("post").getNodeValue().equals("other")) {
                    Employee empOth = new Employee(name, birthday, employmentDate);
                    empOth.makeOtherEmp(attributes.getNamedItem("description").getNodeValue());
                    employees.add(empOth);
                } else
                    employees.add(new Employee(name, birthday, employmentDate));
            }
        }

        for (int i = 0; i < employeeElements.getLength(); i++) {
            Node employee = employeeElements.item(i);
            NamedNodeMap attributes = employee.getAttributes();

            String name = attributes.getNamedItem("name").getNodeValue();
            Date birthday = Employee.format.parse(attributes.getNamedItem("birthday").getNodeValue());
            Date employmentDate = Employee.format.parse(attributes.getNamedItem("employmentDate").getNodeValue());

            if (attributes.getNamedItem("post").getNodeValue().equals("manager")) {
                Employee employeeMNG = new Employee(name, birthday, employmentDate);
                employeeMNG.makeManager();
                if (attributes.getNamedItem("subordinates").getNodeValue() != null) {
                    String[] names = attributes.getNamedItem("subordinates").getNodeValue().split(", ");
                    for (Employee employee1 : employees) {
                        for (String name1 : names) {
                            if (employee1.getName().equals(name1))
                                employee1.setHead(employeeMNG);
                        }
                    }
                }
                employees.add(employeeMNG);
            }
        }
    }

    public void writeXML(String pathXML) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element rootElement = document.createElement("EmployeesList");
        document.appendChild(rootElement);
        for (Employee employee : employees) {
            rootElement.appendChild(setEmployeeToXML(document, employee));
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult file = new StreamResult(pathXML);

        transformer.transform(source, file);
    }

    private Element setEmployeeToXML(Document document, Employee employee) {
        Element result = document.createElement("employee");

        if (employee.isManager()) {
            StringBuilder subs = new StringBuilder();
            for (Employee employee1 : employee.getManagerSubs()) {
                subs.append(employee1.getName()).append(", ");
            }
            result.setAttribute("subordinates", subs.toString());
            result.setAttribute("post", "manager");
        } else if (employee.isOtherEmployee()) {
            result.setAttribute("description", employee.getDescription());
            result.setAttribute("post", "other");
        } else
            result.setAttribute("post", "worker");

        result.setAttribute("employmentDate", Employee.format.format(employee.getEmploymentDate()));
        result.setAttribute("birthday", Employee.format.format(employee.getBirthday()));
        result.setAttribute("name", employee.getName());

        if (employee.isOtherEmployee())
            result.setAttribute("description", employee.getDescription());
        return result;
    }
}
