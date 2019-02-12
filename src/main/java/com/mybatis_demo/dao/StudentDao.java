package com.mybatis_demo.dao;


import com.mybatis_demo.entity.Student;
import com.mybatis_demo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

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
}
