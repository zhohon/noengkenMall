package com.jfinalshop.model;

/**
 * Created by dextrys on 2016/1/12.
 */
public class ShelfType {
  private String id;
  private String name;

  public ShelfType(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
