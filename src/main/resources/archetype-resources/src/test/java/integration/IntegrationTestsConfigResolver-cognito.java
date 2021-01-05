#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class IntegrationTestsConfigResolver {
    private static final String SYSTEM_PROPERTY_NAME = "integration.test.profile";
    private static final String OUTPUTS_FILE_PATH = "./.serverless/outputs.json";
    private static final String OUTPUTS_PROPS_SERVICE_ENDPOINT = "ServiceEndpoint";
    private static final String OUTPUTS_PROPS_COGNITO_URL = "CognitoUrl";
    private static final String OUTPUTS_PROPS_DEFAULT_CLIENT_ID = "TestClientId";
    private static final String OUTPUTS_PROPS_DEFAULT_CLIENT_SECRET = "TestClientSecret";
    private static final String OUTPUTS_PROPS_SCOPE = "TestScope";

    private static String integrationTestsProfile = "";
    private static Map<String, String> serverlessOutputs = new HashMap<>();
    private static String authString = "";
    private static boolean resolved = false;

    public static synchronized void set() {
        parseProfile();
        if (!integrationTestsProfile.isEmpty() && !resolved) {
            resolved = true;
            parseServerlessOutputs();
            setRestAssuredBaseUrl();
            validateCognitoData();
            fetchAccessToken();
        }
    }

    private static void parseProfile() {
        Properties props = System.getProperties();
        integrationTestsProfile = props.getProperty(SYSTEM_PROPERTY_NAME, "");
    }

    private static void parseServerlessOutputs() {
        File serverlessOutputsFile = new File(OUTPUTS_FILE_PATH);
        if (!serverlessOutputsFile.isFile())
            throw new RuntimeException(
                String.format("Unable to parse '%s'. Deploy to AWS before running cloud " +
                    "   integration tests.", OUTPUTS_FILE_PATH));
        try {
            TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {
            };
            serverlessOutputs = new ObjectMapper().readValue(serverlessOutputsFile, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Unable to parse '%s'. File must contain keys/" +
                    "values only.", OUTPUTS_FILE_PATH));
        }
    }

    private static void setRestAssuredBaseUrl() {
        RestAssured.baseURI = String.format(
            "%s/%s",
            serverlessOutputs.get(OUTPUTS_PROPS_SERVICE_ENDPOINT),
            integrationTestsProfile
        );
        System.out.printf("       RestAssured base URL -> %s%n", RestAssured.baseURI);
    }

    private static void validateCognitoData() {
        if (
            !serverlessOutputs.containsKey(OUTPUTS_PROPS_COGNITO_URL) ||
                !serverlessOutputs.containsKey(OUTPUTS_PROPS_DEFAULT_CLIENT_ID) ||
                !serverlessOutputs.containsKey(OUTPUTS_PROPS_DEFAULT_CLIENT_SECRET) ||
                !serverlessOutputs.containsKey(OUTPUTS_PROPS_SCOPE)) {
            throw new RuntimeException(
                String.format(
                    "'%s' must contain the following keys: '%s', '%s', '%s', '%s'.",
                    OUTPUTS_FILE_PATH,
                    OUTPUTS_PROPS_COGNITO_URL,
                    OUTPUTS_PROPS_DEFAULT_CLIENT_ID,
                    OUTPUTS_PROPS_DEFAULT_CLIENT_SECRET,
                    OUTPUTS_PROPS_SCOPE
                )
            );
        }
    }

    private static void fetchAccessToken() {
        String token = RestAssured
            .given()
            .auth()
            .preemptive()
            .basic(
                serverlessOutputs.get(OUTPUTS_PROPS_DEFAULT_CLIENT_ID),
                serverlessOutputs.get(OUTPUTS_PROPS_DEFAULT_CLIENT_SECRET))
            .contentType(ContentType.URLENC)
            .formParam("client_id", serverlessOutputs.get(OUTPUTS_PROPS_DEFAULT_CLIENT_ID))
            .formParam("grant_type", "client_credentials")
            .formParam("scope", serverlessOutputs.get(OUTPUTS_PROPS_SCOPE))
            .when()
                .post(serverlessOutputs.get(OUTPUTS_PROPS_COGNITO_URL))
            .then()
                .log()
                .ifError()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasKey("access_token"))
                .body("any { it.key == 'access_token' }", is(notNullValue()))
                .extract()
                .path("access_token");

        authString = String.format("Bearer %s", token);
        System.out.println("       API access token acquired.");
    }

    public static String getAuth() {
        return authString;
    }

    public static boolean isLocal() {
        return integrationTestsProfile.isEmpty();
    }
}