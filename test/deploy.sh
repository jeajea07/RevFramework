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

cp "$FRAMEWORK_JAR" "$BUILD_DIR/WEB-INF/lib/"

javac -d "$BUILD_DIR/WEB-INF/classes" \
      -cp "$FRAMEWORK_JAR:$TOMCAT_LIB/*" \
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