import Model.Product;
import api.MiniMarketApiService;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Story("MiniMarket")
public class MarketTest {
    private static MiniMarketApiService apiService;
    private static Gson gson;
    private static Long id = 0L;

    @BeforeAll
    static void beforeAll() {
        apiService = new MiniMarketApiService();
        gson = new Gson();
    }

    @Test
    void testGetProductByIdProductExist() throws IOException {
        Product product = apiService.getProduct(1);

        assertAll("Product by id exists",
                () -> assertEquals(1L, product.getId()),
                () -> assertEquals("Milk", product.getTitle()),
                () -> assertEquals("Food", product.getCategoryTitle())
        );
    }

    @Test
    void testGetProductByIdProductNotExists() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            apiService.getProduct(100);
        });
    }

    @Test
    void testGetProducts() throws IOException {
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        String json = getJsonResource("exp.json");
        List<Product> expected = gson.fromJson(json, type);
        List<Product> actually = apiService.getProducts();
        assertEquals(expected.size(), actually.size());
        for (int i = 0; i < expected.size(); i++) {
            assertProduct(expected.get(i), actually.get(i));
        }
    }

    @Test
    @Order(1)
    void testCreateNewProduct() throws IOException {
        Product product = Product.builder()
                .categoryTitle("Food")
                .price(228)
                .title("Fish")
                .build();
        id = apiService.createProduct(product);
        Product expected = apiService.getProduct(id);
        assertEquals(id, expected.getId());
    }

    @Test
    @Order(2)
    void testDeleteById() throws IOException {

        Assertions.assertThrows(EOFException.class, () -> {
            apiService.deleteProduct(id);
        });

        Assertions.assertThrows(RuntimeException.class, () -> {
            apiService.getProduct(id);
        });

    }

    String getJsonResource(String resource) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(getClass().getResource(resource).getFile()));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Step("product act->product exp check")
    void assertProduct(Product expected, Product actually) {
        assertAll("product act->product exp check",
                () -> assertEquals(expected.getId(), actually.getId()),
                () -> assertEquals(expected.getTitle(), actually.getTitle()),
                () -> assertEquals(expected.getCategoryTitle(), actually.getCategoryTitle()),
                () -> assertEquals(expected.getPrice(), actually.getPrice())
        );
    }
}
