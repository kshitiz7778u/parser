PARSER :
project aim to read files in a concurrent mode (multi-thread) and write all data in a in-memory database.

Assumption : Log File is in unix format and json will be in one line

Pre-requisites :
JDK 8
Gradle

Running the Application :
gradle clean bootRun

Set file path like this :
cs.parser.file.location=file:C:\\Users\\KSHITIZ\\{filename} ---->  pass this as input argument to main function else application will 
run with default file test
