package reference;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSearchEntry;
import cn.edu.sustech.cs307.dto.CourseTable;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.StudentService;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class studentServive implements StudentService {
    static Connection con;
    private PreparedStatement preparedStatement;
    static {
        try {
            con = SQLDataSource.getInstance().getSQLConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try{
            String sql = "insert into student(firstName, lastName, enrolledDate, majorId)value (?,?,?,?)";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2,firstName);
            preparedStatement.setString(3,lastName);
            preparedStatement.executeUpdate();

        }catch(Exception e){
            throw new IntegrityViolationException();

        }
    }

    @Override
    public List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid, @Nullable String searchName, @Nullable String searchInstructor, @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime, @Nullable List<String> searchClassLocations, CourseType searchCourseType, boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed, boolean ignoreMissingPrerequisites, int pageSize, int pageIndex) {



        return null;
    }

    /**
     *
     * @param studentId
     * @param sectionId the id of CourseSection
     * @return
     */
    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId) {

        return null;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException {
        try{
            String sql = "select grade from enrollcourse\n" +
                    "where sectionId= ?and studentId= ?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, sectionId);
            preparedStatement.setInt(2,studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            int grade=0;
            while (resultSet.next()){
                grade=resultSet.getInt("grade");
            }
            if (grade!=0){
                try{
                    String sql1 = "delete from enrollcourse\n" +
                            "where studentId= ? and sectionId=?";
                    preparedStatement=con.prepareStatement(sql1);
                    preparedStatement.setInt(1, studentId);
                    preparedStatement.setInt(2,sectionId);
                    preparedStatement.executeUpdate();

                }catch(SQLException e){
                }

            }else {
                throw new IllegalStateException();
            }

        }catch(Exception e){
            throw new EntityNotFoundException();

        }

    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {

    }

    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        return null;
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        return null;
    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        return false;
    }

    @Override
    public Major getStudentMajor(int studentId) {
        return null;
    }
}
