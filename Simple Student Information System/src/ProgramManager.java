import java.util.List;

public class ProgramManager {

    private List<Program> programs;

    public ProgramManager(List<Program> programs) {
        this.programs = programs;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    //to add a new program
    public String addProgram(Program newProgram, CollegeManager collegeManager) {
        if (newProgram.getCode() == null || newProgram.getCode().trim().isEmpty() ||
                newProgram.getName() == null || newProgram.getName().trim().isEmpty() ||
                newProgram.getCollege() == null) {
            return "All program fields are required.";
        }

        // Validate that the college actually exists
        boolean collegeExists = collegeManager.getColleges().stream()
                .anyMatch(c -> c.getCode().equalsIgnoreCase(newProgram.getCollege()));

        if (!collegeExists) {
            return "The selected College does not exist.";
        }

        boolean found = false;
        for (Program p : programs) {
            if (p.getCode().equalsIgnoreCase(newProgram.getCode())) {
                // UPSERT: Match found, update details
                p.setName(newProgram.getName());
                p.setCollege(newProgram.getCollege());
                found = true;
                break;
            }
        }

        if (!found) {
            // if no match found, add as new
            programs.add(newProgram);
        }

        csvReader.saveProgram(programs);
        return null;
    }

//    public boolean updateProgram(String code, String newName, String newCollegeCode, CollegeManager collegeManager) {
//
//        Program program = findProgram(code);
//
//        if (program == null) {
//            System.out.println("Program not found.");
//            return false;
//        }
//
//        // Validate new college code
//        if (!collegeManager.collegeExists(newCollegeCode)) {
//            System.out.println("Invalid college code.");
//            return false;
//        }
//
//        program.setName(newName);
//        program.setCode(newCollegeCode);
//
//        csvReader.saveProgram(programs);
//        return true;
//    }


    public boolean deleteProgram(String code,
                                 StudentManager studentManager) {

        // Prevent deleting if used by student
        if (studentManager.isProgramUsed(code)) {
            System.out.println("Cannot delete. Program used by student.");
            return false;
        }

        Program program = findProgram(code);

        if (program == null) {
            System.out.println("Program not found.");
            return false;
        }

        programs.remove(program);
        csvReader.saveProgram(programs);
        return true;
    }

    // finds program
    public Program findProgram(String code) {

        for (Program p : programs) {
            if (p.getCode().equals(code)) {
                return p;
            }
        }

        return null;
    }

//    // checks if program does exist
//    public boolean programExists(String code) {
//
//        for (Program p : programs) {
//            if (p.getCode().equals(code)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    //
    // check if college is used
    public boolean isCollegeUsed(String collegeCode) {

        for (Program p : programs) {
            if (p.getCollege().equalsIgnoreCase(collegeCode)) {
                return true;
            }
        }

        return false;
    }
}