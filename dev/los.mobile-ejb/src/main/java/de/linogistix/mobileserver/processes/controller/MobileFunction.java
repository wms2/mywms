package de.linogistix.mobileserver.processes.controller;

import java.io.Serializable;

public class MobileFunction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String className;
	private String[] args;
	
	public MobileFunction( String className ) {
		this.className = className;
		this.args = new String[]{};
	}
	public MobileFunction( String className, String arg0 ) {
		this.className = className;
		this.args = new String[]{arg0};
	}
	public MobileFunction( String className, String[] args ) {
		this.className = className;
		this.args = args;
	}

	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}

	
}
