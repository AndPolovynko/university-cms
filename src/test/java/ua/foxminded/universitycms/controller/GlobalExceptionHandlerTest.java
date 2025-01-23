package ua.foxminded.universitycms.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

class GlobalExceptionHandlerTest {
  private GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleConstraintViolationExceptionShouldReturnExpectedModelAndView() {
    ConstraintViolationException ex = new ConstraintViolationException(
        Set.of(new ConstraintViolationTestImpl("Violation message.")));
    ModelAndView modelAndView = handler.handleConstraintViolationException(ex);

    ModelAndView expectedModelAndView = new ModelAndView();
    expectedModelAndView.addObject("message", "Violation message.");
    expectedModelAndView.addObject("error", "Internal Server Error");
    expectedModelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    assertThat(modelAndView.getModel()).isEqualTo(expectedModelAndView.getModel());
  }

  @Test
  void handleEntityNotFoundRuntimeExceptionShouldReturnExpectedModelAndView() {
    EntityNotFoundRuntimeException ex = new EntityNotFoundRuntimeException("Entity not found.");
    ModelAndView modelAndView = handler.handleEntityNotFoundRuntimeException(ex);

    ModelAndView expectedModelAndView = new ModelAndView();
    expectedModelAndView.addObject("message", "Entity not found.");
    expectedModelAndView.addObject("error", "Internal Server Error");
    expectedModelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    assertThat(modelAndView.getModel()).isEqualTo(expectedModelAndView.getModel());
  }

  private class ConstraintViolationTestImpl implements ConstraintViolation<Course> {
    private final String message;

    public ConstraintViolationTestImpl(String message) {
      this.message = message;
    }

    @Override
    public String getMessage() {
      return message;
    }

    @Override
    public String getMessageTemplate() {
      return null;
    }

    @Override
    public Course getRootBean() {
      return null;
    }

    @Override
    public Class<Course> getRootBeanClass() {
      return null;
    }

    @Override
    public Object getLeafBean() {
      return null;
    }

    @Override
    public Object[] getExecutableParameters() {
      return null;
    }

    @Override
    public Object getExecutableReturnValue() {
      return null;
    }

    @Override
    public Path getPropertyPath() {
      return null;
    }

    @Override
    public Object getInvalidValue() {
      return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
      return null;
    }

    @Override
    public <U> U unwrap(Class<U> type) {
      return null;
    }
  }
}
