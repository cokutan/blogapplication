package scalefocus.blogapp.restcontrollers;

import java.util.List;

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

import lombok.RequiredArgsConstructor;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.service.BlogService;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class BlogOperationsRestController {
    final private BlogService blogService;

    @GetMapping("/users/{username}/blogs")
    public ResponseEntity<List<Blog>> getSummaryListForUser(@PathVariable String username) {
	try {
	    List<Blog> blogs = blogService.getBlogSummaryListForUser(username);

	    if (blogs.isEmpty()) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }

	    return new ResponseEntity<>(blogs, HttpStatus.OK);
	} catch (BlogAppEntityNotFoundException e) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	} catch (Exception e) {
	    e.printStackTrace();
	    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @GetMapping("/blogs/tags/{tag}")
    public ResponseEntity<List<Blog>> getBlogsWithTag(@PathVariable String tag) {
	try {
	    List<Blog> blogs = blogService.getBlogsWithTag(tag);

	    if (blogs.isEmpty()) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }

	    return new ResponseEntity<>(blogs, HttpStatus.OK);
	} catch (Exception e) {
	    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @PostMapping("/blogs")
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
	try {
	    blog = blogService.createBlog(blog);

	    return new ResponseEntity<>(blog, HttpStatus.OK);
	} catch (BlogAppEntityNotFoundException e) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	} catch (Exception e) {
	    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @PutMapping("/blogs/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable("id") Long id, @RequestBody Blog blog) {
	try {
	    blog = blogService.updateBlog(id, blog);

	    return new ResponseEntity<>(blog, HttpStatus.OK);
	} catch (BlogAppEntityNotFoundException e) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	} catch (Exception e) {
	    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @PutMapping("/blogs/{id}/tags/{tag}")
    public ResponseEntity<HttpStatus> attachTag(@PathVariable("id") Long id, @PathVariable("tag") String tag) {
	try {
	    blogService.attachTag(id, tag);

	    return new ResponseEntity<>(HttpStatus.OK);
	} catch (BlogAppEntityNotFoundException e) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	} catch (Exception e) {
	    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    @DeleteMapping("/blogs/{id}/tags/{tag}")
    public ResponseEntity<HttpStatus> unattachTag(@PathVariable("id") Long id, @PathVariable("tag") String tag) {
	try {
	    blogService.unattachTag(id, tag);
	    return new ResponseEntity<>(HttpStatus.OK);
	} catch (BlogAppEntityNotFoundException e) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	} catch (Exception e) {
	    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }
}
