# simpleRESTAPI-vert.x
 							APPLICATION SET UP
1. Can be run directly by going to simpleREST.zip\org.vishikbhalla\target and running the .fat-jar
2. If you want to compile and see the code follow steps below
3. Make sure Java 1.8 or higher is installed and Apache Maven is installed
4. Unzip the simpleRest folder
5. To see the code you can import the project into your chosen IDE
6. Compile the sources using [mvn clean install]
7. change directory to the target folder in the main project directory
8. execute java -jar [fat.jar name]
9. Connect using postman or browser to localhost:8000/

POST URL: localhost:8000/api/objects

GET URL: localhost:8000/api/objects/<uid>  to get a single object
	   localhost:8000/api/objects/ to get the urls for all objects

PUT URL: localhost:8000/api/objects/<uid>

DELETE URL: localhost:8000/api/objects/<uid>

I have deployed the application as an EC2 microinstance which can be accessed with the link http://ec2-3-136-116-166.us-east-2.compute.amazonaws.com:8000/

							WHAT'S NEXT

This REST API will need a UI which will be the first thing I would implement next. I will use Angular JS to develop a single page application to call the methods I implemented . I will also change the storage of the arbitrary JSON objects from a hashmap which they are currently stored in, to a SQL database with the uid being the primary key. Currently since the objects are stored in a HashMap so it will only be able to hold as many objects as the device running the applications memory will  allow, but if it is expanded to a database it will be able to hold much more. I will add security, by securing with https, and adding a login page and web authentication using Json web tokens. The users and their hashed passwords can be saved in the database as well.After that I will expand the API's so that they can complete operations such as PATCH, HEAD, CONNECT, OPTIONS, and TRACE. 


