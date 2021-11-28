import com.geekbrains.db.dao.ProductsMapper;
import com.geekbrains.db.model.Products;
import com.geekbrains.db.model.ProductsExample;

import java.util.List;

public class Test {
    public static void main(String[] args) {

        MyBatisDbService dbService = new MyBatisDbService();
        ProductsMapper mapper = dbService.getProductsMapper();

        mapper.insert(new Products(1, "Newspaper", 500, 1));
        mapper.insert(new Products(2, "Lamba", 1000, 5));
        mapper.insert(new Products(3, "Newspaper2", 500, 1));
        mapper.insert(new Products(4, "cat", 20, 2));


        ProductsExample example = new ProductsExample();

        example.createCriteria().andCategoryIdEqualTo(1);
        List<Products> productsWithCategory1 = mapper.selectByExample(example);
        System.out.println(productsWithCategory1);

        example.createCriteria().andPriceLessThan(1000);
        List<Products> productsLessPrice = mapper.selectByExample(example);
        System.out.println(productsLessPrice);

        example.createCriteria().andTitleBetween("a", "h");
        List<Products> productsTitle = mapper.selectByExample(example);
        System.out.println(productsTitle);

    }
}