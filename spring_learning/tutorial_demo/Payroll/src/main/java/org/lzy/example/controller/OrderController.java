package org.lzy.example.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.lzy.example.entity.Order;
import org.lzy.example.entity.enumeration.Status;
import org.lzy.example.exception.OrderNotFoundException;
import org.lzy.example.modelAssembler.OrderModelAssembler;
import org.lzy.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;

@RestController
public class OrderController {

	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private OrderModelAssembler assembler;
	
	@GetMapping("/orders/{id}")
	public EntityModel<Order> one(@PathVariable Long id){
		Order order = orderRepo.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));
		return assembler.toModel(order);
	}
	
	@GetMapping("/orders")
	public CollectionModel<EntityModel<Order>> all(){
		List<Order> searchResult = (List<Order>) orderRepo.findAll();
		List<EntityModel<Order>> orders = searchResult
				.stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		
		return CollectionModel.of(orders,
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}
	
	@PostMapping("/orders")
	ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order){
		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepo.save(order);
		
		return ResponseEntity
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
				.body(assembler.toModel(newOrder));
		
	}
	
	@DeleteMapping("/orders/{id}/cancel")
	public ResponseEntity<?> cancel(@PathVariable Long id){
		Order order = orderRepo.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));
		
		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
		}
		
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				.body(Problem.create()
						.withTitle("Method not allowed")
						.withDetail("You cannot cancel an order that is in the " + order.getStatus() + " status"));
	}
	
	@PutMapping("/orders/{id}/complete")
	public ResponseEntity<?> complete(@PathVariable Long id){
		Order order = orderRepo.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));
		
		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
		}
		
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				.body(Problem.create()
						.withTitle("Method not allowed")
						.withDetail("You cannot complete an order that is in the " + order.getStatus() + " status"));
	}
}
