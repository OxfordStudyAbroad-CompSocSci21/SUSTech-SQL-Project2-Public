package reference;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.SemesterService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Semesterreference implements SemesterService {
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
    public int addSemester(String name, Date begin, Date end) {
        int result=0;
        try{
            String sql = "insert into Semester (semestername, begintime, endtime) VALUES (?,?,?)";
            preparedStatement=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setDate(2,new java.sql.Date(begin.getTime()));
            preparedStatement.setDate(3,new java.sql.Date(end.getTime()));
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()){
                result=generatedKeys.getInt(1);
            }
            return result ;
        }catch(SQLException e){
//            e.printStackTrace();
            throw new IntegrityViolationException();
        }

    }

    @Override
    public void removeSemester(int semesterId) {
        try{
            String sql = "delete from Semester where semesterId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new EntityNotFoundException();
        }

    }

    @Override
    public List<Semester> getAllSemesters() {
        List<Semester>result=new ArrayList<Semester>();
        try{
            String sql = "select * from Semester";
            preparedStatement=con.prepareStatement(sql);
            ResultSet  resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Semester demo=new Semester();
                demo.id=resultSet.getInt(1);
                demo.name=resultSet.getString(2);
                demo.begin=resultSet.getDate(3);
                demo.end=resultSet.getDate(4);
                result.add(demo);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Semester getSemester(int semesterId) {
        String sql = "select * from Semester where semesterId =?";
        try{
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, semesterId);
            ResultSet  resultSet = preparedStatement.executeQuery();
            Semester demo = new Semester();
            while (resultSet.next()) {

                demo.id = resultSet.getInt("semesterId");
                demo.name = resultSet.getString(2);
                demo.begin = resultSet.getDate(3);
                demo.end = resultSet.getDate(4);
            }
            return  demo;
        }catch(SQLException e){
            throw new EntityNotFoundException();
        }
    }
}
