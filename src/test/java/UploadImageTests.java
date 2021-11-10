import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UploadImageTests extends BaseTest {

    public static String uploadedImageIdDelete;
    public static String uploadedImageId;

    @Test
    @Order(1)
    void uploadImageFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(getFileContent());
        JsonPath json = given()
                .multiPart("image", fileContentBase64)
                .headers(headers)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
        uploadedImageIdDelete = json.getString("data.deletehash");
        uploadedImageId = json.getString("data.id");
    }

    @Test
    @Order(2)
    void getValidImageTest() {
        given()
                .headers(headers)
                .when()
                .get("image/{uploadedImageId}", uploadedImageId)
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .contentType(ContentType.JSON)
                .body("data.id", equalTo(uploadedImageId));
    }

    @Test
    @Order(3)
    void deleteUploadedImage() {
        given()
                .headers(headers)
                .when()
                .delete("https://api.imgur.com/3/image/{uploadedImageIdDelete}", uploadedImageIdDelete)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private byte[] getFileContent() {
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource("image.jpg").getFile());
        byte[] fileContent = null;
        try {
            fileContent = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}
