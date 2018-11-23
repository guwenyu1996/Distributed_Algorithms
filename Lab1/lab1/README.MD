# Schiper-Eggli-Sandoz Algorithm on AWS

Procedure of Using on AWS with Ubuntu OS
--
Generate a .jar file of this project, copy it to the server

Modify url.properties file

Copy the url.properties to the same folder as .jar file

Copy the .java.policy file to the root folder of user

Type "java -jar -Djava.rmi.useLocalHostname=false -Djava.rmi.server.hostname=ec2-18-222-231-179.us-east-2.compute.amazonaws.com  -Djava.security.policy=java.policy  Lab1-1.0-SNAPSHOT.one-jar.jar" replace the url by the public url of server

Run the test.java on your own laptop
