package scalefocus.blogapp.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE = "An exception occured, which will cause a {} response";

	/**
	 * Handles BlogAppEntityNotFoundException. Created to encapsulate errors with
	 * more detail than javax.persistence.EntityNotFoundException.
	 *
	 * @param ex the BlogAppEntityNotFoundException
	 * @return the ApiError object
	 */
	@ExceptionHandler(BlogAppEntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(BlogAppEntityNotFoundException ex) {
		ApiError apiError = new ApiError(NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		log.warn(AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, NOT_FOUND, ex);
		return buildResponseEntity(apiError);
	}

	/**
	 * Handles BlogAppEntityNotFoundException. Created to encapsulate errors with
	 * more detail than javax.persistence.EntityNotFoundException.
	 *
	 * @param ex the BlogAppEntityNotFoundException
	 * @return the ApiError object
	 */
	@ExceptionHandler(UserNotAuthorizedForOperation.class)
	protected ResponseEntity<Object> handleUserNotAuthorizedFound(UserNotAuthorizedForOperation ex) {
		ApiError apiError = new ApiError(FORBIDDEN);
		apiError.setMessage(ex.getMessage());
		log.warn(AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, FORBIDDEN, ex);
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(ConnectException.class)
	protected ResponseEntity<Object> handleConenctionError(ConnectException ex) {
		ApiError apiError = new ApiError(SERVICE_UNAVAILABLE);
		apiError.setMessage(ex.getMessage());
		log.warn(AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, SERVICE_UNAVAILABLE, ex);
		return buildResponseEntity(apiError);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex, @Nullable Object body,
			@NonNull HttpHeaders headers, HttpStatusCode status, @NonNull WebRequest request) {
		if (status.is5xxServerError()) {
			log.error(AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, status, ex);
		} else if (status.is4xxClientError()) {
			log.warn(AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, status, ex);
		} else {
			log.debug(AN_EXCEPTION_OCCURED_WHICH_WILL_CAUSE_A_RESPONSE, status, ex);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
