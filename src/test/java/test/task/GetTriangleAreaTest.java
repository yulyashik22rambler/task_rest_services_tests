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
public class GetTriangleAreaTest extends CommonTest {
    @DisplayName("Count a triangle area - Ok")
    @ParameterizedTest
    @CsvSource({ "3,4,5", "8.5,9.9,12.0", "0.00221,0.00221,0.0035" })
    public void getArea200Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Get area
        Response getAreaResp = RestTransport.getArea(triangle.getId());
        assertEquals(200, getAreaResp.getStatusCode(), "Status code of get area response should be 200");

        Result area = getAreaResp.as(Result.class);
        Double areaExpected = countArea(side1, side2, side3);

        assertThat("Area should be " + areaExpected, area.getResult(), sameBeanAs(areaExpected));

        // Delete the triangle to clear available triangles count in server
        Response deleteRes = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteRes.getStatusCode());

    }

    @DisplayName("Count a triangle area - Unauthorized")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getArea401Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Create a triangle
        Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
        assertEquals(200, createRes.getStatusCode(), "Status code should be 200");
        Triangle triangle = createRes.getBody().as(Triangle.class);

        // Get area without token
        Response getAreaResp = RestTransport.getArea(triangle.getId(), false);
        assertEquals(401, getAreaResp.getStatusCode(), "Status code of get area response should be 401");

        // Delete the triangle to clear available triangles count in server
        Response deleteRes = RestTransport.deleteTriangle(triangle.getId());
        assertEquals(200, deleteRes.getStatusCode());

    }

    @DisplayName("Count a triangle area - Not found")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getArea404Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Get area without token
        Response getAreaResp = RestTransport.getArea(UUID.randomUUID().toString());
        assertEquals(404, getAreaResp.getStatusCode(), "Status code of get area response should be 404");

    }

    @DisplayName("Count a triangle area - Bad request")
    @ParameterizedTest
    @CsvSource({ "2,3,5" })
    public void getArea400Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info("Parameters: " + side1 + " " + side2 + " " + side3);
        // Get area without token
        Response getAreaResp = RestTransport.getArea(UUID.randomUUID().toString() + "/triangle/");
        assertEquals(400, getAreaResp.getStatusCode(), "Status code of get area response should be 400");

    }

    private Double countArea(String side1, String side2, String side3) {
        Double a = Double.valueOf(side1);
        Double b = Double.valueOf(side2);
        Double c = Double.valueOf(side3);

        Double x = (a + b + c) / 2;
        Double S = Math.sqrt(x * (x - a) * (x - b) * (x - c));
        LOG.info("S = " + S);

        return S;
    }
}
