package com.mybatis_demo.service;

import com.mybatis_demo.dao.StudentDao;
import com.mybatis_demo.entity.Student;

import java.util.List;

public class StudentService{
    public static void add() throws Exception {
        Student student=new Student();
        student.setId(1);
        student.setName("12");
        student.setScore(12.3);
        StudentDao dao=new StudentDao();
        dao.add(student);
    }

    public static void delete() throws Exception {
        StudentDao dao=new StudentDao();
        dao.delete(1);
    }

    public static void update() throws Exception{
        Student student=new Student();
        student.setId(11);
        student.setName("1111");
        student.setScore(1111.1);
        StudentDao dao=new StudentDao();
        dao.update(student);
    }

    public static List selectall() throws Exception{
        StudentDao studentdao=new StudentDao();
        return studentdao.findall();
    }

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
}
