package org.lzy.example.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Employee implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String role;
	private String firstName;
	private String lastName;
	
	
	protected Employee() {}
	
	

	public Employee(String firstName, String lastName, String role) {
		
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.firstName + " " + this.lastName;
	}

	public void setName(String name) {
		String[] part = name.split(" ");
		this.firstName = part[0];
		this.lastName = part[1];
		
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}



	@Override
	public String toString() {
		return "Employee [id=" + id + ", role=" + role + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}


	

	


	
	
}
