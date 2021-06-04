package reference;

import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.dto.User;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) throws ParseException {
      //  Userreference u=new Userreference();
        //User u2=u.getUser(2);
        //System.out.println(u.getUser(4).id);
        //System.out.println("de");
//        Instructorreference demo=new Instructorreference();
//       // demo.addInstructor(1,"o","0");
//        System.out.println(demo.getInstructedCourseSections(1,1).size());
//          Semesterreference demo2=new Semesterreference();
////        Scanner reader = new Scanner(System.in);
//         String str = "2001-03-03";
//         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//         java.util.Date date = (java.util.Date) dateFormat.parse(str);
//         String str1 = "2001-04-31";
//         DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
//         java.util.Date date1 = (java.util.Date) dateFormat.parse(str);
//         java.sql.Date dayDateSql = new java.sql.Date(date.getTime());
//         java.sql.Date dayDateSql2 = new java.sql.Date(date1.getTime());
//         System.out.println(demo2.addSemester("23",dayDateSql,dayDateSql2));
//        System.out.println(demo2.getSemester(1).name);
//       // demo2.removeSemester(1);
//        System.out.println(demo2.getAllSemesters().size());
//        Majorreference majorreference=new Majorreference();
 //      System.out.println( majorreference.getAllMajors().size());
//       Departmentreference departmentreference=new Departmentreference();
//       System.out.println(departmentreference.addDepartment("SES"));
          coursereference coursereference=new coursereference();
//        System.out.println(coursereference.addCourseSection("cs207",2,"chinese",60));
//        Instructorreference instructorreference=new Instructorreference();
//        instructorreference.addInstructor(1,"yueming","zhu");
//        List<Short>week=new ArrayList<>();
//        week.add((short) 1);
//        week.add((short) 2);
//       System.out.println(coursereference.addCourseSectionClass(2,1, DayOfWeek.WEDNESDAY,week,(short)2,(short)3,"liy"));
//        System.out.println(coursereference.getCourseSectionsInSemester("cs207",2).get(0).name);
//        System.out.println(coursereference.getCourseSectionByClass(1).name);
//        System.out.println(coursereference.getCourseSectionClasses(2).get(0).id);
       // coursereference.addCourse("cs207","sjk",3,3, Course.CourseGrading.PASS_OR_FAIL,null);
        //coursereference.addCourse("cs308","sf",3,3, Course.CourseGrading.PASS_OR_FAIL,null);
        System.out.println(coursereference.getAllCourses().get(0).name+coursereference.getAllCourses().get(1).name);
    }
}
