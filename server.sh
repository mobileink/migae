#!/usr/local/bin/bash

#set -x
# -agentlib:jdwp=transport=dt_socket,server=y,address=7000
# -javaagent:/usr/local/java/appengine/lib/agent/appengine-agent.jar
# -Xbootclasspath/p:/user/local/java/appengine/lib/override/appengine-dev-jdk-overrides.jar
# -D--enable_all_permissions=true

#DEBUG=1

CLASSPATH="war/WEB-INF/lib/**/*.jar:/usr/local/java/appengine/lib/shared/**/*.jar:$CLASSPATH"

#export CLASSPATH=/usr/local/java/appengine-java-sdk-1.7.6/lib/appengine-tools-api.jar

# JVM_OPTS="-javaagent:/usr/local/java/appengine-java-sdk-1.7.6/lib/agent/appengine-agent.jar -Xbootclasspath/p:/usr/local/java/appengine-java-sdk-1.7.6/lib/override/appengine-dev-jdk-overrides.jar"

LEIN_JVM_OPTS="-Xms64m -Xmx512m \
 -javaagent:/usr/local/java/appengine-java-sdk-1.7.6/lib/agent/appengine-agent.jar \
 -Xbootclasspath/p:/usr/local/java/appengine-java-sdk-1.7.6/lib/override/appengine-dev-jdk-overrides.jar \
 -D--enable_all_permissions \
 -Ddatastore.auto_id_allocation_policy=scattered
 -D--property=kickstart.user.dir=/Users/gar/lein/magic \
 -Dappengine.sdk.root=/usr/local/java/appengine \
 -Djava.security.manager \
 -Djava.security.policy=war/WEB-INF/java.policy"
# -Djava.security.policy=/var/folders/4m/gtyyqsjs7mz2gqzvj7zfdpqm0000gn/T/test2005195374264808294.policy"

#source lein with-profile nodeps trampoline repl
#source lein trampoline repl
source lein repl

#     -agentlib:jdwp=transport=dt_socket,server=y,address=7000 \

      # java -cp /usr/local/java/appengine/lib/appengine-tools-api.jar \
#      -javaagent:/usr/local/java/appengine-java-sdk-1.7.6/lib/agent/appengine-agent.jar \
#      -Xbootclasspath/p:/usr/local/java/appengine-java-sdk-1.7.6/lib/override/appengine-dev-jdk-overrides.jar \
#      -Dlog4j.configuration=log4j.props \
#      com.google.appengine.tools.development.DevAppServerMain \
#      /Users/gar/lein/magic/war;

