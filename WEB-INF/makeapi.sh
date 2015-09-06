 sudo javac -classpath .:/opt/tomcat/lib/servlet-api.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/mysql-connector-java-5.1.36-bin.jar:/opt/tomcat/webapps/ROOT/WEB-INF/lib/json-lib-2.4-jdk15.jar api.java
sudo mv api.class classes/api.class
sudo initctl restart tomcat