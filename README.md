# ShoppingApp

This is a web application for a Shopping App designed in CSE135 class in University of California, San Diego.

## Pages
There are total 10 pages in this application, each with different functions and different design.

###  1. Home Page
 This page provides links to the login page and the sign-up page.

<img src="./img/home.png" width="500" height="280">

###  2. Signup Page
 In this page a user declares:
  the unique name he would like to have, in a textbox
  his role (owner or customer), in a dropdown menu
  his age, in a textbox
  his state, in a dropdown menu. The states is be displayed as full name “California”, “Nevada”, etc.

<img src="./img/signup.png" width="500" height="280">

If the user signed up successfully, the page will jump to the login page with an information saying "User cece successfully registered!!". 

<img src="./img/signup-success.png" width="500" height="280">

Otherwise, if the user failed to sign up, it will provide the reason for failure, such as "Your sign up failed
Username already registered. Please choose another username or go to Login Screen".

<img src="./img/signup-failure.png" width="600" height="250">


###  3. Login Page
 A user’s session with the application always starts from the “Login page”. In the “Login” page the user provides his name. The name is stored in the session.

<img src="./img/login.png" width="400" height="250">


###  4. Menu Page
 The “Menu” page provides links to the other pages that depend on whether the user is an owner or a customer. The owners have access to all the pages that customers have access but not vice versa.
 
 The menu page for owner:

<img src="./img/menu-owner.png" width="400" height="250">

 The menu page for customer:

<img src="./img/menu-customer.png" width="400" height="250">

###  5. Category Page
 Owners can view and create product categories. This page displays the name and description of each category in text box and text area respectively, and offers “Insert”, “Delete” and “Update” buttons, since the owners can insert, delete and update categories. The page will show information of whether successfully insert/update/delete the category, such as "Data Insert Successfully".

<img src="./img/category.png" width="600" height="300">

###  6. Product Page
 At the “Products” page the owners of the application can see all the products we have, and insert new products, where each product has a name, unique SKU, category and price.
 
<img src="./img/product.png" width="600" height="300">

The owner may also issue a search for products that contain a string provided by the owner in a search textbox, and the displayed products should be those that belong to the selected category and match the string of the search box.

<img src="./img/product-search.png" width="600" height="300">

###  7. Products Browsing Page
 This page offers to the customers the same product searching functionality that the “Products” page offers to the owners. If "Add to cart" is clicked, it will lead to the “Product Order” page, so that the customer can order this product.
 
<img src="./img/productBrowsing.png" width="600" height="300">

###  8. Product Order Page
 This page displays the current contents of the shopping cart, and shows the product that was just chosen and asks the quantity of it that should be placed in the shopping cart. Upon a quantity being submitted, the shopping cart obtains one or more item. The application transfers the user to the “Products Browsing” page.
 
<img src="./img/ProductOrder.png" width="600" height="300">

###  9. Buy Shopping Cart Page
  The customer sees the products, amounts and prices of what he has chosen. He sees the amount price for each product and also sees the total price of the shopping cart.

<img src="./img/shopcart.png" width="600" height="300">

###  10. Confirmation Page
  A successful purchase leads to a “Confirmation” page that shows what was bought. After the purchase the shopping cart is emptied. The data of the purchase (what products/quantities were in the bought cart, who bought, when and at what price) are stored in the database. The confirmation page provides a link to the “Products Browsing” page so that the customer can start again.

<img src="./img/confirmation.png" width="600" height="300">

###  11. Sales Analytics Page
 At the “Products” page the owners of the application can insert new products, where each product has a name, unique SKU, category and price. He provides his credit card in a text box and clicks “Purchase”. 
 
<img src="./img/product.png" width="600" height="250">

###  12. Product Page
 At the “Products” page the owners of the application can insert new products, where each product has a name, unique SKU, category and price.
 
<img src="./img/product.png" width="400" height="250">




