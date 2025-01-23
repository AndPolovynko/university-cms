package ua.foxminded.universitycms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.ConstraintViolationException;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;

@ControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(ConstraintViolationException.class)
  public ModelAndView handleConstraintViolationException(ConstraintViolationException ex) {
    ModelAndView modelAndView = new ModelAndView("error/custom-error");
    ex.getConstraintViolations().forEach(violation -> {
      modelAndView.addObject("message", violation.getMessage());
    });
    modelAndView.addObject("error", "Internal Server Error");
    modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    return modelAndView;
  }
  
  @ExceptionHandler(EntityNotFoundRuntimeException.class)
  public ModelAndView handleEntityNotFoundRuntimeException(EntityNotFoundRuntimeException ex) {
    ModelAndView modelAndView = new ModelAndView("error/custom-error");
    modelAndView.addObject("message", ex.getMessage());
    modelAndView.addObject("error", "Internal Server Error");
    modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    return modelAndView;
  }
}
