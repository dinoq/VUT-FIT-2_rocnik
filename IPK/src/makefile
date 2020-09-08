
JFLAGS = -g
JC = javac
JVM = java


build:
	$(JC) $(JFLAGS) Server.java
	$(JC) $(JFLAGS) Handler.java

clean:
	$(RM) Server.class
	$(RM) Handler.class

run:
ifdef port
	$(JVM) Server $(port)
else
	@echo "PORT IS NOT DEFINED!"
endif