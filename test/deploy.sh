#!/bin/bash
APP_NAME="TesteFramework"
WEB_DIR="src/main/webapp"
JAVA_SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/opt/tomcat/webapps"
TOMCAT_LIB="/opt/tomcat/lib"
FRAMEWORK_JAR="$LIB_DIR/rev-framework-1.0.jar"

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/WEB-INF/lib
mkdir -p $BUILD_DIR/WEB-INF/classes

cp -r $WEB_DIR/* $BUILD_DIR/

# copie TOUS les jars de lib/ (framework + spring + hibernate + mysql...) dans le war
cp "$LIB_DIR"/*.jar "$BUILD_DIR/WEB-INF/lib/"

# construit le classpath de compilation avec tous les jars de lib/
CP="$TOMCAT_LIB/*"
for jar in "$LIB_DIR"/*.jar; do
    CP="$CP:$jar"
done

javac -d "$BUILD_DIR/WEB-INF/classes" \
-cp "$CP" \
      $(find $JAVA_SRC_DIR -name "*.java")

if [ $? -ne 0 ]; then
echo "Échec de la compilation !"
exit 1
fi

cd $BUILD_DIR || exit
jar -cvf "$APP_NAME.war" .
cd ..
cp -f "$BUILD_DIR/$APP_NAME.war" "$TOMCAT_WEBAPPS/"
echo "Déploiement terminé !"