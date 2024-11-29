import java.io.Serializable;

class Person implements Serializable {
    private String fullName;
    private String id;
    private String gender;
    private String homeProvince;
    private String dob;
    public Person(String fullName, String id, String gender, String homeProvince, String dob) {
        this.fullName = fullName;
        this.id = id;
        this.gender = gender;
        this.homeProvince = homeProvince;
        this.dob = dob;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getHomeProvince() {
        return homeProvince;
    }

    public String getDob() {
        return dob;
    }
}
