package reference;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class prerequistTest {
    public static void main(String[] args) {
        coursereference c = new coursereference();
        Prerequisite A = new CoursePrerequisite("A");
        Prerequisite B = new CoursePrerequisite("B");
        Prerequisite C = new CoursePrerequisite("C");
        Prerequisite D = new CoursePrerequisite("D");
        Prerequisite E = new CoursePrerequisite("E");
        Prerequisite F = new CoursePrerequisite("F");
        List<Prerequisite> l3 = new ArrayList<>();
        l3.add(E);
        l3.add(F);
        Prerequisite and2 = new AndPrerequisite(l3);
        List<Prerequisite> l2 = new ArrayList<>();
        l2.add(C);
        l2.add(D);
        l2.add(and2);
        Prerequisite or = new OrPrerequisite(l2);
        List<Prerequisite> l1 = new ArrayList<>();
        l1.add(A);
        l1.add(B);
        l1.add(or);
        Prerequisite and1 = new AndPrerequisite(l1);
        c.addCourse("cs207", "jsj", 4, 64, Course.CourseGrading.PASS_OR_FAIL, and1);
/*        // 按指定模式在字符串查找
        String line = "CS307|CS102|(CS101z&CS204)";
        String pattern = "[A-Z0-9a-z]{2,}";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        while (m.find( )) {
            System.out.println("Found value: " + m.group(0) );
        }*/
    }
}