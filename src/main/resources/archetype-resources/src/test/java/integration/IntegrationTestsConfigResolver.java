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

public class IntegrationTestsConfigResolver {
    private static final String SYSTEM_PROPERTY_NAME = "integration.test.profile";
    private static final String OUTPUTS_FILE_PATH = "./.serverless/outputs.json";
    private static String integrationTestsProfile = "";
    private static Map<String, String> serverlessOutputs = new HashMap<>();
    private static boolean resolved = false;

    public static synchronized void set() {
        parseProfile();
        if (!integrationTestsProfile.isEmpty() && !resolved) {
            resolved = true;
            parseServerlessOutputs();
            setRestAssuredBaseUrl();
        }
    }

    private static void parseProfile() {
        Properties props = System.getProperties();
        integrationTestsProfile = props.getProperty(SYSTEM_PROPERTY_NAME, "");
    }

    private static void parseServerlessOutputs() {
        File serverlessOutputsFile = new File(OUTPUTS_FILE_PATH);
        if(!serverlessOutputsFile.isFile())
            throw new RuntimeException(
                String.format("Unable to parse '%s'. Deploy to AWS before running cloud " +
                    "   integration tests.", OUTPUTS_FILE_PATH));
        try {
            TypeReference<Map<String,String>> typeRef = new TypeReference<Map<String,String>>() {};
            serverlessOutputs = new ObjectMapper().readValue(serverlessOutputsFile, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Unable to parse '%s'. File must contain keys/" +
                    "values only.", OUTPUTS_FILE_PATH));
        }
    }

    private static void setRestAssuredBaseUrl() {
        RestAssured.baseURI = String.format("%s/%s", serverlessOutputs.get("ServiceEndpoint"),
            integrationTestsProfile);
        System.out.printf("       RestAssured base URL -> %s%n", RestAssured.baseURI);
    }
}
