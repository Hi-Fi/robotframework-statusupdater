package com.github.hi_fi.statusupdater.qc.infrastructure;

public class TestInstance {
	
	private Integer id;
	private Integer testId;
	private String name = "";
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}
	
	public Integer getTestId() {
		return this.testId;
	}
	
	public void setTestId(int testId) {
		this.testId = testId;
	}
	
	public void setTestId(String testId) {
		this.testId = Integer.parseInt(testId);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
