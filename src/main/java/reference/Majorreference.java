package reference;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.MajorService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Majorreference implements MajorService {
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
    public int addMajor(String name, int departmentId) {
        int result=0;
        try{
            String sql = "insert into Semester (majorName, department) VALUES (?,?)";
            preparedStatement=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2,departmentId);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                result=generatedKeys.getInt(1);

        }catch(Exception e){
            throw new IntegrityViolationException();
        }
        return result ;
    }

    @Override
    public void removeMajor(int majorId) {
        try{
            String sql = "delete from Semester where majorId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new EntityNotFoundException();

        }
    }

    @Override
    public List<Major> getAllMajors() {
        List<Major>result=new ArrayList<Major>();
        try{
            String sql = "select * from Major";
            preparedStatement=con.prepareStatement(sql);
            ResultSet  resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Major demo=new Major();
                demo.id=resultSet.getInt(1);
                demo.name=resultSet.getString(2);
                demo.department=(new Departmentreference()).getDepartment(resultSet.getInt(3));
                result.add(demo);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Major getMajor(int majorId) {
        try{
            String sql = "select * from Major where majorId =?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,majorId);
            ResultSet  resultSet = preparedStatement.executeQuery();
                Major demo=new Major();
                while (resultSet.next()) {
                    demo.id = resultSet.getInt(1);
                    demo.name = resultSet.getString(2);
                    demo.department = (new Departmentreference()).getDepartment(resultSet.getInt(3));
                }
                return  demo;

        }catch(SQLException e){
            throw new EntityNotFoundException();
        }

    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try{
            String sql = "insert into couseTypeToMajor (majorId, courseId,courseType) VALUES (?,?,2)";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,majorId);
            preparedStatement.setString(2,courseId);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try{
            String sql = "insert into couseTypeToMajor (majorId, courseId,courseType) VALUES (?,?,3)";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,majorId);
            preparedStatement.setString(2,courseId);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new IntegrityViolationException();
        }
    }
}
