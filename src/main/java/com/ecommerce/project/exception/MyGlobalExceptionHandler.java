package com.ecommerce.project.exception;

import com.ecommerce.project.payload.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> response= new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{
            String fieldName=((FieldError)err).getField();
            String message=err.getDefaultMessage();
            response.put(fieldName,message);
        });
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> exceptionHandler(Exception e){
        log.error("An error occurred: {}", e.getMessage(), e);
        APIResponse apiResponse=  APIResponse.builder().message(e.getMessage()).status(false).build();
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){

    String message= e.getMessage();
    APIResponse apiResponse=  APIResponse.builder().message(message).status(false).build();
    return  new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIExResponse(APIException e){
        String message= e.getMessage();
        APIResponse apiResponse=  APIResponse.builder().message(message).status(false).build();
        return  new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
}
