package scalefocus.blogapp.restcontrollers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.exceptions.UserNotAuthorizedForOperation;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogUserRepository;
import scalefocus.blogapp.service.BlogService;

@RestController
@RequestMapping("/api/v2")
@SecurityScheme(name = "userBearerHttp", type = SecuritySchemeType.HTTP, description = "authentication needed to use blog methods", scheme = "bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@Slf4j
public class BlogOperationsRestController {
	final private BlogService blogService;

	final private BlogJPARepository blogJPARepository;
	final private BlogUserRepository blogUserRepository;

	@GetMapping("/users/{username}/blogs")
	@Operation(summary = "Get a list of summary of blogs created by given user", tags = { "blogs" }, responses = {
			@ApiResponse(responseCode = "200", description = "Succesfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Blog.class))),
			@ApiResponse(responseCode = "204", description = "Succesfully retrieved with no content", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<List<Blog>> getSummaryListForUser(
			@Parameter(description = "name of the user to retrieve blogs") @PathVariable String username) {
		List<Blog> blogs = blogService.getBlogSummaryListForUser(username);

		if (blogs.isEmpty()) {
			log.info("No blogs found for user..........");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(blogs, HttpStatus.OK);
	}

	@GetMapping("/blogs/tags/{tag}")
	@Operation(summary = "Get a list of blogs attached to a the given tag", tags = { "blogs", "tags" }, responses = {
			@ApiResponse(responseCode = "200", description = "Succesfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Blog.class))),
			@ApiResponse(responseCode = "204", description = "Succesfully retrieved with no content", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<List<Blog>> getBlogsWithTag(
			@Parameter(description = "tag to be used for fetching") @PathVariable String tag) {
		List<Blog> blogs = blogService.getBlogsWithTag(tag);

		if (blogs.isEmpty()) {
			log.info("No blogs found for user..........");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(blogs, HttpStatus.OK);
	}

	@PostMapping("/blogs")
	@Operation(summary = "Create blog", tags = { "blogs" }, responses = {
			@ApiResponse(responseCode = "200", description = "Succesfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Blog.class))),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<Blog> createBlog(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Blog to be created", required = true, content = @Content(schema = @Schema(implementation = Blog.class))) @RequestBody Blog blog,
			Principal principal) {
		blog.setCreatedBy(blogUserRepository.findFirstByUsername(principal.getName()).get());
		blog = blogService.createBlog(blog);

		return new ResponseEntity<>(blog, HttpStatus.OK);
	}

	@PutMapping("/blogs/{id}")
	@Operation(summary = "Update blog", tags = { "blogs" }, responses = {
			@ApiResponse(responseCode = "200", description = "Succesfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Blog.class))),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<Blog> updateBlog(
			@Parameter(description = "id of blog to be updated") @PathVariable("id") Long id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Blog to be updated", required = true, content = @Content(schema = @Schema(implementation = Blog.class))) @RequestBody Blog blog,
			Principal principal) {
		Optional<Blog> blogOp = blogJPARepository.findById(id);
		if (blogOp.isPresent()) {
			if (isUserAuthorized(principal, blogOp, "Update blog", id.toString())) {
				blog = blogService.updateBlog(id, blog);
			}
		}
		return new ResponseEntity<>(blog, HttpStatus.OK);
	}

	@DeleteMapping("/blogs/{id}")
	@Operation(summary = "Delete blog with given id", tags = { "blogs" }, responses = {
			@ApiResponse(responseCode = "200", description = "Succesfully deleted", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<HttpStatus> deleteBlog(
			@Parameter(description = "id of blog to be deleted") @PathVariable("id") Long id, Principal principal) {
		Optional<Blog> blogOp = blogJPARepository.findById(id);
		if (blogOp.isPresent()) {
			if (isUserAuthorized(principal, blogOp, "Delete Blog", id.toString())) {
				blogService.deleteBlog(id);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/blogs/{id}/tags/{tag}")
	@Operation(summary = "Attach a tag to the blog", tags = { "blogs", "tags" }, responses = {
			@ApiResponse(responseCode = "200", description = "Succesfully attached", content = @Content),
			@ApiResponse(responseCode = "403", content = @Content),
			@ApiResponse(responseCode = "404", content = @Content),
			@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<HttpStatus> attachTag(
			@Parameter(description = "id of blog to be attached") @PathVariable("id") Long id,
			@Parameter(description = "tag to attach") @PathVariable("tag") String tag, Principal principal) {
		Optional<Blog> blogOp = blogJPARepository.findById(id);
		if (blogOp.isPresent()) {
			if (isUserAuthorized(principal, blogOp, "Attach Tag", id.toString())) {
				blogService.attachTag(id, tag);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/blogs/{id}/tags/{tag}")
	@Operation(summary = "Detach tag from the blog", operationId = "unattachTag", tags = { "blogs",
			"tags" }, responses = {
					@ApiResponse(responseCode = "200", description = "Succesfully unattached", content = @Content),
					@ApiResponse(responseCode = "403", content = @Content),
					@ApiResponse(responseCode = "404", content = @Content),
					@ApiResponse(responseCode = "500", content = @Content) })
	public ResponseEntity<HttpStatus> unattachTag(
			@Parameter(description = "id of blog to be dettached") @PathVariable("id") Long id,
			@Parameter(description = "tag to be dettached") @PathVariable("tag") String tag, Principal principal) {
		Optional<Blog> blogOp = blogJPARepository.findById(id);
		if (blogOp.isPresent()) {
			if (isUserAuthorized(principal, blogOp, "Dettach tag", id.toString())) {
				blogService.unattachTag(id, tag);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private boolean isUserAuthorized(Principal principal, Optional<Blog> blogOp, String operation, String entityId) {
		String username = principal.getName();
		boolean isUserAuthorized = blogOp.get().getCreatedBy().getUsername().equals(username);
		if (!isUserAuthorized)
			throw new UserNotAuthorizedForOperation(username, operation, entityId);
		return true;
	}
}
