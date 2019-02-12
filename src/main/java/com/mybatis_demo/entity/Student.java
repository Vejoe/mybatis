package com.mybatis_demo.entity;

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
