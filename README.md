# EasyShop v2.0

## Description

This project is an e-commerce application that uses Spring Boot API for the backend and a MySQL database for storage. This RESTful API comes with a front end website with the endpoints needed
to provide a smooth shopping experience with features like registering and logging in as a user, browse inventory based on specific criteria, and manage a shopping cart.

## Front End Features

The User Interface functionality includes:

-User Registration and authentication (JWT-based)  
-Display products with pricing and imaging  
-A drop box that filters products based on category   
-Two sliders to dictate price range  
-A drop box that filters products based on color  
-Role-based access for customers/admins

## My Contributions

1. **Categories Controller class**  
   -Created REST API endpoints for category oversight  
   -Implemented exception handling for category-related features to display meaningful errors  
   -Limited functionality based on role of user
   
2. **MySQLCategoriesDao Class**  
   -Implemented methodology to access MySQL database
   
3. **Bug Fixes**  
   -Filter functionality previously yielded incorrect results. Corrected in MySQLProductDao  
   -Updating products previously created a new product with the update. Corrected in ProductsController


