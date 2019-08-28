package pl.dg.batch.POJO;

import java.util.List;
import java.util.Map;

public class User {
    private String name = "";
    private String surname = "";
    private String age = "";
    private Map<String, List<String>> contacts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Map<String, List<String>> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, List<String>> contacts) {
        this.contacts = contacts;
    }
}
