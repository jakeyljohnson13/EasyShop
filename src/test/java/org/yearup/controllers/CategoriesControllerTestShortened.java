package org.yearup.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriesControllerTestShortened {

    @Mock CategoryDao categoryDao;
    @Mock ProductDao  productDao;

    CategoriesController controller;

    @BeforeEach
    void setUp() {
        controller = new CategoriesController(categoryDao, productDao);
    }

    // helper factory methods
    private static Category cat(int id, String name) {
        return new Category(id, name, name + " desc");
    }

    private static Product product(int id, String name, int categoryId) {
        Product p = new Product();
        p.setProductId(id);
        p.setName(name);
        p.setDescription(name + " desc");
        p.setPrice(new BigDecimal("9.99"));
        p.setCategoryId(categoryId);
        p.setStock(1);
        p.setFeatured(false);
        p.setColor("N/A");
        p.setImageUrl("http://n/a");
        return p;
    }

    @Nested
    class GetAllCategories {
        @Test
        void happyPath() {
            List<Category> list = List.of(cat(1,"A"), cat(2,"B"));
            when(categoryDao.getAllCategories()).thenReturn(list);

            var result = controller.getAll();

            assertEquals(2, result.size());
            verify(categoryDao).getAllCategories();
            verifyNoInteractions(productDao);
        }

        @Test
        void daoThrows500() {
            when(categoryDao.getAllCategories())
                    .thenThrow(new RuntimeException("boom"));

            var ex = assertThrows(ResponseStatusException.class,
                    controller::getAll);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        }
    }

    @Nested
    class GetById {
        @Test
        void found() {
            when(categoryDao.getById(5)).thenReturn(cat(5,"X"));
            var c = controller.getById(5);
            assertEquals("X", c.getName());
            verify(categoryDao).getById(5);
        }

        @Test
        void notFound404() {
            when(categoryDao.getById(9)).thenReturn(null);
            var ex = assertThrows(ResponseStatusException.class,
                    () -> controller.getById(9));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        void error500() {
            when(categoryDao.getById(7))
                    .thenThrow(new RuntimeException("DB"));
            var ex = assertThrows(ResponseStatusException.class,
                    () -> controller.getById(7));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        }
    }

    @Nested
    class GetProducts {
        @Test
        void found() {
            List<Product> items = List.of(product(1,"P1",1));
            when(productDao.listByCategoryId(1)).thenReturn(items);

            var res = controller.getProductsById(1);
            assertEquals(1, res.size());
            verify(productDao).listByCategoryId(1);
            verifyNoInteractions(categoryDao);
        }

        @ParameterizedTest
        @MethodSource("absentOrError")
        void missingOrError(int id, Throwable toThrow, HttpStatus expected) {
            if (toThrow != null) {
                when(productDao.listByCategoryId(id)).thenThrow(toThrow);
            } else {
                when(productDao.listByCategoryId(id)).thenReturn(null);
            }

            var ex = assertThrows(ResponseStatusException.class,
                    () -> controller.getProductsById(id));
            assertEquals(expected, ex.getStatus());
        }
        static Stream<org.junit.jupiter.params.provider.Arguments> absentOrError() {
            return Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(5, null, HttpStatus.NOT_FOUND),
                    org.junit.jupiter.params.provider.Arguments.of(9, new RuntimeException(), HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }
    }

    @Nested
    class CreateUpdateDelete {
        @Test
        void addCategory() {
            var incoming = cat(0,"New");
            var created  = cat(7,"New");
            when(categoryDao.create(incoming)).thenReturn(created);

            var res = controller.addCategory(incoming);
            assertEquals(7, res.getCategoryId());
            verify(categoryDao).create(incoming);
        }

        @Test
        void updateCategory() {
            var updated = cat(3,"Up");
            controller.updateCategory(3, updated);
            verify(categoryDao).update(3, updated);
        }

        @Test
        void deleteFound() {
            when(categoryDao.getById(2)).thenReturn(cat(2,"Del"));
            controller.deleteCategory(2);
            verify(categoryDao).delete(2);
        }

        @Test
        void deleteNotFound404() {
            when(categoryDao.getById(4)).thenReturn(null);
            var ex = assertThrows(ResponseStatusException.class,
                    () -> controller.deleteCategory(4));
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }
}