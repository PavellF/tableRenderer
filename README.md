# vendingMachine

This Spring Boot application uses h2 in-memory database as data source, 
default cache implementation based on ConcurrentHashMap. 

### Run with IDE

Import as maven project, change profile to ``prod`` in 
application.yml, run main method in VendingMachineApp 
and go to http://localhost:8080/swagger-ui.html#/
If you encounter problems try to delete .m2 folder.

### Run without IDE

Ensure you have maven installed. 
Download repository, cd to folder containing pom.xml then

``mvn clean install``<br>
``cd target``<br>
``sudo java -jar -Dspring.profiles.active=prod vending-machine-1.0.jar``<br>

Go to http://localhost:8080/swagger-ui.html#/

### Description

REST API for coffee machine: User can request a cup of coffee, coffee machine should return the cup and update it's state in accordance with ammounts of materials left User can add water, milk, beans to the machine or perform machine cleaning
One cup of capuchino uses: 150ml of water, 100ml of Milk, 15mg of coffee beans
If there isn't enougth materials coffee machine should return an error and ask for water, milk, beans or cleaning Once per 8 cups the machine requires cleaning Coffee machine can hold 2 liters of water, 1 liter of milk and 100mg of coffee beans, if user tries to put into it more than possible there should be an overflow error (input validation)
Try to write clean and maintanable code, aply KISS, DRY, YAGNI principles Specifiy documentation as if somebody else would integrate with this API Write tests as you see fit For additional points you can add additional options of coffee types (americano, espresso)
