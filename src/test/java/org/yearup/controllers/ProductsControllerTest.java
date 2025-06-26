package org.yearup.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductsController controller;

    @Test
    void updateProduct_callsUpdateOnly() {
        Product p = new Product();
        p.setProductId(42);
        p.setName("Watch");
        p.setPrice(new BigDecimal("80.00"));
        p.setCategoryId(1);
        p.setDescription("A sleek watch.");
        p.setColor("Blue");
        p.setStock(100);
        p.setFeatured(true);
        p.setImageUrl("http://example.com/watch.png");

        controller.updateProduct(42, p);

        verify(productDao, times(1)).update(42, p);
        verify(productDao, never()).create(any(Product.class));
    }

    @Test
    void updateProductFailure_returnsServerError() {
        doThrow(new RuntimeException("DB error"))
                .when(productDao).update(anyInt(), any(Product.class));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.updateProduct(42, new Product())
        );

        assertTrue(ex.getStatus().is5xxServerError());
    }
}