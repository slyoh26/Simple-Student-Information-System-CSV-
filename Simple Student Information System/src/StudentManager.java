import java.util.List;

public class StudentManager {

    private List<Student> students;

    public StudentManager(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

//    private boolean isValidStudentId(String id) {
//        return id.matches("^\\d{4}-\\d{4}$");
//    }

    // to add student
    public String addStudent(Student newStudent, ProgramManager programManager) {
        if (newStudent.getId() == null || newStudent.getId().trim().isEmpty() ||
                newStudent.getFirstName() == null || newStudent.getFirstName().trim().isEmpty() ||
                newStudent.getLastName() == null || newStudent.getLastName().trim().isEmpty() ||
                newStudent.getProgramCode() == null || newStudent.getSex() == null) {
            return "All student fields are required.";
        }

        // Validate that the program actually exists
        boolean programExists = programManager.getPrograms().stream()
                .anyMatch(p -> p.getCode().equalsIgnoreCase(newStudent.getProgramCode()));

        if (!programExists) {
            return "The selected Program does not exist.";
        }

        boolean found = false;
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(newStudent.getId())) {
                // UPSERT: Match found, update details
                s.setFirstName(newStudent.getFirstName());
                s.setLastName(newStudent.getLastName());
                s.setProgramCode(newStudent.getProgramCode());
                s.setYear(newStudent.getYear());
                s.setSex(newStudent.getSex());
                found = true;
                break;
            }
        }

        if (!found) {
            // INSERT: No match found, add as new
            students.add(newStudent);
        }

        csvReader.saveStudents(students);
        return null;
    }


//    public boolean updateStudent(String id,
//                                 String newProgramCode,
//                                 ProgramManager programManager) {
//
//        Student student = findStudent(id);
//
//        if (student == null) {
//            System.out.println("Student not found.");
//            return false;
//        }
//
//        // Validate program
//        if (!programManager.programExists(newProgramCode)) {
//            System.out.println("Invalid program code.");
//            return false;
//        }
//
//        student.setProgramCode(newProgramCode);
//        csvReader.saveStudents(students);
//        return true;
//    }


    public boolean deleteStudent(String id) {

        Student student = findStudent(id);

        if (student == null) {
            System.out.println("Student not found.");
            return false;
        }

        students.remove(student);
        csvReader.saveStudents(students);
        return true;
    }

    //finds student
    public Student findStudent(String id) {

        for (Student s : students) {
            if (s.getId().equals(id)) {
                return s;
            }
        }

        return null;
    }
//    //checks student exists
//    public boolean studentExists(String id) {
//
//        for (Student s : students) {
//            if (s.getId().equals(id)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    // checks if program is used

    public boolean isProgramUsed(String programCode) {

        for (Student s : students) {
            if (s.getProgramCode().equals(programCode)) {
                return true;
            }
        }

        return false;
    }
}