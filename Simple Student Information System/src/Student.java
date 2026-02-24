public class Student {
    private String id;
    private String firstName;
    private String lastName;
    private String programCode;
    private int year;
    private String sex;

    public Student(String id, String firstName,String lastName, String programCode, int year,String sex){
        this.id=id;
        this.firstName=firstName;
        this.lastName=lastName;
        this.year=year;
        this.sex=sex;
        this.programCode=programCode;
    }
    public String toString() {
        return id + " " + firstName + " " + lastName + " " +
                programCode + " " + year + " " + sex;
    }
    public String getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProgramCode() {
        return programCode;
    }

    public String getSex() {
        return sex;
    }

    void setId(String id){
        this.id=id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
