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
import model.Result;
import model.Triangle;
import utils.CommonTest;
import utils.RestTransport;

@Slf4j
public class GetTrianglePerimeterTest extends CommonTest {

    @DisplayName("Count a triangle perimeter - Ok")
    @ParameterizedTest
    @CsvSource({ "3,4,5", "8.5,9.9,12.0", "0.00221,0.00221,0.0035" })
    public void getPerimeter200Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Get perimeter
        Response getPerimeterResp = RestTransport.getPerimeter(triangle.getId());
        assertEquals(200, getPerimeterResp.getStatusCode(), "Status code of get perimeter response should be 200");

        Result perimeter = getPerimeterResp.as(Result.class);
        Double perimeterExpected = countPerimeter(side1, side2, side3);

        assertThat("Perimeter should be " + perimeterExpected, perimeter.getResult(), sameBeanAs(perimeterExpected));

        // Delete the triangle to clear available triangles count in server
        Response deleteRes = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteRes.getStatusCode());

    }

    @DisplayName("Count a triangle perimeter - Unauthorized")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getPerimeter401Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Get perimeter without token
        Response getPerimeterResp = RestTransport.getPerimeter(triangle.getId(), false);
        assertEquals(401, getPerimeterResp.getStatusCode(), "Status code of get perimeter response should be 401");

        // Delete the triangle to clear available triangles count in server
        Response deleteRes = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteRes.getStatusCode());

    }

    @DisplayName("Count a triangle perimeter - Not found")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getPerimeter404Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Get perimeter without token
        Response getPerimeterResp = RestTransport.getPerimeter(UUID.randomUUID().toString());
        assertEquals(404, getPerimeterResp.getStatusCode(), "Status code of get perimeter response should be 404");

    }

    @DisplayName("Count a triangle perimeter - Bad request")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getPerimeter400Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Get perimeter without token
        Response getPerimeterResp = RestTransport.getPerimeter("/newtriangle" + UUID.randomUUID().toString());
        assertEquals(400, getPerimeterResp.getStatusCode(), "Status code of get perimeter response should be 400");

    }

    private Double countPerimeter(String side1, String side2, String side3) {
        Double side1D = Double.valueOf(side1);
        Double side2D = Double.valueOf(side2);
        Double side3D = Double.valueOf(side3);

        return side1D + side2D + side3D;
    }
}
