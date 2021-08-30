import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee {
    public static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    private String name;
    private Date birthday;
    private Date employmentDate;
    private Employee head = null;
    private List<Employee> managerSubs = null;
    private String description = null;

    @Override
    public String toString() {
        return name;
    }

    public Employee(String name, Date birthday, Date employmentDate) {
        this.name = name;
        this.birthday = birthday;
        this.employmentDate = employmentDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getHead() {
        return head;
    }

    public List<Employee> getManagerSubs() {
        return managerSubs;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public boolean isManager() {
        if (managerSubs == null) {
            return false;
        }
        return true;
    }

    public boolean isOtherEmployee() {
        if (description == null) {
            return false;
        }
        return true;
    }

    public void makeWorker() {
        description = null;
        managerSubs = null;
    }

    public void makeManager() {
        if (description != null)
            description = null;

        if (head != null) {
            head.getManagerSubs().remove(this);
            head = null;
        }

        if (managerSubs == null)
            managerSubs = new ArrayList<>();
    }

    public void makeOtherEmp(String description) {
        if (managerSubs != null)
            managerSubs = null;

        if (this.description == null)
            this.description = description;
    }

    public void setHead(Employee manager) {
            manager.getManagerSubs().add(this);
            this.head = manager;
    }
}
