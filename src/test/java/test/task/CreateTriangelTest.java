package test.task;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import model.Triangle;
import utils.CommonTest;
import utils.RestTransport;

@Slf4j
public class CreateTriangelTest extends CommonTest {

    @DisplayName("Create triangle test")
    @ParameterizedTest
    @CsvSource({ "3,4,5", "8.6,9.9,12.1", "0.0221,0.0281,0.035" })
    public void createTriangleDefaultTest(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Triangle triangleExpected = Triangle.builder()
                .firstSide(String.valueOf(Double.valueOf(side1)))
                .secondSide(String.valueOf(Double.valueOf(side2)))
                .thirdSide(String.valueOf(Double.valueOf(side3)))
                .build();
        Response createResponse = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createResponse.getStatusCode(), "Status code should be 200");
        Triangle triangleActual = createResponse.getBody().as(Triangle.class);

        assertThat("A triangle should be as expected " + triangleExpected, triangleActual,
                sameBeanAs(triangleExpected).ignoring("id"));

        // Delete the triangle
        Response deleteRes = RestTransport.deleteTriangle(triangleActual.getId());
        assertEquals(200, deleteRes.getStatusCode());
    }

    @DisplayName("Create triangle test") // Bag cause there was not said a separator can not be like "."
    @ParameterizedTest
    @CsvSource({ "3,4,5,-", "1.2,4.6,5.7,:", "1.2,4.6,5.7,~", "1.2,4.6,5.7,separator", "1.2,4.6,5.7,null", "3,4,5,1" })
    public void createTriangleAnySeparatorTest(String side1, String side2, String side3, String separator) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Triangle triangleExpected = Triangle.builder()
                .firstSide(String.valueOf(Double.valueOf(side1)))
                .secondSide(String.valueOf(Double.valueOf(side2)))
                .thirdSide(String.valueOf(Double.valueOf(side3)))
                .build();
        Response createResponse = RestTransport.createTriangleAnySeparator(side1, side2, side3, separator);
        assertEquals(200, createResponse.getStatusCode(), "Status code should be 200");
        LOG.info("Expected " + triangleExpected.toString());
        LOG.info("Actual " + createResponse.asString());
        Triangle triangleActual = createResponse.getBody().as(Triangle.class);

        assertThat("A triangle should be as expected " + triangleExpected, triangleActual,
                sameBeanAs(triangleExpected).ignoring("id"));

        // Delete the triangle
        Response deleteRes = RestTransport.deleteTriangle(triangleActual.getId());
        assertEquals(200, deleteRes.getStatusCode());
    }

    @DisplayName("Create triangle - Unauthorized - request without token")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void createTriangle401Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3, false);
        assertEquals(401, createRes.getStatusCode(), "Status code to create a triangle should be 401");

    }

    @DisplayName("Create triangle - Bad request to path /triangle//")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void createTriangle400Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createResponse = RestTransport.createTriangleDefaultBadRequest(side1, side2, side3);
        assertEquals(400, createResponse.getStatusCode(), "Status code to create a triangle should be 400");

    }

    @DisplayName("Create triangle - Bad request with separator like ';'")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void createTriangle400WrongSeparatorTest(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createResponse = RestTransport.createTriangleDefaultBadRequestSaparator(side1, side2, side3);
        assertEquals(400, createResponse.getStatusCode(), "Status code to create a triangle should be 400");
    }

    @DisplayName("Create triangle - Bad request - wrong values")
    @ParameterizedTest
    @CsvSource({ "\"\",4,5" })
    public void createTriangle400WrongInputTest(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(400, createRes.getStatusCode(), "Status code to create a triangle should be 400");
    }

    @DisplayName("Create triangle - Unprocessible - wrong values")
    @ParameterizedTest
    @CsvSource({ ",4,5", "-3,4,5", "null,4,5", "-,4,5", ";,4,5" }) // a side length usually can not be  negative
    public void createTriangle422WrongRequestTest(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(422, createRes.getStatusCode(), "Status code to create a triangle should be 422");
        // Bad - TODO - length can not be negative
    }

    @DisplayName("Create triangle - Unprocessible - a side length can not be null")
    @Test
    public void createTriangle422WrongRequestTest() {
        LOG.info("Test " + getMethodName() + " started");
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(null, "3", "4");
        assertEquals(422, createRes.getStatusCode(), "Status code to create a triangle should be 422");
    }

    @DisplayName("Create triangle - Unprocessible - summ of 2 any sides shold be more the third side")
    @ParameterizedTest
    @CsvSource({ "1,2,4", "0,1,3" })
    public void createTriangle422Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Try to create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(422, createRes.getStatusCode(), "Status code to create a triangle should be 422");

    }

    @DisplayName("Create triangle more than 10 allowed - Ok")
    @ParameterizedTest
    @CsvSource({ "5,7,10" }) // Bag TODO max 11 saved 
    public void createTriangle422MoreThan10AllowedTest(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started with sides " + side1 + " " + side2 + " " + side3);
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create all available triangles
        for (int i = 0; i < 11; i++) {
            Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
            assertEquals(200, createRes.getStatusCode(), "Status code to create a triangle should be 200");
        }
        Response response = RestTransport.getAllTriangles(true);
        List<Triangle> triangleList = response.jsonPath().getList("", Triangle.class);
        
        // Delete the triangle to clear available triangles count in server
        deleteAllAvailableTriangles(response);
        
        assertEquals(10, triangleList.size(), "Size should be 10");

    }
 

    @DisplayName("Create triangle test - undescriber behavour")
    @ParameterizedTest
    @CsvSource({"3,4,5,.", "1.2,4.6,5.7,.", "3e1,4,5,;" }) 
    public void createTriangleAnySeparator422Test(String side1, String side2, String side3, String separator) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Try to create a triangle
        Response createResponse = RestTransport.createTriangleAnySeparator(side1, side2, side3, separator);
        assertEquals(422, createResponse.getStatusCode(), "Status code should be 422");      
    }

}
