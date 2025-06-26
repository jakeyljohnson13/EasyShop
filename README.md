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

<div align="center">
Here we see the login screen.
</div>

![Capstone 3 Login Screen](https://github.com/user-attachments/assets/7e412e70-3b12-434f-88fb-4c4ea0220131)

<div align="center">
And this is the home page. As you can see in the top right, login is fully functional.
</div>

![Capstone 3 Home Screen](https://github.com/user-attachments/assets/67602f8c-5042-4763-a3f0-1239c50f9d56)

<div align="center">
Filter function
</div>

![Capstone 3 Filtering All Categories](https://github.com/user-attachments/assets/6c5912f0-9c09-4a0e-8430-9c407a57a862)


## My Contributions

1. **Categories Controller class**  
   -Created REST API endpoints for category oversight  
   -Implemented exception handling for category-related features to display meaningful errors  
   -Limited functionality based on role of user

<div align="center">
Original vs Updated
</div>

![Capstone 3 CategoriesController Comparison](https://github.com/user-attachments/assets/bb1a2a11-75c8-4072-830c-a29dfb092d01)
   
2. **MySQLCategoriesDao Class**  
   -Implemented methodology to access MySQL database

<div align="center">
Another side by side comparison
</div>

![Capstone 3 MySQLCategoryDao Comparison](https://github.com/user-attachments/assets/1d4c2507-5502-4a39-9fa9-a8f2942ebc78)

3. **Bug Fixes**  
   -Filter functionality previously yielded incorrect results. Corrected in MySQLProductDao  
   -Updating products as an admin previously caused users to see duplicate products. Corrected in ProductsController

<div align="center">
Bug 1 fix
</div>

![Capstone 3 Bug MySQLProductDao](https://github.com/user-attachments/assets/9827ac70-c84d-4798-a23a-e1d3f3f888bc)

<div align="center">
Bug 2 fix
</div>

![Capstone 3 Bug ProductController](https://github.com/user-attachments/assets/b1b63769-b2d0-4158-a8d2-bf7c9b8a5522)

## Testing with Postman
In order to verify this API was working as intended, I used Postman to test various functions. Below, you'll see an image containing which tests were conducted and the proper HTTPS code.

<div align="center">
Postman Testing
</div>

![Capstone 3 Postman All Pass](https://github.com/user-attachments/assets/7553dee3-663d-42e1-8ab6-eb27351d889f)
