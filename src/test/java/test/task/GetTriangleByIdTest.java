package test.task;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import model.Triangle;
import utils.CommonTest;
import utils.RestTransport;

@Slf4j
public class GetTriangleByIdTest extends CommonTest {

    @DisplayName("Get triangle by id - Ok")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void getTriangleById200Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code to create a triangle should be 200");
        Triangle thriangleExp = createRes.getBody().as(Triangle.class);

        // Get the triangle by id
        Response getRes = RestTransport.getTriangleById(thriangleExp.getId());
        assertEquals(200, getRes.getStatusCode(), "Status code to create a triangle should be 200");
        Triangle triangleActual = getRes.as(Triangle.class);

        assertThat("Perimeter should be " + thriangleExp, triangleActual, sameBeanAs(thriangleExp));

        // Delete the triangle
        Response deleteRes = RestTransport.deleteTriangle(thriangleExp.getId());
        assertEquals(200, deleteRes.getStatusCode(), "Status code to create a triangle should be 200");

    }

    @DisplayName("Get triangle by id - Unauthorized")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void getTriangleById401Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code to create a triangle should be 200");
        Triangle thriangleExp = createRes.getBody().as(Triangle.class);

        // Get the triangle by id
        Response getRes = RestTransport.getTriangleById(thriangleExp.getId(), false);
        assertEquals(401, getRes.getStatusCode(), "Status code to create a triangle should be 401");

        // Delete the triangle
        Response deleteRes = RestTransport.deleteTriangle(thriangleExp.getId());
        assertEquals(200, deleteRes.getStatusCode(), "Status code to create a triangle should be 200");

    }

    @DisplayName("Get triangle by id - Not found")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void getTriangleById404Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Get the triangle by id
        Response getRes = RestTransport.getTriangleById(UUID.randomUUID().toString());
        assertEquals(404, getRes.getStatusCode(), "Status code to create a triangle should be 404");

    }

    @DisplayName("Get triangle by id -Bad request")
    @ParameterizedTest
    @CsvSource({ "3,4,5" })
    public void getTriangleById400Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Get the triangle by id
        Response getRes = RestTransport.getTriangleById("/newtriangle" + UUID.randomUUID().toString());
        assertEquals(400, getRes.getStatusCode(), "Status code to create a triangle should be 400");

    }
}
