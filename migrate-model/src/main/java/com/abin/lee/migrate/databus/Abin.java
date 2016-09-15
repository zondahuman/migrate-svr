package com.abin.lee.migrate.databus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-9-15
 * Time: 下午7:27
 * To change this template use File | Settings | File Templates.
 */
public class Abin implements Serializable{
    private Integer id;
    private String name;

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
}
