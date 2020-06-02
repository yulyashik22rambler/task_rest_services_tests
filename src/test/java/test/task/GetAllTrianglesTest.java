package test.task;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import model.Triangle;
import utils.CommonTest;
import utils.RestTransport;

@Slf4j
public class GetAllTrianglesTest extends CommonTest {

    @DisplayName("Get all triangle test- Ok")
    @ParameterizedTest
    @CsvSource({ "8,9,12" })
    public void getAllTriangles200Test(String side1, String side2, String side3) {
        LOG.info("Test " + getMethodName() + " started");
        LOG.info(side1 + " " + side2 + " " + side3);
        // Create all available triangles
        List<Triangle> triangleListExp = new ArrayList<>();
        List<Triangle> triangleListActual = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Response createRes = RestTransport.createTriangleDefault(side1, side2, side3);
            assertEquals(200, createRes.getStatusCode(), "Status code to create a triangle should be 200");
            triangleListExp.add(createRes.as(Triangle.class));
        }

        // Get all available triangles
        Response response = RestTransport.getAllTriangles(true);

        assertEquals(200, response.getStatusCode(), "Status code to create a triangle should be 200");

        List<Triangle> responseActual = response.jsonPath().getList("", Triangle.class);

        responseActual.forEach(el -> triangleListActual.add(el));

        triangleListExp.sort(Comparator.comparing(Triangle::getId));
        triangleListActual.sort(Comparator.comparing(Triangle::getId));

        assertThat("All triangle should be as " + triangleListExp, triangleListActual, sameBeanAs(triangleListExp));

        // Delete the triangle to clear available triangles count in server
        deleteAllAvailableTriangles(response);
    }

    @DisplayName("Get all triangle test- Ok empty result")
    @Test
    public void getAllTriangles200EmptyTest() {
        LOG.info("Test " + getMethodName() + " started");
           // Delete all available triangles in server
        Response response = RestTransport.getAllTriangles(true);
        deleteAllAvailableTriangles(response);

        // Try to get all available triangles
        Response responseGetAll = RestTransport.getAllTriangles(true);

        assertEquals(200, responseGetAll.getStatusCode(), "Status code of getting all triangles should be 200");
        List<Triangle> triangleList = response.jsonPath().getList("", Triangle.class);
        assertTrue(triangleList.isEmpty(), "Response should be empty");

    }

    @DisplayName("Get all triangle test- Unauthorized")
    @Test
    public void getAllTriangles401Test() {
        LOG.info("Test " + getMethodName() + " started");
        // Try to get all available triangles
        Response response = RestTransport.getAllTriangles(false);

        assertEquals(401, response.getStatusCode(), "Status code of getting all triangles should be 401");
    }

    @DisplayName("Get all triangle test- Bad request")
    @Test
    public void getAllTriangles400Test() {
        LOG.info("Test " + getMethodName() + " started");
        RequestSpecification request = RestTransport.getRequest(true);

        // Try to get all available triangles by bad request
        Response responseCheck = request.then().when().get("//all").then().extract().response();

        assertEquals(400, responseCheck.getStatusCode(), "Status code of getting all triangles should be 400");

    }

    @DisplayName("Get all triangle test - The server has not found anything matching the Request-URI")
    @Test
    public void getAllTriangles400BadRequestTest() {
        LOG.info("Test " + getMethodName() + " started");
        RequestSpecification request = RestTransport.getRequest(true);

        // Try to get all available triangles by bad request
        Response responseCheck = request.then().when().get("/triangles123/all").then().extract().response();
        assertEquals(404, responseCheck.getStatusCode(), "Status code of getting all triangles should be 404");
       
    }
}
