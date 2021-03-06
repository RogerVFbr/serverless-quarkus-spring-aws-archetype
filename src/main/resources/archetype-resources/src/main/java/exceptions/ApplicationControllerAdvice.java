#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.exceptions;

import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import software.amazon.awssdk.services.ssm.model.SsmException;

@RestControllerAdvice
public class ApplicationControllerAdvice {
    private static final Logger LOG = Logger.getLogger(ApplicationControllerAdvice.class);

    @ExceptionHandler(SsmException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrors handleAwsResourceException(SsmException ex) {
        LOG.error(String.format("SsmException -> %s", ex.getMessage()));
        return new ApiErrors().withError(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiErrors handleAwsResourceException(RuntimeException ex) {
        LOG.error(String.format("RuntimeException -> %s", ex.getMessage()));
        return new ApiErrors().withError(ex.getMessage());
    }
}
