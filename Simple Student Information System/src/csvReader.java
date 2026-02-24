import java.io.*;
import java.util.*;

public class csvReader {

    public static List<Student> loadStudent(){
        List<Student> students = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("students.csv"));
            String line;
            reader.readLine();

            while ((line=reader.readLine()) !=null){
                String[] data = line.split(",");

                Student s = new Student(data[0], data[1], data[2], data[3], Integer.parseInt(data[4]), data[5]);

                students.add(s);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: The file is missing!");
            throw new RuntimeException(e);
        }
        catch (IOException e){
            System.out.println("Error: The file is there, but I can't read it.");
        }
        return students;
    }

    public static List<Program> loadProgram(){
        List<Program> programs = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("program.csv"));
            String line;
            reader.readLine();

            while((line=reader.readLine())!=null){
                String[] data = line.split(",");
                Program p = new Program(data[0],data[1],data[2]);

                programs.add(p);
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error: The file is missing!");
            throw new RuntimeException(e);
        }
        catch (IOException e){
            System.out.println("Error: The file is there, but I can't read it.");
        }
        return programs;
    }

    public static List<College> loadCollege(){
        List<College> colleges = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("college.csv"));
            String line;
            reader.readLine();

            while((line=reader.readLine())!=null){
                String[] data = line.split(",");
                College c = new College (data[0],data[1]);

                colleges.add(c);
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error: The file is missing!");
            throw new RuntimeException(e);
        }
        catch (IOException e){
            System.out.println("Error: The file is there, but I can't read it.");
        }
        return colleges;
    }

    public static void saveStudents(List<Student> students) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("students.csv"))) {

            pw.println("id,firstName,lastName,programCode,year,gender");

            for (Student s : students) {
                pw.println(
                        s.getId() + "," +
                                s.getFirstName() + "," +
                                s.getLastName() + "," +
                                s.getProgramCode() + "," +
                                s.getYear() + "," +
                                s.getSex()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveProgram(List<Program> programs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("program.csv"))) {

            pw.println("code,name,college");

            for (Program p : programs) {
                pw.println(
                        p.getCode() + "," + p.getName() + "," + p.getCollege()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveColleges(List<College> colleges) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("college.csv"))) {

            pw.println("code,name");

            for (College c : colleges) {
                pw.println(c.getCode() + "," + c.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
