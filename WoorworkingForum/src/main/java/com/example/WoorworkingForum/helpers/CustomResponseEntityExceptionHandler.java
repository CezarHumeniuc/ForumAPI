package com.example.WoorworkingForum.helpers;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(exception.getDetailMessageArguments(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        return new ResponseEntity<>("You must be logged in", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return new ResponseEntity<>("Unable to find what you were looking for", HttpStatus.BAD_REQUEST);
    }

}
