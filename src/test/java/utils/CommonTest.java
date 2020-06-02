package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import model.Triangle;

@Slf4j
public class CommonTest {

    @BeforeEach
    public void deleteAllTriangle() {
        LOG.info("Clearing working directory");
        Response response = RestTransport.getAllTriangles(true);

        deleteAllAvailableTriangles(response);
    }

    @AfterAll
    public static void finish() {
        LOG.info("All tests were run");
    }

    protected static void deleteAllAvailableTriangles(Response response) {
        List<Triangle> triangleList = response.jsonPath().getList("", Triangle.class);

        if (!triangleList.isEmpty()) {
            triangleList.forEach(triangle ->

            {
                try {
                    Response deleteRes = RestTransport.deleteTriangle(triangle.getId());
                    assertEquals(200, deleteRes.getStatusCode());
                    LOG.info("A triangele was deleted");
                } catch (RuntimeException e) {
                    LOG.info("Could not delete triangle");
                }
            });
        }
    }

    protected String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

}
