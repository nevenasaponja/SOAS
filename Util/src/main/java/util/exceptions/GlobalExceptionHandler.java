package util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionModel> handleHttpClientException(
            HttpClientErrorException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        "Requested resource was not found",
                        HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionModel> handleMissingRequestParam(
            MissingServletRequestParameterException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        "Make sure to enter all required request parameters",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ExceptionModel> handleNoDataFound(
            NoDataFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        String.format(
                                "Please make sure to enter currency from the list: %s",
                                ex.getCurrencies()),
                        HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(CurrencyDoesntExistException.class)
    public ResponseEntity<ExceptionModel> handleInvalidCurrency(
            CurrencyDoesntExistException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        String.format(
                                "Please make sure to enter currency from the list: %s",
                                ex.getCurrencies()),
                        HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ExceptionModel> handleInvalidQuantity(
            InvalidQuantityException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        "Quantity must be greater than zero and within the permitted limit",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionModel> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        "Invalid request data",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ExceptionModel> handleFeignException(
            FeignException ex) {

        HttpStatus status = HttpStatus.resolve(ex.status());

        if (status == null) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }

        return ResponseEntity.status(status)
                .body(new ExceptionModel(
                        "Error while communicating with another microservice",
                        ex.getMessage(),
                        status));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionModel> handleUnexpectedException(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionModel(
                        "An unexpected error occurred",
                        "The request could not be completed",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }
}