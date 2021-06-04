package reference;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Userreference implements UserService {
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
    public void removeUser(int userId) {
        try{
            String sql = "delete from Users where userId=?";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new EntityNotFoundException();
        }
    }
    //TOBECHECKED
    public List<User> getAllStudents(){
        List<User>result=new ArrayList<User>();
        try{
            String sql = "select userId, firstName, lastName , enrolledDate,  majorName,Major.majorId, departmentName,department.departmentId from student,major,department where Student.majorId=Major.majorId and Major.department=Department.departmentId;";
            preparedStatement=con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            String lastname;
            String firstname;
            while (resultSet.next()) {
                firstname=resultSet.getString("firstName");
                lastname=resultSet.getString("lastname");

                User user=new Student();
                user.id=resultSet.getInt(" userId");
                if (judge(firstname,lastname)){
                    user.fullName=firstname+" "+lastname;
                }else {
                    user.fullName=firstname+lastname;
                }
                ((Student)user).enrolledDate=resultSet.getDate("enrolledDate");
                ((Student)user).major=new Major();
                ((Student)user).major.id=resultSet.getInt("Major.majorId");
                ((Student)user).major.department=new Department();
                ((Student)user).major.name=resultSet.getString("majorName");
                ((Student)user).major.department.id=resultSet.getInt("department.departmentId ");
                ((Student)user).major.department.name=resultSet.getString("departmentName");
                result.add(user);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    //TOBECHECKED
    public List<User> getAllInstructors(){
        List<User>result=new ArrayList<User>();
        try{
            String sql = "select userId, firstName,lastName from instructor;";
            preparedStatement=con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            String lastname;
            String firstname;
            while (resultSet.next()) {
                User user=new Instructor();
                firstname=resultSet.getString("firstName");
                lastname=resultSet.getString("lastname");
                if (judge(firstname,lastname)){
                    user.fullName=firstname+" "+lastname;
                }else {
                    user.fullName=firstname+lastname;
                }
                user.id=resultSet.getInt(1);
                user.fullName=resultSet.getString(2);

                result.add(user);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    //TOBECHECKED
    @Override
    public List<User> getAllUsers() {
        List<User>result=new ArrayList<User>();
        result.addAll(getAllInstructors());
        result.addAll(getAllStudents());
        return result;
    }
    //TOBECHECKED
    @Override
    public User getUser(int userId) {
        User user=null;
        try{
            String sql = "SELECT p.relname FROM users c,pg_class p WHERE c.userid =? and c.tableoid = p.oid;";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String table=null;
            if (resultSet.next()){
                table=resultSet.getString(1);
            }
           if(table.equals("student")){
               sql = "select userId, firstName, lastName as fullname, enrolledDate,  majorName,Major.majorId, departmentName,department.departmentId from student,major,department where Student.majorId=Major.majorId and Major.department=Department.departmentId and userid=?;";
               preparedStatement=con.prepareStatement(sql);
               preparedStatement.setInt(1,userId);
               resultSet = preparedStatement.executeQuery();
               while (resultSet.next()) {
                   user=new Student();
                   String firstname=resultSet.getString("firstName");
                   String lastname=resultSet.getString("lastname");
                   user.id=resultSet.getInt("userId");
                   user.fullName=getFullname(firstname,lastname);
                   ((Student)user).enrolledDate=resultSet.getDate("enrolledDate");
                   ((Student)user).major=new Major();
                   ((Student)user).major.id=resultSet.getInt("Major.majorId");
                   ((Student)user).major.name=resultSet.getString("majorName");
                   ((Student)user).major.department=new Department();
                   ((Student)user).major.department.id=resultSet.getInt("department.departmentId");
                   ((Student)user).major.department.name=resultSet.getString("departmentName");
                    return user;
               }
           }else{
               sql = "select userId, firstName||lastName from instructor where userid=?;";
               preparedStatement=con.prepareStatement(sql);
               preparedStatement.setInt(1,userId);
               resultSet = preparedStatement.executeQuery();
               while (resultSet.next()) {
                   user=new Instructor();
                   user.id=resultSet.getInt(1);
                   user.fullName=resultSet.getString(2);
                   return user;
               }
           }
     

            return  user;
        }catch(Exception e){
//            e.printStackTrace();
            throw new EntityNotFoundException();
        }

    }
    public static String getFullname(String firstname,String lastname){
        if (judge(firstname, lastname)){
            return firstname+" "+lastname;
        }else {
            return firstname+lastname;
        }
    }
    public static  boolean judge(String firstname,String lastname){
        boolean isWord1=firstname.matches("[a-zA-Z]+");
        boolean isWord2=lastname.matches("[a-zA-Z]+");
        return isWord1&isWord2;
    }
/*    @Override
    public User getUser(int userId) {
        User user=null;
        try{
            String sql = "SELECT p.relname FROM users c,pg_class p WHERE c.userid =? and c.tableoid = p.oid;";
            preparedStatement=con.prepareStatement(sql);
            preparedStatement.setInt(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String table=null;
            if (resultSet.next()){
                table=resultSet.getString(1);
            }
            con.setAutoCommit(false);//将自动提交关闭
            PreparedStatement pstmt = con.prepareStatement("select  fun_name(?) ");

            pstmt.setString(1, table);
            resultSet=pstmt.executeQuery();
            String Mycurfor=null;
            if (resultSet.next()){
                Mycurfor=resultSet.getString(1);

            }
            pstmt = con.prepareStatement("fetch all in \""+Mycurfor+"\"");
            resultSet=pstmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            if (resultSet.next()){
                int numOfCols = rsmd.getColumnCount();
                if (numOfCols == 5) {
                    user = new Student();
                    user.id = resultSet.getInt("userid");
                    user.fullName=resultSet.getString("firstname")+resultSet.getString("lastname");
                    ((Student)user).enrolledDate=resultSet.getDate("enrolleddate");
                    ((Student)user).major=new Major();
                    ((Student)user).major.id=resultSet.getInt(5);
                    ((Student)user).major.department=new Department();
                    ((Student)user).major.department.id=resultSet.getInt(7);
                    ((Student)user).major.department.name=resultSet.getString(6);
                }
            }
            pstmt.close();


            con.commit();//执行完后，手动提交事务
            con.setAutoCommit(true);//再把自动提交打开，避免影响其他需要自动提交的操作
            return  user;
        }catch(SQLException e){
//            e.printStackTrace();
            throw new EntityNotFoundException();
        }

    }*/
}
