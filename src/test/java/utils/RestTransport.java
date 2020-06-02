package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestTransport {
    private static String accessToken = "d6f88013-ee53-4a81-a706-1747aec4f139";
    private static String url = "https://qa-quiz.natera.com";
    private static String uri = "/triangle";

    private static RequestSpecification getRequestSpecificationWithToken() {
        RequestSpecification request = getRequestSpecificationWithoutToken();
        request.header("X-User", accessToken);
        return request;
    }

    private static RequestSpecification getRequestSpecificationWithoutToken() {
        RestAssured.baseURI = url + uri;
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        return request;
    }

    public static Response createTriangleDefault(String side1, String side2, String side3) {
        return createTriangleDefault(side1, side2, side3, true);
    }

    public static Response createTriangleDefault(String side1, String side2, String side3, Boolean hasToken) {
        String st = "{\"input\": \"" + side1 + ";" + side2 + ";" + side3 + "\"}";
        LOG.info("body " + st);
        RequestSpecification request = getRequest(hasToken);
        return request.body(st).then().when().post().then().extract().response();
    }

    public static Response createTriangleAnySeparator(String side1, String side2, String side3, String sapartor) {
        String st = "{\"separator\": \"" + sapartor + "\", \"input\": \"" + side1 + sapartor + side2 + sapartor + side3
                + "\"}";
        LOG.info("body " + st);
        RequestSpecification request = getRequest(true);
        return request.body(st).then().when().post().then().extract().response();
    }

    public static Response getTriangleById(String id) {
        return getTriangleById(id, true);
    }

    public static Response getTriangleById(String id, Boolean hasToken) {
        String getUri = "/{triangleId}".replace("{triangleId}", id);
        RequestSpecification request = getRequest(hasToken);
        return request.then().when().get(getUri).then().extract().response();
    }

    public static Response getAllTriangles(Boolean hasToken) {
        RequestSpecification request = getRequest(hasToken);
        return request.then().when().get("/all").then().extract().response();
    }

    public static Response deleteTriangle(String id) {
        return deleteTriangle(id, true);
    }

    public static Response deleteTriangle(String id, Boolean hasToken) {
        String deleteUri = "/{triangleId}".replace("{triangleId}", id);
        RequestSpecification request = getRequest(hasToken);
        return request.then().when().delete(deleteUri).then().extract().response();
    }

    public static Response getPerimeter(String id) {
        return getPerimeter(id, true);
    }

    public static Response getPerimeter(String id, Boolean hasToken) {
        String perimeterUri = "/{triangleId}/perimeter".replace("{triangleId}", id);
        RequestSpecification request = getRequest(hasToken);
        Response response = request.then().when().get(perimeterUri).then().extract().response();
        return response;
    }

    public static Response getArea(String id) {
        return getArea(id, true);
    }

    public static Response getArea(String id, boolean hasToken) {
        String perimeterUri = "/{triangleId}/area".replace("{triangleId}", id);
        RequestSpecification request = getRequest(hasToken);
        return request.then().when().get(perimeterUri).then().extract().response();

    }

    public static RequestSpecification getRequest(Boolean hasToken) {
        RequestSpecification request;
        if (hasToken) {
            request = getRequestSpecificationWithToken();
        } else {
            request = getRequestSpecificationWithoutToken();
        }
        return request;
    }

    public static Response createTriangleDefaultBadRequest(String side1, String side2, String side3) {
        RequestSpecification request = getRequest(true);
        return request.body("{\"input\": \"" + side1 + ";" + side2 + ";" + side3 + "\"}")
                .then()
                .when()
                .post("//")
                .then()
                .extract()
                .response();

    }

    public static Response createTriangleDefaultBadRequestSaparator(String side1, String side2, String side3) {
        RequestSpecification request = getRequest(true);
        return request.body("{\"input\": \"" + side1 + "-" + side2 + "-" + side3 + "\"}")
                .then()
                .when()
                .post("//")
                .then()
                .extract()
                .response();

    }

}
