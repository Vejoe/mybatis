# Mybatis����(mysql)
## 1 ���ܼ�������
### 1.1 ����
##### MyBatis ����apache��һ����Դ��ĿiBatis, 2010�������Ŀ��apache software foundation Ǩ�Ƶ���google code�����Ҹ���ΪMyBatis����һ������Java�ĳ־ò��ܡ�
##### Mybatis��Hibernate����ORM��һ��ʵ�ֿ�ܣ����Ƕ�JDBC��һ�ַ�װ��
### 1.2 ����
```aidl
ͨ��Reader�����ȡMybatisӳ���ļ�
ͨ��SqlSessionFactoryBuilder���󴴽�SqlSessionFactory����
��ȡ��ǰ�̵߳�SQLSession
����Ĭ�Ͽ���
ͨ��SQLSession��ȡӳ���ļ��еĲ�����ţ��Ӷ���ȡSQL���
�ύ����
�ر���Դ
```
## 2 ������������
### 2.1 ����Maven
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
### 2.2 ����mybatis.xml�����ļ�
```aidl
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- �������ݿ���Ϣ�����ļ� -->
    <properties resource="db.properties"/>
    <!-- ����һ��Ĭ�ϵ����ӻ�����Ϣ -->
    <environments default="mybatis_test">
        <!-- ����һ���������ݿ⻷����Ϣ -->
        <environment id="mybatis_test">
            <!-- ʹ��jdbc�������ʽ -->
            <transactionManager type="jdbc"></transactionManager>
            <!-- ʹ�����ӳط�ʽ����ȡ���� -->
            <dataSource type="pooled">
                <!-- �������ݿ������ -->
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```
### 2.3 ����db.properties���ݿ�����������Ϣ
```aidl
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis_test?serverTimezone=UTC&characterEncoding=utf-8
username=root
password=root
```
### 2.4 дһ���������ݿ�Ĺ�����MybatisUtil�����Ҽ���Ƿ������
```aidl
public class MybatisUtil {
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();
    private static SqlSessionFactory sqlSessionFactory;
    /**
     * ����λ��src/mybatis.xml�����ļ�
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
     * ��ֹ���ͨ��new��������
     */
    private MybatisUtil(){}
    /**
     * ��ȡSqlSession
     */
    public static SqlSession getSqlSession(){
        //�ӵ�ǰ�߳��л�ȡSqlSession����
        SqlSession sqlSession = threadLocal.get();
        //���SqlSession����Ϊ��
        if(sqlSession == null){
            //��SqlSessionFactory�ǿյ�����£���ȡSqlSession����
            sqlSession = sqlSessionFactory.openSession();
            //��SqlSession�����뵱ǰ�̰߳���һ��
            threadLocal.set(sqlSession);
        }
        //����SqlSession����
        return sqlSession;
    }
    /**
     * �ر�SqlSession�뵱ǰ�̷ֿ߳�
     */
    public static void closeSqlSession(){
        //�ӵ�ǰ�߳��л�ȡSqlSession����
        SqlSession sqlSession = threadLocal.get();
        //���SqlSession����ǿ�
        if(sqlSession != null){
            //�ر�SqlSession����
            sqlSession.close();
            //�ֿ���ǰ�߳���SqlSession����Ĺ�ϵ��Ŀ������GC�������
            threadLocal.remove();
        }
    }
    /**
     * ����
     */
    public static void main(String[] args) {
        Connection conn = MybatisUtil.getSqlSession().getConnection();
        System.out.println(conn!=null?"���ӳɹ�":"����ʧ��");
    }
}
```
### 2.5 ����һ�ű�(mysql)
```aidl
create table students(
  id  int(6) primary key,
  name varchar(20),
  score double(3,1)
);
```
### 2.6 ����һ�����ƥ���ʵ����
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
### 2.7 ����һ��ʵ�����ӳ���ϵ�ļ�student-mapper.xml
```aidl
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="Studao">
    <!-- resultMap��ǩ:ӳ��ʵ����� 
         type���ԣ���ʾʵ��ȫ·����
         id���ԣ�Ϊʵ������ӳ��ȡһ�������Ψһ������
    -->
    <resultMap id="student-mapper" type="com.mybatis_demo.entity.Student">
        <!-- id��ǩ:ӳ����������
             result��ǩ��ӳ�����������
             property����:ʵ���������
             column���ԣ�����ֶ���
        -->
        <id property="id" jdbcType="INTEGER" column="id"/>
        <result property="name" jdbcType="VARCHAR" column="name"/>
        <result property="score" jdbcType="DOUBLE" column="score"/>
    </resultMap>
</mapper>
``` 
## 3 ��Mybatis��ɾ�Ĳ�
### 3.1 �������ݲ���
#### 3.1.1 ��ӳ���ļ�<resultMap>��ǩ������Ӹñ�ǩ
```aidl
    <!--id������Dao�������õ��ã�parameterType����������-->
    <insert id="add" parameterType="com.mybatis_demo.entity.Student">
        INSERT INTO STUDENTS (ID, NAME, SCORE) VALUES (#{id},#{name},#{score});
    </insert>
```
#### 3.1.2 ���һ��Dao��дһ��StudentDao
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
#### 3.1.3 ���һ��Service��дһ��StudentService,��ʵ�ֲ���
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
### 3.2 ɾ�����ݲ���
#### 3.2.1 ��ӳ���ļ�д��ɾ�����
```aidl
    <delete id="delete" parameterType="int">
       DELETE FROM STUDENTS WHERE id=#{id};
    </delete>
```
#### 3.2.2 ��StudentDao���һ������
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
#### 3.2.3 ��StudentService��ӷ���
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
### 3.3 �޸����ݲ���
#### 3.3.1 ��ӳ���ļ�д���޸����
```aidl
    <update id="update" parameterType="com.mybatis_demo.entity.Student">
        update students set name=#{name},score=#{score} where id=#{id};
    </update>
```
#### 3.3.2 ��StudentDao���һ������
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
#### 3.3.3 ��StudentService��ӷ���
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
### 3.4 ��ѯ�������ݲ���
#### 3.4.1 ��ӳ���ļ�д���޸����
```aidl
<!-- resultMapΪӳ���id:Ϊʵ������ӳ��ȡһ�������Ψһ������   -->
    <select id="findAll" resultMap="student-mapper">
        SELECT * FROM STUDENTS;
    </select>
```
#### 3.4.2 ��StudentDao���һ������
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
#### 3.4.3 ��StudentService��ӷ���
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
### 3.5 ��ѯ�������ݲ���
#### 3.5.1 ��ӳ���ļ�д���޸����
```aidl
    <!-- resultMapΪӳ���id:Ϊʵ������ӳ��ȡһ�������Ψһ������   -->
    <select id="findone" resultMap="student-mapper" parameterType="int">
        SELECT * FROM STUDENTS WHERE ID=#{id};
    </select>
```
#### 3.5.2 ��StudentDao���һ������
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
#### 3.5.3 ��StudentService��ӷ���
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
