package org.yearup.data;


import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    void addItem(int userId, int productId);
    void updateItem(int userId, ShoppingCartItem item);
    void deleteCart(int userId);

}
