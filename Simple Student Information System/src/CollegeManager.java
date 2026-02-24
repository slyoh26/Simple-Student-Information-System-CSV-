import java.util.List;

public class CollegeManager {
    List<College> colleges;

    public CollegeManager(List<College> colleges){
        this.colleges=colleges;
    }

    //returns all colleges
    public List<College> getColleges() {
        return colleges;
    }

    //checks if a college already exists in the first place
//    public boolean collegeExists(String code) {
//        for (College c : colleges) {
//            if (c.getCode().equalsIgnoreCase(code)) {
//                return true;
//            }
//        }
//        return false;
//    }

    //
    public College findCollege(String code){
        for(College c : colleges){
        if (c.getCode().equals(code))
            return c;
        }
        return null;
    }


    // add college
    public String addCollege(College newCollege) {
        if (newCollege.getCode() == null || newCollege.getCode().trim().isEmpty() ||
                newCollege.getName() == null || newCollege.getName().trim().isEmpty()) {
            return "College code and name cannot be empty.";
        }

        boolean found = false;
        for (College c : colleges) {
            if (c.getCode().equalsIgnoreCase(newCollege.getCode())) {
                // UPSERT: Match found, update the name
                c.setName(newCollege.getName());
                found = true;
                break;
            }
        }

        if (!found) {
            // INSERT: No match found, add as new
            colleges.add(newCollege);
        }

        csvReader.saveColleges(colleges);
        return null; // Return null on success
    }

//    public boolean updateCollege(String code, String name){
//    College college = findCollege(code);
//    if(college==null) {
//        System.out.println("College not found");
//        return false;
//    }
//    college.setName(name);
//    csvReader.saveColleges(colleges);
//    return true;
//    }

    public boolean deleteCollege(String code, ProgramManager programManager) {

        // Prevent delete if used by program
        if (programManager.isCollegeUsed(code)) {
            System.out.println("Cannot delete. College is used by a program.");
            return false;
        }

        College college = findCollege(code);

        if (college == null) {
            System.out.println("College not found.");
            return false;
        }

        colleges.remove(college);
        csvReader.saveColleges(colleges);
        return true;
    }

}
