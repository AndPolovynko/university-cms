package ua.foxminded.universitycms.service.exception;

public class InvalidUserConfigurationException extends RuntimeException {
  
  public InvalidUserConfigurationException(String errorMessage) {
    super(errorMessage);
  }
  
  public InvalidUserConfigurationException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}
