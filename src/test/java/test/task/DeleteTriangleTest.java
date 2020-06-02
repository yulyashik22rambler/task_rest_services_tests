package test.task;

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
public class DeleteTriangleTest extends CommonTest {
   
    @DisplayName("Delete a triangle  - Ok")
    @ParameterizedTest
    @CsvSource({ "3,4,5", "8.5,9.9,12.0", "0.00221,0.00221,0.0035" })
    public void getPerimeter200Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Delete the triangle
        Response deleteRes = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteRes.getStatusCode(), "Status code should be 200");

    }

    @DisplayName("Delete a triangle  - Unauthorized")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getPerimeter401Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Try to delete the triangle without token
        Response deleteRes = RestTransport.deleteTriangle(triangle.getId(), false);
        assertEquals(401, deleteRes.getStatusCode(), "Status code should be 401");

        // Delete the triangle to clear available triangles count in server
        Response deleteResTrue = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteResTrue.getStatusCode());

    }

    @DisplayName("Delete a triangle  - Not found")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getPerimeter404Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Delete with random id the triangle
        Response deleteRes = RestTransport.deleteTriangle(UUID.randomUUID().toString());
        assertEquals(200, deleteRes.getStatusCode());
    }

    @DisplayName("Delete a triangle  - Bad request")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getPerimeter400Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Try to delete - get a bad request
        Response deleteRes = RestTransport.deleteTriangle("/newtriangle/" + triangle.getId());
        assertEquals(400, deleteRes.getStatusCode(), "Status code should be 400");

        // Delete the triangle to clear available triangles count in server
        Response deleteResTrue = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteResTrue.getStatusCode());

    }

}
