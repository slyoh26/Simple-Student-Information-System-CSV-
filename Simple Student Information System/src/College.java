public class College {

    private String code;
    private String name;

    public College(String code, String name){
        this.code=code;
        this.name=name;
    }
    public String toString() {
        return code + "," + name;
    }
    String getCode(){
        return code;
    }
    String getName(){
        return name;
    }

    void setCode(String code){
        this.code=code;
    }

    public void setName(String name) {
        this.name = name;
    }

}
