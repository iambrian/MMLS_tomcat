PDIR = $(shell pwd)
GLDIR = /opt/tomcat/lib
LDIR = $(PDIR)/lib
CDIR = $(PDIR)/classes
SOURCES = ANN.java MongoWrapper.java Convertor.java JSONUtil.java MySQLWrapper.java api.java

CP = .:$(GLDIR)/servlet-api.jar:$(LDIR)/mysql-connector-java-5.1.36-bin.jar:$(LDIR)/json-lib-2.4-jdk15.jar:$(LDIR)/json-20141113.jar:$(LDIR)/json-lib-2.4-jdk15.jar:$(LDIR)/gson-2.4.jar:$(LDIR)/mongo-java-driver-2.13.2.jar

all: $(patsubst %.java,%.class,$(SOURCES))
	@mv api.class $(CDIR)/api.class
	@mv Convertor.class $(CDIR)/Convertor.class
	@mv JSONUtil.class $(CDIR)/JSONUtil.class
	@mv MySQLWrapper.class $(CDIR)/MySQLWrapper.class
	@mv MongoWrapper.class $(CDIR)/MongoWrapper.class
	@mv ANN.class $(CDIR)/ANN.class
	@echo Done

%.class:
	@echo -n [Compiling] $(patsubst %.class,%.java,$@)... 
	@sudo javac -classpath $(CP) $(patsubst %.class,%.java,$@)
	@echo success

.PHONY: clean
clean:
	@rm $(patsubst %.java,classes/%.class,$(SOURCES))
	@echo [Removed] $(patsubst %.java,classes/%.class,$(SOURCES))

.PHONY: start
start:
	sudo initctl restart tomcat
	
