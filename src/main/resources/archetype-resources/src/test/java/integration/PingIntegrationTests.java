#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PingIntegrationTests {
    @BeforeAll
    public static void setup() {
        IntegrationTestsConfigResolver.set();
    }

    @Test
    @Order(1)
    public void whenPing() {
        Response response = RestAssured
            .when()
            .get("/ping");

        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.getContentType(), "application/json");
        assertEquals(response.asString(), "Endpoint pinged.");
    }
}
