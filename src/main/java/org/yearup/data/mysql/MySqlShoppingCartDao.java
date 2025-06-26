package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    private final ProductDao productDao;

    @Override
    public ShoppingCart getByUserId(int userId) {

        ShoppingCart cart = new ShoppingCart();

        String sql = "SELECT product_id, quantity, discount_percent "
                + "FROM capstone.shopping_cart "
                + "WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cart.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading cart");
        }

        return cart;
    }

    @Override
    public void addItem(int userId, int productId) {
        String updateSql =
                "UPDATE capstone.shopping_cart "
                        + "SET quantity = quantity + 1 "
                        + "WHERE user_id = ? AND product_id = ?";

        String insertSql =
                "INSERT INTO capstone.shopping_cart "
                        + "  (user_id, product_id, quantity, discount_percent) "
                        + "VALUES (?, ?, 1, 0)";

        try (Connection conn = getConnection();
             PreparedStatement updatePs = conn.prepareStatement(updateSql);
             PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
            conn.setAutoCommit(false);

            updatePs.setInt(1, userId);
            updatePs.setInt(2, productId);
            int rows = updatePs.executeUpdate();

            if (rows == 0) {
                insertPs.setInt(1, userId);
                insertPs.setInt(2, productId);
                insertPs.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding product to cart.");
        }
    }

    @Override
    public void updateItem(int userId, ShoppingCartItem item) {
        String sql =
                "UPDATE capstone.shopping_cart "
                        + "SET quantity = ?, discount_percent = ? "
                        + "WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getQuantity());
            ps.setBigDecimal(2, item.getDiscountPercent());
            ps.setInt(3, userId);
            ps.setInt(4, item.getProductId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating cart item.");
        }
    }

    @Override
    public void deleteCart(int userId) {
        String sql =
                "DELETE FROM CartItems WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing cart for user "
                    + userId, e);
        }


    }

    private ShoppingCartItem mapRow(ResultSet rs)
            throws SQLException {
        int productId = rs.getInt("product_id");
        int quantity = rs.getInt("quantity");
        BigDecimal discount = rs.getBigDecimal("discount_percent");

        // look up full Product details
        Product product = productDao.getById(productId);

        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setDiscountPercent(discount);

        return item;
    }
}
