package reference;

import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.DepartmentService;
import cn.edu.sustech.cs307.database.SQLDataSource;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
public class Departmentreference implements DepartmentService {
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
    public int addDepartment(String name) {
        try {
            String sql = "insert into Department (departmentName) " +
                    "values (?)";
            preparedStatement = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            int result = preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            int result2 = 0;
            while (generatedKeys.next()) {
                result2 = generatedKeys.getInt(1);
            }
//            String resultsql = "select departmentId from Department where departmentName=?";
//            preparedStatement = con.prepareStatement(resultsql);
            return result2;
        } catch (SQLException e) {
//            throw new IntegrityViolationException();
            e.printStackTrace();
        }
        return 0;


    }

    @Override
    public void removeDepartment(int departmentId) {
        try {
            String sql = "delete from Department where departmentId=?";
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Department> getAllDepartments() {
        List<Department> result = new ArrayList<Department>();
        try {
            String sql = "select * from Department";
            preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Department demo = new Department();
                demo.id = resultSet.getInt("departmentId");
                demo.name = resultSet.getString("departmentName");
                result.add(demo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Department getDepartment(int departmentId) {
        String sql = "select * from Department where departmentId =?";
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Department demo = new Department();
            while (resultSet.next()) {
                demo.id = Integer.parseInt(resultSet.getString("departmentId"));
                demo.name = resultSet.getString("departmentName");
            }
            return demo;
        } catch (SQLException e) {
            e.printStackTrace();
//            throw new EntityNotFoundException();
        }
        return null;
    }

}
