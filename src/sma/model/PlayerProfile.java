package sma.model;

import java.util.ArrayList;

public class PlayerProfile {
	private String name;
	private String status;
	private ArrayList<String> roles;
	
	public PlayerProfile(String name, String status, ArrayList<String> roles) {
		super();
		this.name = name;
		this.status = status;
		this.roles = roles;
	}

	public PlayerProfile() {
		super();
		this.name ="";
		this.status = "";
		this.roles = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<String> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<String> roles) {
		this.roles = roles;
	}
	
	public void print()
	{
		System.out.println("PLAYER PROFILE\n"
				+" NAME "+this.name
				+"\n ROLES "+this.roles
				+"\n STATUS "+this.status);
	}
	
	
}
