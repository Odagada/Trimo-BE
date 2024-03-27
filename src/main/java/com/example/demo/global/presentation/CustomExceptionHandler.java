package com.example.demo.global.presentation;

import com.example.demo.global.webHook.DiscordWebHook;
import com.example.demo.review.exception.ReviewException;
import com.example.demo.review.exception.StarException;
import com.example.demo.review.exception.WeatherException;
import com.example.demo.spot.exception.SpotException;
import com.example.demo.user.exception.UserException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final String defaultErrorMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn(() -> defaultErrorMessage);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(defaultErrorMessage));
    }

    @ExceptionHandler(value = {
            UserException.UserNotFoundException.class,
            ReviewException.ReviewNotFoundException.class,
            SpotException.SpotNotFoundException.class,
            UsernameNotFoundException.class,
            WeatherException.WeatherNotFoundException.class,
            StarException.StarNotFoundException.class,
            ReviewException.SortConditionNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(final RuntimeException exception) {
        final String message = exception.getMessage();
        log.warn(message);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(message));
    }
    @ExceptionHandler(value = {
            SpotException.DuplicatedSpotExistsException.class,
            StarException.InvalidStarRankException.class,
            StarException.StarRankAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomBadRequestException(final RuntimeException exception) {
        final String message = exception.getMessage();
        log.warn(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(value = {
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
        final String message = exception.getMessage();
        log.error("Unexpected error occurred: " + message, exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(value = {
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(final Exception exception){
        final String message = "데이터 무결성 위반 오류입니다. 에러 상세 내용 \n" + exception.getMessage();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(value = {
            AccessDeniedException.class
    })
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException exception) {
        final String message = exception.getMessage();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(value = {
            ConstraintViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException exception) {
        final String message = "Constraint violation error: " + exception.getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        final String message = "Method argument type mismatch: " + exception.getName();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }
    @ExceptionHandler(value = {
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(final HandlerMethodValidationException exception){
        final String message = exception.getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(Exception.class)
    public void sendExceptionToDiscord(Exception e, WebRequest webRequest) {
        final String message = e.getMessage();
        DiscordWebHook discordWebHook =
                new DiscordWebHook(
                        "\uD83D\uDEA8 에러 발생",
                        message
                                + "\n"
                                + "### \uD83D\uDD56 발생 시간 \n"
                                + LocalDateTime.now()
                                + "\n"
                                + "### 🔗 요청 URL\n"
                                + webRequest.getDescription(false)
                                + "\n"
                                + "### 📄 Stack Trace\n"
                                + "```\n"
                                + Arrays.toString(e.getStackTrace()).substring(0,1000)
                                + "\n```",
                        0xFF0000
                );
        discordWebHook.jsonConverter();
    }
}
