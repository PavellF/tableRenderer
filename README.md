### Run with IDE

Import as maven project, run main method in Generator.java.
If you encounter problems try to delete .m2 folder.

### Run without IDE

Ensure you have maven installed. 
Download repository, cd to folder containing pom.xml then

``mvn clean compile assembly:single``<br>
``cd target``<br>
``java -jar reportApp.jar settings.xml source-data.tsv out.txt``<br>

### Description

See Report generator.txt.