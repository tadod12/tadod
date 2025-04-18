#! /bin/bash

# Stop if error
set -e

# Default maven
MAVEN_REPO_CENTRAL=${MAVEN_REPO_CENTRAL:-"http://repo1.maven.org/maven2"}
MAVEN_REPOS_ADDITIONAL=${MAVEN_REPOS_ADDITIONAL:-""}
MAVEN_REPO_CONFLUENT=${MAVEN_REPO_CONFLUENT:-"http://packages.confluent.io/maven"}
MAVEN_DEP_DESTINATION=${MAVEN_DEP_DESTINATION}
EXTERNAL_LIBS_DIR=${EXTERNAL_LIBS_DIR}

# Download dependency
maven_dep() {
    local REPO="$1"
    local GROUP="$2"
    local PACKAGE="$3"
    local VERSION="$4"
    local FILE="$5"

    DOWNLOAD_FILE_TMP_PATH="tmp/maven_dep/${PACKAGE}"
    DOWNLOAD_FILE="$DOWNLOAD_FILE_TMP_PATH/$FILE"
    echo "DEBUG: FILE_NAME='$FILE'"
    
    test -d $DOWNLOAD_FILE_TMP_PATH || mkdir -p $DOWNLOAD_FILE_TMP_PATH
    curl -o "$DOWNLOAD_FILE" "$REPO/$GROUP/$PACKAGE/$VERSION/$FILE"
}

maven_central_dep() {
    maven_dep $MAVEN_REPO_CENTRAL $1 $2 $3 "$2-$3.jar" $4
    mv "$DOWNLOAD_FILE" $MAVEN_DEP_DESTINATION
}

maven_confluent_dep() {
    echo "PARAMS: '$1', '$2', '$3'"
    maven_dep $MAVEN_REPO_CONFLUENT "io/confluent" $1 $2 "$1-$2.jar" $3
    mv "$DOWNLOAD_FILE" $MAVEN_DEP_DESTINATION
}

maven_debezium_plugin() {
    maven_dep $MAVEN_REPO_CENTRAL "io/debezium" "debezium-connector-$1" $2 "debezium-connector-$1-$2-plugin.tar.gz" $3
    tar -xzf "$DOWNLOAD_FILE" -C "$MAVEN_DEP_DESTINATION" && rm "$DOWNLOAD_FILE"
}

maven_debezium_optional() {
    maven_dep $MAVEN_REPO_CENTRAL "io/debezium" "debezium-$1" $2 "debezium-$1-$2.tar.gz" $3
    tar -xzf "$DOWNLOAD_FILE" -C "$EXTERNAL_LIBS_DIR" && rm "$DOWNLOAD_FILE"
}

maven_camel_kafka() {
    maven_dep $MAVEN_REPO_CENTRAL "org/apache/camel/kafkaconnector" "camel-$1-kafka-connector" $2 "camel-$1-kafka-connector-$2-package.tar.gz" $3
    tar -xzf "$DOWNLOAD_FILE" -C "$MAVEN_DEP_DESTINATION" && rm "$DOWNLOAD_FILE"
}

maven_debezium_additional_plugin() {
    eval "$MAVEN_REPOS_ADDITIONAL"
    REPO=${1^^}
    if [ -z "${!REPO}" ]
    then
        maven_dep $MAVEN_REPO_CENTRAL "io/debezium" "debezium-connector-$2" $3 "debezium-connector-$2-$3-plugin.tar.gz" $4
    else
        maven_dep "${!REPO}" "io/debezium" "debezium-connector-$2" $3 "debezium-connector-$2-$3-plugin.tar.gz" $4
    fi
    tar -xzf "$DOWNLOAD_FILE" -C "$MAVEN_DEP_DESTINATION" && rm "$DOWNLOAD_FILE"
}

maven_apicurio_converter() {
    if [[ -z "$EXTERNAL_LIBS_DIR" ]] ; then
        echo "WARNING: EXTERNAL_LIBS_DIR is not set. Skipping Apicurio converter loading..."
        return
    fi
    if [[ ! -d "$EXTERNAL_LIBS_DIR" ]] ; then
        echo "WARNING: EXTERNAL_LIBS_DIR is not a directory. Skipping Apicurio converter loading..."
        return
    fi
    APICURIO_CONVERTER_PACKAGE="apicurio-registry-distro-connect-converter"
    maven_dep $MAVEN_REPO_CENTRAL "io/apicurio" "$APICURIO_CONVERTER_PACKAGE" "$1" "$APICURIO_CONVERTER_PACKAGE-$1.tar.gz" "$2"
    mkdir "$EXTERNAL_LIBS_DIR/apicurio"
    tar -xzf "$DOWNLOAD_FILE" -C "$EXTERNAL_LIBS_DIR/apicurio" && rm "$DOWNLOAD_FILE"
}

maven_otel_libs() {
    if [[ -z "$EXTERNAL_LIBS_DIR" ]] ; then
        echo "WARNING: EXTERNAL_LIBS_DIR is not set. Skipping loading OTEL libraries ..."
        return
    fi
    if [[ ! -d "$EXTERNAL_LIBS_DIR" ]] ; then
        echo "WARNING: EXTERNAL_LIBS_DIR is not a directory. Skipping loading OTEL libraries ..."
        return
    fi
    if [[ ! -d "$EXTERNAL_LIBS_DIR/otel" ]] ; then
	mkdir "$EXTERNAL_LIBS_DIR/otel"
    fi
    maven_dep $MAVEN_REPO_CENTRAL $1 $2 $3 "$2-$3.jar" $4
    mv "$DOWNLOAD_FILE" $EXTERNAL_LIBS_DIR/otel
}

case $1 in
    "central" ) shift
            maven_central_dep ${@}
            ;;
    "confluent" ) shift
            maven_confluent_dep ${@}
            ;;
    "debezium" ) shift
            maven_debezium_plugin ${@}
            ;;
    "debezium-additional" ) shift
            maven_debezium_additional_plugin ${@}
            ;;
    "debezium-optional" ) shift
            maven_debezium_optional ${@}
            ;;
    "camel-kafka" ) shift
            maven_camel_kafka ${@}
            ;;
    "apicurio" ) shift
            maven_apicurio_converter ${@}
            ;;
    "otel" ) shift
            maven_otel_libs ${@}
            ;;
esac