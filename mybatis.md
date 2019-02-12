# Mybatis入门(mysql)
## 1 介绍及其流程
### 1.1 介绍
##### MyBatis 本是apache的一个开源项目iBatis, 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis，是一个基于Java的持久层框架。
##### Mybatis、Hibernate都是ORM的一种实现框架，都是对JDBC的一种封装。
### 1.2 流程
```aidl
通过Reader对象读取Mybatis映射文件
通过SqlSessionFactoryBuilder对象创建SqlSessionFactory对象
获取当前线程的SQLSession
事务默认开启
通过SQLSession读取映射文件中的操作编号，从而读取SQL语句
提交事务
关闭资源
```
## 2 快速入门配置
### 2.1 配置Maven
```aidl
<dependencies>
        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
            <version>8.0.11</version>
        </dependency>
        
        <!--mybatis-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.1.1</version>
        </dependency>
</dependencies>
```
### 2.2 配置mybatis.xml配置文件
```aidl
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 加载数据库信息属性文件 -->
    <properties resource="db.properties"/>
    <!-- 设置一个默认的连接环境信息 -->
    <environments default="mybatis_test">
        <!-- 设置一个连接数据库环境信息 -->
        <environment id="mybatis_test">
            <!-- 使用jdbc事务管理方式 -->
            <transactionManager type="jdbc"></transactionManager>
            <!-- 使用连接池方式来获取连接 -->
            <dataSource type="pooled">
                <!-- 配置数据库的属性 -->
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```
### 2.3 配置db.properties数据库连接配置信息
```aidl
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis_test?serverTimezone=UTC&characterEncoding=utf-8
username=root
password=root
```
### 2.4 写一个连接数据库的工具类MybatisUtil，并且检测是否可连接
```aidl
public class MybatisUtil {
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();
    private static SqlSessionFactory sqlSessionFactory;
    /**
     * 加载位于src/mybatis.xml配置文件
     */
    static{
        try {
            Reader reader = Resources.getResourceAsReader("Mybatis.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * 禁止外界通过new方法创建
     */
    private MybatisUtil(){}
    /**
     * 获取SqlSession
     */
    public static SqlSession getSqlSession(){
        //从当前线程中获取SqlSession对象
        SqlSession sqlSession = threadLocal.get();
        //如果SqlSession对象为空
        if(sqlSession == null){
            //在SqlSessionFactory非空的情况下，获取SqlSession对象
            sqlSession = sqlSessionFactory.openSession();
            //将SqlSession对象与当前线程绑定在一起
            threadLocal.set(sqlSession);
        }
        //返回SqlSession对象
        return sqlSession;
    }
    /**
     * 关闭SqlSession与当前线程分开
     */
    public static void closeSqlSession(){
        //从当前线程中获取SqlSession对象
        SqlSession sqlSession = threadLocal.get();
        //如果SqlSession对象非空
        if(sqlSession != null){
            //关闭SqlSession对象
            sqlSession.close();
            //分开当前线程与SqlSession对象的关系，目的是让GC尽早回收
            threadLocal.remove();
        }
    }
    /**
     * 测试
     */
    public static void main(String[] args) {
        Connection conn = MybatisUtil.getSqlSession().getConnection();
        System.out.println(conn!=null?"连接成功":"连接失败");
    }
}
```
### 2.5 创建一张表(mysql)
```aidl
create table students(
  id  int(6) primary key,
  name varchar(20),
  score double(3,1)
);
```
### 2.6 创建一个与表匹配的实体类
```aidl
public class Student {
    private Integer id;
    private String name;
    private Double score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setScore(Double score) {
        this.score = score;
    }

    public Double getScore() {
        return score;
    }
}
```
### 2.7 创建一个实体与表映射关系文件student-mapper.xml
```aidl
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="Studao">
    <!-- resultMap标签:映射实体与表 
         type属性：表示实体全路径名
         id属性：为实体与表的映射取一个任意的唯一的名字
    -->
    <resultMap id="student-mapper" type="com.mybatis_demo.entity.Student">
        <!-- id标签:映射主键属性
             result标签：映射非主键属性
             property属性:实体的属性名
             column属性：表的字段名
        -->
        <id property="id" jdbcType="INTEGER" column="id"/>
        <result property="name" jdbcType="VARCHAR" column="name"/>
        <result property="score" jdbcType="DOUBLE" column="score"/>
    </resultMap>
</mapper>
``` 
## 3 简单Mybatis增删改查
### 3.1 增加数据操作
#### 3.1.1 往映射文件<resultMap>标签外面添加该标签
```aidl
    <!--id：后面Dao层来引用调用；parameterType：参数类型-->
    <insert id="add" parameterType="com.mybatis_demo.entity.Student">
        INSERT INTO STUDENTS (ID, NAME, SCORE) VALUES (#{id},#{name},#{score});
    </insert>
```
#### 3.1.2 添加一个Dao层写一个StudentDao
```aidl
public class StudentDao {
    SqlSession sqlSession;

    public void add(Student student) throws Exception {
        try{
            sqlSession=MybatisUtil.getSqlSession();
            sqlSession.insert("Studao.add",student);
            sqlSession.commit();
        }catch(Exception ex){
            ex.printStackTrace();
            sqlSession.rollback();
            throw ex;
        }finally{
            MybatisUtil.closeSqlSession();
        }
    }
}
```
#### 3.1.3 添加一个Service层写一个StudentService,并实现插入
```aidl
public class StudentService{
    public static void add() throws Exception {
        Student student=new Student();
        student.setId(1);
        student.setName("12");
        student.setScore(12.3);
        StudentDao dao=new StudentDao();
        dao.add(student);
    }
    public static void main(String[] args) throws Exception {
        StudentService.add();
    }
}
```
### 3.2 删除数据操作
#### 3.2.1 往映射文件写好删除语句
```aidl
    <delete id="delete" parameterType="int">
       DELETE FROM STUDENTS WHERE id=#{id};
    </delete>
```
#### 3.2.2 往StudentDao添加一个方法
```aidl
    public void delete(int id) throws Exception {
        try{
            sqlSession=MybatisUtil.getSqlSession();
            sqlSession.delete("Studao.delete",id);
            sqlSession.commit();
        }catch(Exception ex){
            ex.printStackTrace();
            sqlSession.rollback();
            throw ex;
        }finally{
            MybatisUtil.closeSqlSession();
        }
    }
```
#### 3.2.3 往StudentService添加方法
```aidl
    public static void delete() throws Exception {
        StudentDao dao=new StudentDao();
        dao.delete(1);
    }

    public static void main(String[] args) throws Exception {
//        StudentService.add();
        StudentService.delete();
    }
```
### 3.3 修改数据操作
#### 3.3.1 往映射文件写好修改语句
```aidl
    <update id="update" parameterType="com.mybatis_demo.entity.Student">
        update students set name=#{name},score=#{score} where id=#{id};
    </update>
```
#### 3.3.2 往StudentDao添加一个方法
```aidl
    public void update(Student student) throws Exception{
        try{
            sqlSession=MybatisUtil.getSqlSession();
            sqlSession.update("Studao.update",student);
            sqlSession.commit();
        }catch (Exception ex){
            ex.printStackTrace();
            sqlSession.rollback();
        }finally {
            MybatisUtil.closeSqlSession();
        }
    }
```
#### 3.3.3 往StudentService添加方法
```aidl
    public static void update() throws Exception{
        Student student=new Student();
        student.setId(11);
        student.setName("1111");
        student.setScore(1111.1);
        StudentDao dao=new StudentDao();
        dao.update(student);
    }

    public static void main(String[] args) throws Exception {
//        StudentService.add();
//        StudentService.delete();
        StudentService.update();
    }
```
### 3.4 查询多条数据操作
#### 3.4.1 往映射文件写好修改语句
```aidl
<!-- resultMap为映射的id:为实体与表的映射取一个任意的唯一的名字   -->
    <select id="findAll" resultMap="student-mapper">
        SELECT * FROM STUDENTS;
    </select>
```
#### 3.4.2 往StudentDao添加一个方法
```aidl
    public List<Student> findall() throws Exception{
        List list=null;
        try{
            sqlSession=MybatisUtil.getSqlSession();
            list = sqlSession.selectList("Studao.findAll");
        }catch (Exception ex){
            ex.printStackTrace();
            sqlSession.rollback();
        }finally {
            MybatisUtil.closeSqlSession();
        }
        return list;
    }
```
#### 3.4.3 往StudentService添加方法
```aidl
    public static List selectall() throws Exception{
        StudentDao studentdao=new StudentDao();
        return studentdao.findall();
    }

    public static void main(String[] args) throws Exception {
//        StudentService.add();
//        StudentService.delete();
//        StudentService.update();
        List<Student> list = StudentService.selectall();
        Student student=null;
        if(list!=null){
            for(int i = 0;i<list.size();i++){
                student=list.get(i);
                System.out.println("id:"+student.getId()+",name:"+student.getName()+",score:"+student.getScore());
            }
        }
    }
```
### 3.5 查询单条数据操作
#### 3.5.1 往映射文件写好修改语句
```aidl
    <!-- resultMap为映射的id:为实体与表的映射取一个任意的唯一的名字   -->
    <select id="findone" resultMap="student-mapper" parameterType="int">
        SELECT * FROM STUDENTS WHERE ID=#{id};
    </select>
```
#### 3.5.2 往StudentDao添加一个方法
```aidl
    public Student findone(int i) throws Exception{
        Student student=null;
        try{
            sqlSession=MybatisUtil.getSqlSession();
            student = sqlSession.selectOne("Studao.findone",i);
        }catch (Exception ex){
            ex.printStackTrace();
            sqlSession.rollback();
        }finally {
            MybatisUtil.closeSqlSession();
        }
        return student;
    }
```
#### 3.5.3 往StudentService添加方法
```aidl
    public static Student selectone() throws Exception{
        StudentDao studentdao=new StudentDao();
        return studentdao.findone(110);
    }
    
    public static void main(String[] args) throws Exception {
//        StudentService.add();
//        StudentService.delete();
//        StudentService.update();
//        List<Student> list = StudentService.selectall();
//        Student student=null;
//        if(list!=null){
//            for(int i = 0;i<list.size();i++){
//                student=list.get(i);
//                System.out.println("id:"+student.getId()+",name:"+student.getName()+",score:"+student.getScore());
//            }
//        }
        Student student = StudentService.selectone();
        System.out.println("id:"+student.getId()+",name:"+student.getName()+",score:"+student.getScore());
    }
```
