package dev.laur.deeptimeatlas.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.laur.deeptimeatlas.taxon.client.exception.PbdbServiceUnavailableException;

import dev.laur.deeptimeatlas.taxon.exception.ReferenceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PbdbServiceUnavailableException.class)
    public ProblemDetail handlePbdbServiceUnavailable(
            PbdbServiceUnavailableException exception
    ) {
        logger.warn("PBDB request failed", exception);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage()
        );

        problem.setTitle("External service unavailable");

        return problem;
    }

    @ExceptionHandler(ReferenceNotFoundException.class)
public ProblemDetail handleReferenceNotFound(
        ReferenceNotFoundException exception
) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            exception.getMessage()
    );

    problem.setTitle("Bibliographic reference not found");

    return problem;
   }
}