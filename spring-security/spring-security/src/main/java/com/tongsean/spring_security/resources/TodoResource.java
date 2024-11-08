package com.tongsean.spring_security.resources;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoResource {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final List<Todo> LIST_TODO = List.of(
			new Todo("tongsean", "A 1"),
			new Todo("tongsean", "B 2")
			);

	@GetMapping(path="/todos")
	public List<Todo> retrieveTodos() {
		return LIST_TODO;
	}
	
	@GetMapping(path="/users/{username}/todos")
	@PostAuthorize("returnObject.username == 'tongsean'")
	public Todo retrieveTodoByUser(@PathVariable("username") String username) {
		return LIST_TODO.get(0);
	}
	
	@PostMapping(path="/users/{username}/todos")
	public void createTodoForUser(@PathVariable("username") String username, 
			@RequestBody Todo todo) {
		logger.info("Create {} for {}", todo, username);
	}
}


record Todo (String username, String description) {}