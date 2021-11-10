import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class GetImageTests extends BaseTest {

    @Test
    void getValidImageTest() {
        given()
                .headers(headers)
                .log()
                .all(true)
                .when()
                .get("image/{imageHash}", properties.getProperty("imageHash"))
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .body("data.id", equalTo(properties.getProperty("imageHash")))
                .body(containsString(properties.getProperty("imageHash")));
    }

    @Test
    void getImageWithInvalidHash() {
        given()
                .headers(headers)
                .log()
                .all(true)
                .when()
                .get("image/{imageHash}", properties.getProperty("brokenImageHash"))
                .then()
                .body(containsString("imgur: the simple 404 page"));
    }

    @Test
    void getImageWithNoHash() {
        given()
                .headers(headers)
                .log()
                .all(true)
                .when()
                .get("image/")
                .then()
                .statusCode(400);
    }

    @Test
    void getImageWithNoToken() {
        given()
                .log()
                .all(true)
                .when()
                .get("image/{imageHash}", properties.getProperty("imageHash"))
                .then()
                .statusCode(401);
    }
}