package cn.e3mall.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

public class FreeMarkerTest {

    @Test
    public void testFreeMarker() throws Exception{
        //创建一个模板文件
        //创建一个Configuration对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板文件保存的目录
        configuration.setDirectoryForTemplateLoading(new File("D:\\SSM\\e33\\e3-item-web\\src\\main\\webapp\\WEB-INF\\ftl"));
        //模板文件的编码格式，一般是UTF-8
        configuration.setDefaultEncoding("utf-8");
        //加载一个模板文件，创建一个模板对象
        Template template = configuration.getTemplate("student.ftl");
        //创建一个是数据集。可以是pojo也可以是map，推荐用map
        Map data = new HashMap<>();
        data.put("hello","hello freemarker!");
        //创建一个pojo对象
        Student student = new Student(1,"小明",18,"回龙观");
        data.put("student",student);
        //创建一个list
        List<Student> stuList = new ArrayList<>();
        stuList.add(new Student(1,"小明1",18,"回龙观"));
        stuList.add(new Student(2,"小明2",19,"回龙观"));
        stuList.add(new Student(3,"小明3",20,"回龙观"));
        data.put("stuList",stuList);
        //添加日期类型
        data.put("date",new Date());
        //null值得测试
        data.put("val",null);
        //创建一个Writer对象，指定输出文件的路径及文件名
        Writer out = new FileWriter(new File("D:\\JavaEE32\\freemarker\\student.html"));
        //生成静态页面
        template.process(data,out);
        //关闭流
        out.close();
    }
}
