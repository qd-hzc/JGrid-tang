/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tang.taglib.grid;

/**
 *
 * @author txijian
 */
public class Paging {
    private int page_size = 0;
    private int page_number = -1;
    private int total_size = -1;
    private String start_point;
    private String other;

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getTotal_size() {
        return total_size;
    }

    public void setTotal_size(int total_size) {
        this.total_size = total_size;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getStart_point() {
        return start_point;
    }

    public void setStart_point(String start_point) {
        this.start_point = start_point;
    }
    
    
}
