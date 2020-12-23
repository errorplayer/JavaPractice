package org.lzy.example.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.lzy.example.entity.Employee;
import org.lzy.example.exception.EmployeeNotFoundException;
import org.lzy.example.modelAssembler.EmployeeModelAssembler;
import org.lzy.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepo;
	
	@Autowired
	private EmployeeModelAssembler assembler;
	
	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> all() {
		List<Employee> searchResult = (List<Employee>) employeeRepo.findAll();
		List<EntityModel<Employee>> employees = searchResult
		  .stream()
		  .map(assembler::toModel)
		  .collect(Collectors.toList());
		
		return CollectionModel.of(employees, 
				linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
	
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
		EntityModel<Employee> entityModel = assembler.toModel(employeeRepo.save(newEmployee));
		
		return ResponseEntity
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entityModel);
	}
	
	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
		
		Employee updatedEmployee = employeeRepo.findById(id)
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return employeeRepo.save(employee);
				})
				.orElseGet(() -> {
					newEmployee.setId(id);
					return employeeRepo.save(newEmployee);
				});
		EntityModel entityModel = assembler.toModel(updatedEmployee);
		return ResponseEntity
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entityModel);
		
	}
	
	
	
	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
		employeeRepo.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable Long id) {

	  Employee employee = employeeRepo.findById(id) //
	      .orElseThrow(() -> new EmployeeNotFoundException(id));

	  return assembler.toModel(employee);
	}
	
}

