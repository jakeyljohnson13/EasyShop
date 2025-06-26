package org.yearup.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.yearup.models.Product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriesControllerTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private ProductDao productDao;

    private CategoriesController controller;

    @BeforeEach
    void setUp() {
        // inject mocks into controller
        controller = new CategoriesController(categoryDao, productDao);
    }

    @Test
    void getAll_returnsListOfCategories() {
        // arrange
        List<Category> fakeCategories = Arrays.asList(
                new Category(1, "Electronics", "Devices and such."),
                new Category(2, "Fashion","Trendy new clothes.")
        );
        when(categoryDao.getAllCategories()).thenReturn(fakeCategories);

        // act
        List<Category> result = controller.getAll();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryDao, times(1)).getAllCategories();
    }

    @Test
    void getAll_throwsInternalServerErrorOnDaoException() {
        // arrange
        when(categoryDao.getAllCategories())
                .thenThrow(new RuntimeException("DB is down"));

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getAll()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getReason().contains("Sorry something is off"));
        verify(categoryDao, times(1)).getAllCategories();
    }
    @Test
    void getById_returnsCategoryWhenFound() {
        // arrange
        Category fake = new Category(7, "Toys", "Kids’ playthings");
        when(categoryDao.getById(7)).thenReturn(fake);

        // act
        Category result = controller.getById(7);

        // assert
        assertNotNull(result);
        assertEquals(7, result.getCategoryId());
        assertEquals("Toys", result.getName());
        assertEquals("Kids’ playthings", result.getDescription());
        verify(categoryDao, times(1)).getById(7);
        verifyNoInteractions(productDao);
    }
    @Test
    void getById_throwsNotFoundWhenCategoryIsNull() {
        // arrange
        when(categoryDao.getById(42)).thenReturn(null);

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(42)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(categoryDao, times(1)).getById(42);
        verifyNoInteractions(productDao);
    }
    @Test
    void getById_throwsInternalServerErrorOnDaoException() {
        // arrange
        when(categoryDao.getById(99))
                .thenThrow(new RuntimeException("DB down"));

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(99)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getReason().contains("Something is off"));
        verify(categoryDao, times(1)).getById(99);
        verifyNoInteractions(productDao);
    }
    @Test
    void getProductsById_returnsProductsWhenFound() {
        //arrange
        Product p1 = new Product();
        p1.setProductId(101);
        p1.setName("Watch");
        p1.setPrice(new BigDecimal("80.00"));
        p1.setCategoryId(1);
        p1.setDescription("A sleek watch.");
        p1.setColor("Blue");
        p1.setStock(100);
        p1.setFeatured(true);
        p1.setImageUrl("http://example.com/watch.png");

        Product p2 = new Product();
        p2.setProductId(102);
        p2.setName("Alarm Clock");
        p2.setPrice(new BigDecimal("25.50"));
        p2.setCategoryId(1);
        p2.setDescription("Loud and reliable.");
        p2.setColor("Red");
        p2.setStock(50);
        p2.setFeatured(false);
        p2.setImageUrl("http://example.com/clock.png");

        List<Product> fakeProducts = Arrays.asList(p1, p2);
        when(productDao.listByCategoryId(1)).thenReturn(fakeProducts);
        when(productDao.listByCategoryId(1)).thenReturn(fakeProducts);

        // act
        List<Product> result = controller.getProductsById(1);

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Widget", result.get(0).getName());
        verify(productDao, times(1)).listByCategoryId(1);
        verifyNoInteractions(categoryDao);
    }

    @Test
    void getProductsById_throwsNotFoundWhenNullReturned() {
        // arrange
        when(productDao.listByCategoryId(5)).thenReturn(null);

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getProductsById(5)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(productDao, times(1)).listByCategoryId(5);
        verifyNoInteractions(categoryDao);
    }

    @Test
    void getProductsById_throwsServerError_onDaoException() {
        // arrange
        when(productDao.listByCategoryId(9))
                .thenThrow(new RuntimeException("DB error"));

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.getProductsById(9)
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getReason().contains("Whoops"));
        verify(productDao, times(1)).listByCategoryId(9);
        verifyNoInteractions(categoryDao);
    }
    @Test
    void addCategory_returnsCreatedCategory() {
        // arrange
        Category incoming = new Category(0, "NewCat", "Brand new category");
        Category created  = new Category(7, "NewCat", "Brand new category");
        when(categoryDao.create(incoming)).thenReturn(created);

        // act
        Category result = controller.addCategory(incoming);

        // assert
        assertNotNull(result);
        assertEquals(7, result.getCategoryId());
        assertEquals("NewCat", result.getName());
        assertEquals("Brand new category", result.getDescription());
        verify(categoryDao, times(1)).create(incoming);
        verifyNoInteractions(productDao);
    }

    @Test
    void addCategory_throwsInternalServerErrorOnDaoException() {
        // arrange
        Category incoming = new Category(0, "BadCat", "This will fail");
        when(categoryDao.create(incoming))
                .thenThrow(new RuntimeException("DB down"));

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.addCategory(incoming)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getReason().contains("Couldn't add category."));
        verify(categoryDao, times(1)).create(incoming);
        verifyNoInteractions(productDao);
    }
    @Test
    void updateCategory_callsDaoWhenNoError() {
        // arrange
        int id = 3;
        Category updated = new Category(id, "UpdatedName", "UpdatedDesc");

        // act
        controller.updateCategory(id, updated);

        // assert
        verify(categoryDao, times(1)).update(id, updated);
        verifyNoInteractions(productDao);
    }

    @Test
    void updateCategory_throwsInternalServerErrorOnDaoException() {
        // arrange
        int id = 4;
        Category bad = new Category(id, "Bad", "Bad");
        doThrow(new RuntimeException("DB down"))
                .when(categoryDao).update(id, bad);

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.updateCategory(id, bad)
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getReason().contains("Update unsuccessful"));
        verify(categoryDao, times(1)).update(id, bad);
        verifyNoInteractions(productDao);
    }
    @Test
    void deleteCategory_deletesWhenCategoryExists() {
        // arrange
        int id = 5;
        Category existing = new Category(id, "Name", "Desc");
        when(categoryDao.getById(id)).thenReturn(existing);

        // act
        controller.deleteCategory(id);

        // assert
        verify(categoryDao, times(1)).getById(id);
        verify(categoryDao, times(1)).delete(id);
        verifyNoInteractions(productDao);
    }

    @Test
    void deleteCategory_throwsNotFoundWhenNoCategory() {
        // arrange
        when(categoryDao.getById(6)).thenReturn(null);

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.deleteCategory(6)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().contains("No category there"));
        verify(categoryDao, times(1)).getById(6);
        verify(categoryDao, never()).delete(anyInt());
        verifyNoInteractions(productDao);
    }
    @Test
    void deleteCategory_throwsInternalServerErrorOnDaoException() {
        // arrange
        int id = 7;
        Category existing = new Category(id, "Name", "Desc");
        when(categoryDao.getById(id)).thenReturn(existing);
        doThrow(new RuntimeException("DB error"))
                .when(categoryDao).delete(id);

        // act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> controller.deleteCategory(id)
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getReason().contains("Deletion unsuccessful"));
        verify(categoryDao, times(1)).getById(id);
        verify(categoryDao, times(1)).delete(id);
        verifyNoInteractions(productDao);
    }
}