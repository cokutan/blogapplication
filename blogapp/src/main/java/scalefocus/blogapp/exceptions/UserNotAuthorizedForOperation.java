package scalefocus.blogapp.exceptions;

import java.util.Optional;

import org.springframework.lang.Nullable;

public class UserNotAuthorizedForOperation extends RuntimeException {

	private static final long serialVersionUID = 4126308745725194316L;

	public UserNotAuthorizedForOperation(String username, String operation, @Nullable String entityId) {
		super("User: " + username + "is not able to process operation: " + operation + " on entity with id :"
				+ Optional.of(entityId).orElse("none"));
	}

}
