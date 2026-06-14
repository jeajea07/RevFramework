#!/bin/bash

APP_NAME="TesteFramework"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/opt/tomcat/webapps"
FRAMEWORK_JAR="$LIB_DIR/rev-framework-1.0.jar"

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/WEB-INF/lib

cp -r $WEB_DIR/* $BUILD_DIR/

cp "$FRAMEWORK_JAR" "$BUILD_DIR/WEB-INF/lib/"

cd $BUILD_DIR || exit
jar -cvf "$APP_NAME.war" .
cd ..

cp -f "$BUILD_DIR/$APP_NAME.war" "$TOMCAT_WEBAPPS/"

echo "Déploiement terminé !"