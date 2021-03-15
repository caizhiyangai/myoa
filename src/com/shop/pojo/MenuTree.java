package com.shop.pojo;

import java.util.List;

public class MenuTree {

	private int id;
	private String name;
	
	private List<SysPermission> children; //子菜单

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SysPermission> getChildren() {
		return children;
	}

	public void setChildren(List<SysPermission> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "MenuTree{" +
				"id=" + id +
				", name='" + name + '\'' +
				", children=" + children +
				'}';
	}
}
