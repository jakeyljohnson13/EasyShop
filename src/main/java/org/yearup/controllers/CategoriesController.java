package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
@RequestMapping("categories")
// add annotation to allow cross site origin requests
@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }


    // add the appropriate annotation for a get action
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Category> getAll() {
        // find and return all categories
        try {
            return categoryDao.getAllCategories();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sorry something is off with the categories.");
        }
    }

    // add the appropriate annotation for a get action
    @GetMapping("{categoryId}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int categoryId) {
        // get the category by id
        try {
            var category = categoryDao.getById(categoryId);

            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return category;
        } catch (ResponseStatusException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something is off with this category.");
        }
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        // get a list of product by categoryId
        try {
            var categoryItems = productDao.listByCategoryId(categoryId);

            if (categoryItems == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            return categoryItems;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Whoops, something's off");
        }
    }

    // add annotation to call this method for a POST action
    @PostMapping()
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category) {
        // insert the category
        try {
            return categoryDao.create(category);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't add category.");
        }
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int categoryId, @RequestBody Category category) {
        // update the category by id
        try {
            categoryDao.update(categoryId, category);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Update unsuccessful.");
        }
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    @DeleteMapping("{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int categoryId) {
        // delete the category by id
        try {
            var category = categoryDao.getById(categoryId);

            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No category there.");
            }
            categoryDao.delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Deletion unsuccessful.");
        }

    }
}
