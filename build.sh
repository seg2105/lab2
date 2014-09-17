#/bin/sh

# Compile the new stuff
javac -d bin/new/ new/ocsf/server/*.java new/ocsf/client/*.java
javac -d bin/new/ new/simplechat1/common/*.java -classpath bin/new
javac -d bin/new/ new/simplechat1/client/*.java -classpath bin/new
javac -d bin/new/ new/simplechat1/*.java -classpath bin/new


# Compile the old stuff
javac -d bin/original/ original/ocsf/server/*.java original/ocsf/client/*.java
javac -d bin/original/ original/simplechat1/common/*.java -classpath bin/original
javac -d bin/original/ original/simplechat1/client/*.java -classpath bin/original
javac -d bin/original/ original/simplechat1/*.java -classpath bin/original



