package reference;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.CourseService;
import cn.edu.sustech.cs307.service.SemesterService;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class coursereference implements CourseService {
    static Connection con;
    private PreparedStatement preparedStatement;
    static {
        try {
            con = SQLDataSource.getInstance().getSQLConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * @author: yzb
     * @param courseId represents the id of course. For example, CS307, CS309
     * @param courseName the name of course
     * @param credit the credit of course
     * @param classHour The total teaching hour that the course spends.
     * @param grading the grading type of course
     * @param prerequisite The root of a {@link cn.edu.sustech.cs307.dto.prerequisite.Prerequisite} expression tree.
     */
    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite prerequisite) {
        try{
            if (!Optional.ofNullable(prerequisite).isPresent()){
                String sql = "insert into course(courseid, coursename, credit, classhour, grading) values (?,?,?,?,?)";
                preparedStatement=con.prepareStatement(sql);
                preparedStatement.setString(1, courseId);
                preparedStatement.setString(2,courseName);
                preparedStatement.setInt(3,credit);
                preparedStatement.setInt(4,classHour);
                switch (grading){
                    case PASS_OR_FAIL :
                        preparedStatement.setInt(5,0);
                    case HUNDRED_MARK_SCORE :
                        preparedStatement.setInt(5,1);
                }

                preparedStatement.executeUpdate();

            }else {
                StringBuffer logic=new StringBuffer();
                addpre(prerequisite,logic);
                String sql = "insert into Course(COURSEID, COURSENAME, CREDIT, CLASSHOUR, GRADING, PREREQUISITE)  values (?,?,?,?,?,?)";
                preparedStatement=con.prepareStatement(sql);
                preparedStatement.setString(1, courseId);
                preparedStatement.setString(2,courseName);
                preparedStatement.setInt(3,credit);
                preparedStatement.setInt(4,classHour);
                switch (grading){
                    case PASS_OR_FAIL :
                        preparedStatement.setInt(5,0);
                    case HUNDRED_MARK_SCORE :
                        preparedStatement.setInt(5,1);
                }
                preparedStatement.setString(6,logic.toString());
                preparedStatement.executeUpdate();

            }

        }catch (Exception e){
            throw new IntegrityViolationException();
        }

    }
    public void addpre(Prerequisite prerequisite,StringBuffer logic){
        if((prerequisite instanceof AndPrerequisite)){
            logic=logic.append("(");
            for (Prerequisite para :
                    ((AndPrerequisite)prerequisite).terms) {
                addpre(para,logic);
                logic=logic.append("&");
            }
            logic.delete(logic.length()-1,logic.length());
            logic=logic.append(")");
        }else
        if((prerequisite instanceof OrPrerequisite)){
            logic=logic.append("(");
            for (Prerequisite para :
                    ((OrPrerequisite)prerequisite).terms) {

                addpre(para,logic);
                logic=logic.append("|");
            }
            logic.delete(logic.length()-1,logic.length());
            logic=logic.append(")");
        }else{
            logic=logic.append(((CoursePrerequisite)prerequisite).courseID);
//            logic=logic+"(";//TODO
        }

    }

    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {
        int result=0;
     try {
         String sql = "insert into coursesection(sectionname, totalcapacity,leftcapacity,coureseid, semesterid) values (?,?,?,?,?)";
         preparedStatement=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
         preparedStatement.setString(1, sectionName);
         preparedStatement.setInt(2,totalCapacity);
         preparedStatement.setInt(3,totalCapacity);
         preparedStatement.setString(4,courseId);
         preparedStatement.setInt(5,semesterId);
         preparedStatement.executeUpdate();
         ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
         if (generatedKeys.next()){
             result=generatedKeys.getInt(1);
         }
         return result;
     }catch(Exception e){
         throw new IntegrityViolationException();
     }
    }

    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, List<Short> weekList, short classStart, short classEnd, String location) {
        int result=0;
        try {
            String sql = "insert into courseSectionClass(sectionId,instructor,dayOfWeek,classBegin,classEnd,location)values (?,?,?,?,?,?)";
            preparedStatement=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, sectionId);
            preparedStatement.setInt(2,instructorId);
            switch (dayOfWeek) {
                case MONDAY: preparedStatement.setInt(3,1);
                case TUESDAY:preparedStatement.setInt(3,2);
                case WEDNESDAY:preparedStatement.setInt(3,3);
                case THURSDAY:preparedStatement.setInt(3,4);
                case FRIDAY:preparedStatement.setInt(3,5);
                case SATURDAY:preparedStatement.setInt(3,6);
                case SUNDAY:preparedStatement.setInt(3,7);
            }
            preparedStatement.setInt(4,classStart);
            preparedStatement.setInt(5,classEnd);
            preparedStatement.setString(6,location);

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()){
                result=generatedKeys.getInt(1);
            }
            for (int i=0;i<weekList.size();i++) {
                String sql1 = "insert into week(classId, weekindex) VALUES (?,?)";
                preparedStatement = con.prepareStatement(sql1);
                preparedStatement.setInt(1, result);
                preparedStatement.setInt(2, weekList.get(i));
                preparedStatement.executeUpdate();
            }
            return result;
        }catch(Exception e){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void removeCourse(String courseId) {
        try{
            String sql = "delete from course where courseId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setString(1, courseId);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new EntityNotFoundException();

        }

    }

    @Override
    public void removeCourseSection(int sectionId) {
        try{
            String sql = "delete from courseSection where sectionId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, sectionId);
            preparedStatement.executeUpdate();

        }catch(Exception e){
          //  e.printStackTrace();
           throw new EntityNotFoundException();

        }

    }

    @Override
    public void removeCourseSectionClass(int classId) {
        try{
            String sql = "delete from courseSectionClass where classId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, classId);
            preparedStatement.executeUpdate();

        }catch(Exception e){
           // e.printStackTrace();
            throw new EntityNotFoundException();

        }

    }
  
    @Override
    public List<Course> getAllCourses() {
        List<Course>result=new ArrayList<Course>();
        try{
            String sql = "select * from Course";
            preparedStatement=con.prepareStatement(sql);
            ResultSet  resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Course course=new Course();
                course.id=resultSet.getString(1);
                course.name=resultSet.getString(2);
                course.credit=resultSet.getInt(3);
                course.classHour=resultSet.getInt(4);
                int grade=resultSet.getInt(5);
                switch (grade){
                    case 0:
                        course.grading= Course.CourseGrading.PASS_OR_FAIL;
                    case 1:
                        course.grading= Course.CourseGrading.HUNDRED_MARK_SCORE;
                }
                result.add(course);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        List<CourseSection>result=new ArrayList<CourseSection>();
        try{
            String sql = "select * from courseSection where coureseId= ?and semesterId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setString(1, courseId);
            preparedStatement.setInt(2,semesterId);
            ResultSet  resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CourseSection courseSection=new CourseSection();
                courseSection.id=resultSet.getInt("sectionId");
                courseSection.name=resultSet.getString("sectionName");
                courseSection.leftCapacity=resultSet.getInt("leftCapacity");
                courseSection.totalCapacity=resultSet.getInt("totalCapacity");
                result.add(courseSection);
            }

        }catch(Exception e){
           // e.printStackTrace();
            throw new EntityNotFoundException();
        }
        return result;
    }

    /**
     * @author yzb //TOBECHECKED: 还没测试
     * @param sectionId if the key is non-existent, please throw an EntityNotFoundException.
     * @return
     */
    @Override
    public Course getCourseBySection(int sectionId) {
        try{
            String sql = "select courseId, courseName, credit, classHour, grading from Course join courseSection cS on Course.courseId = cS.coureseId " +
                    "where sectionId=?;";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,sectionId);
            ResultSet  resultSet1 = preparedStatement.executeQuery();
            Course course=new Course();
            while (resultSet1.next()) {
                course.id=resultSet1.getString(1);
                course.name=resultSet1.getString(2);
                course.credit=resultSet1.getInt(3);
                course.classHour=resultSet1.getInt(4);
                int grade=resultSet1.getInt(5);
                switch (grade){
                    case 0:
                        course.grading= Course.CourseGrading.PASS_OR_FAIL;
                    case 1:
                        course.grading= Course.CourseGrading.HUNDRED_MARK_SCORE;
                }

            }
            return course;

        }catch(Exception e){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        List<CourseSectionClass>result=new ArrayList<CourseSectionClass>();
        try{
            String sql = "select *from courseSectionClass\n" +
                    "join Instructor I on I.userId = courseSectionClass.instructor\n" +
                    "where sectionId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, sectionId);
            ResultSet  resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CourseSectionClass courseSectionClass=new CourseSectionClass();
                int id=resultSet.getInt("classId");
                courseSectionClass.id=id;
                courseSectionClass.classBegin= (short) resultSet.getInt("classBegin");
                courseSectionClass.classEnd= (short) resultSet.getInt("classEnd");
                int day=resultSet.getInt("dayOfWeek");
                switch (day){
                    case 1:courseSectionClass.dayOfWeek=DayOfWeek.MONDAY;
                    case 2:courseSectionClass.dayOfWeek=DayOfWeek.TUESDAY;
                    case 3:courseSectionClass.dayOfWeek=DayOfWeek.WEDNESDAY;
                    case 4:courseSectionClass.dayOfWeek=DayOfWeek.THURSDAY;
                    case 5:courseSectionClass.dayOfWeek=DayOfWeek.FRIDAY;
                    case 6:courseSectionClass.dayOfWeek=DayOfWeek.SATURDAY;
                    case 7:courseSectionClass.dayOfWeek=DayOfWeek.SUNDAY;
                }
                courseSectionClass.location=resultSet.getString("location");
                Instructor instructor=new Instructor();
                instructor.id=resultSet.getInt("userId");
                String fn=resultSet.getString("firstName");
                String ln=resultSet.getString("lastName");
                if (Character.isLetter(fn.charAt(0))&&Character.isLetter(ln.charAt(0))){
                    instructor.fullName=fn+" "+ln;
                }else {
                    instructor.fullName=fn+ln;
                }
                List<Short>weeklist=new ArrayList<>();
                try{
                    String sql1 = "select * from week where classId=?";
                    preparedStatement=con.prepareStatement(sql1);
                    preparedStatement.setInt(1,id);
                    ResultSet  resultSet1 = preparedStatement.executeQuery();
                    while (resultSet1.next()) {
                        weeklist.add((short) resultSet1.getInt("weekIndex"));
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
                result.add(courseSectionClass);
            }

        }catch(Exception e){
           // e.printStackTrace();
            throw new EntityNotFoundException();
        }
        return result;
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        try{
            String sql = "select cs.sectionId,cs.sectionName,cs.totalCapacity,cs.leftCapacity from courseSectionClass\n" +
                    "join courseSection cS on cS.sectionId = courseSectionClass.sectionId\n" +
                    "where classId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,classId);
            ResultSet  resultSet1 = preparedStatement.executeQuery();
            CourseSection courseSection=new CourseSection();
            while (resultSet1.next()) {
                courseSection.id=resultSet1.getInt(1);
                courseSection.name=resultSet1.getString(2);
                courseSection.totalCapacity=resultSet1.getInt(3);
                courseSection.leftCapacity=resultSet1.getInt(4);
            }


            return  courseSection;

        }catch(Exception e){
            throw new EntityNotFoundException();
        }
    }

    /** TODO:还没做
     * @author yzb
     * @param courseId  if the key is non-existent, please throw an EntityNotFoundException.
     * @param semesterId if the key is non-existent, please throw an EntityNotFoundException.
     * @return
     */
    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        return null;
    }
}
