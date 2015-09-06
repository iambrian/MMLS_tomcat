sudo cp /public/MMLS/src/MongoWrapper.java /opt/tomcat/webapps/ROOT/WEB-INF/MongoWrapper.java

sudo cp /public/MMLS/src/MySQLWrapper.java /opt/tomcat/webapps/ROOT/WEB-INF/MySQLWrapper.java

sudo cp /public/MMLS/src/ANN.java /opt/tomcat/webapps/ROOT/WEB-INF/ANN.java

















sudo javac -classpath .:/opt/tomcat/lib/servlet-api.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mysql-connector-java-5.1.36-bin.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/gson-2.3.1.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mongo-java-driver-2.13.2.jar MySQLWrapper.java

sudo javac -classpath .:/opt/tomcat/lib/servlet-api.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mysql-connector-java-5.1.36-bin.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/gson-2.3.1.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mongo-java-driver-2.13.2.jar MongoWrapper.java

sudo javac -classpath .:/opt/tomcat/lib/servlet-api.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mongo-java-driver-2.13.2.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/gson-2.3.1.jar ANN.java 

sudo javac -classpath .:/opt/tomcat/lib/servlet-api.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mysql-connector-java-5.1.36-bin.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/json-20141113.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mongo-java-driver-2.13.2.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/gson-2.3.1.jar api.java


sudo mv MySQLWrapper.class classes/MySQLWrapper.class  
 sudo mv MongoWrapper.class classes/MongoWrapper.class
sudo mv ANN.class classes/ANN.class
sudo mv api.class classes/api.class

sudo initctl restart tomcat