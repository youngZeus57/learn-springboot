package com.tongsean.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tongsean.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.tongsean.rest.webservices.restfulwebservices.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	
	private UserDaoService service;
	
	private UserRepository repository;
	
	private PostRepository postRepository;
	
	public UserJpaResource(UserDaoService service, UserRepository repository, PostRepository postRepository) {
		this.service = service;
		this.repository = repository;
		this.postRepository = postRepository;
	}
	
	@GetMapping(path="/jpa/users")
	public List<User> retrieveAllUser() {
		return repository.findAll();
	}
	
	@GetMapping(path="/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id) {
		Optional<User> user = repository.findById(id);
		
		if (user.isEmpty()) {
			throw new UserNotFoundException("id:"+ id);
		}
		
		EntityModel<User> entityModel = EntityModel.of(user.get());
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUser());
		entityModel.add(link.withRel("all-users"));
		
		return entityModel;
	}

	@DeleteMapping(path="/jpa/users/{id}")
	public void removeUser(@PathVariable int id) {
		repository.deleteById(id);
	}

	@GetMapping(path="/jpa/users/{id}/posts")
	public List<Post> retrievePostsforUser(@PathVariable int id) {
		Optional<User> user = repository.findById(id);
		
		if (user.isEmpty()) {
			throw new UserNotFoundException("id:"+ id);
		}
		
		return user.get().getPosts();
	}
	
	@PostMapping(path="/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = repository.save(user);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping(path="/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> user = repository.findById(id);
		
		if (user.isEmpty()) {
			throw new UserNotFoundException("id:"+ id);
		}
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
}
