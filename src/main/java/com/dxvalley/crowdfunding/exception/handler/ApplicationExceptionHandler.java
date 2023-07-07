package com.dxvalley.crowdfunding.exception.handler;

import com.dxvalley.crowdfunding.exception.customException.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {
    private final DateTimeFormatter dateTimeFormatter;

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Object> handleInvalidArgument(Exception ex) {
        Map<String, String> errorMap = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodEx = (MethodArgumentNotValidException) ex;
            methodEx.getBindingResult().getFieldErrors().forEach((error) -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
        }

        BindException bindEx = (BindException) ex;
        bindEx.getBindingResult().getFieldErrors().forEach((error) -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler({BadRequestException.class, MultipartException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

//    @ExceptionHandler({UnauthorizedException.class,BannedUserException.class})
//    public ResponseEntity<Object> handleUnauthorizedException(Exception ex, HttpServletRequest request) {
//        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
//        ExceptionResponse apiException = new ExceptionResponse(
//                LocalDateTime.now().format(dateTimeFormatter),
//                httpStatus,
//                ex.getMessage(),
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(httpStatus).body(apiException);
//    }

    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public ResponseEntity<Object> handleBannedUserException(InternalAuthenticationServiceException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }
    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ExceptionHandler({PaymentCannotProcessedException.class})
    public ResponseEntity<Object> handlePaymentCannotProcessedException(PaymentCannotProcessedException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.PAYMENT_REQUIRED;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(httpStatus).body(apiException);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse apiException = new ExceptionResponse(
                LocalDateTime.now().format(dateTimeFormatter),
                httpStatus,
                "An unexpected error occurred while processing your request. Please try again later or contact support.",
                request.getRequestURI());
        log.error("INTERNAL_SERVER_ERROR: " + ex.getMessage());
        return ResponseEntity.status(httpStatus).body(apiException);
    }
}


