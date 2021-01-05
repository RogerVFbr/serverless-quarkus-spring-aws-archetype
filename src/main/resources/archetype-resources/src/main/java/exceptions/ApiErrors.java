#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ApiErrors {

    private List<String> errors = new ArrayList<>();

    public ApiErrors withError(String msg) {
        errors.add(msg);
        return this;
    }
}
