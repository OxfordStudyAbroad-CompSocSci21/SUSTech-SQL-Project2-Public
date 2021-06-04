package reference;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.InstructorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Instructorreference implements InstructorService {
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
    public void addInstructor(int userId, String firstName, String lastName) {
        try{
            String sql = "insert into instructor (userid,firstname, lastname) values (?,?,?);";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2,firstName);
            preparedStatement.setString(3,lastName);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new IntegrityViolationException();

        }

    }

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) {
        List<CourseSection>result=new ArrayList<>();
        String sql = "select c.sectionid,sectionname,totalcapacity,leftcapacity from coursesection" +
                "                inner join coursesectionclass c on coursesection.sectionid = c.sectionid" +
                "                where semesterid= ? and c.instructor=?;";
        try{
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.setInt(2,instructorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CourseSection courseSection=new CourseSection();
                courseSection.id=Integer.parseInt(resultSet.getString("sectionId"));
                courseSection.name=resultSet.getString("sectionName");
                courseSection.totalCapacity=resultSet.getInt("totalCapacity");
                courseSection.leftCapacity=resultSet.getInt("leftCapacity");
                result.add(courseSection);
            }
        }catch(SQLException e){
            throw new EntityNotFoundException();
        }
        return result;

    }
}
