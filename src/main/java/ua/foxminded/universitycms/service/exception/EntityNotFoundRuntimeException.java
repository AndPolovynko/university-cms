package ua.foxminded.universitycms.service.exception;

public class EntityNotFoundRuntimeException extends RuntimeException {
  
  public EntityNotFoundRuntimeException(String errorMessage) {
    super(errorMessage);
  }
  
  public EntityNotFoundRuntimeException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}
