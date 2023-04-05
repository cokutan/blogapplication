package scalefocus.blogapp.blogappwebflux.exceptions;

import io.github.techgnious.dto.ResizeResolution;

public class CannotDoTheCompressionException extends RuntimeException {
  public CannotDoTheCompressionException(String filename, ResizeResolution resizeResolution) {
    super("Cannot compress the file named: " + filename + " with resolution:" + resizeResolution);
  }
}
