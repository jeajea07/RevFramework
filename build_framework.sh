#!/bin/bash

FRAMEWORK_NAME="rev-framework"
VERSION="1.0"
SRC_DIR="rev"
OUTPUT_JAR="$FRAMEWORK_NAME-$VERSION.jar"
SERVLET_API_JAR="test/lib/servlet-api.jar"
SPRING_LIB_DIR="rev/lib"

CP="$SERVLET_API_JAR"
for jar in "$SPRING_LIB_DIR"/*.jar; do
    CP="$CP:$jar"
done

find $SRC_DIR -name "*.java" > sources.txt

javac -cp "$CP" \
-d /tmp/rev-classes \
@sources.txt

if [ $? -ne 0 ]; then
rm sources.txt
exit 1
fi

rm -f sources.txt

jar -cvf "$OUTPUT_JAR" -C /tmp/rev-classes .

rm -rf /tmp/rev-classes
cp -f "$OUTPUT_JAR" "test/lib/"
cp -f "$SPRING_LIB_DIR"/*.jar "test/lib/"