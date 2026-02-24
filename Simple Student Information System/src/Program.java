public class Program {

    private String code;
    private String name;
    private String college;

    public Program(String code, String name, String college){
        this.code=code;
        this.name=name;
        this.college=college;
    }

    public String toString() {
        return code + "," + name + "," + college;
    }

    public String getName() {
        return name;
    }
    public String getCode() {
        return code;
    }
    public String getCollege(){
        return college;
    }

    void setCode(String code){
        this.code=code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
