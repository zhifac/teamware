#!/bin/bash

#==============================================================================
#        FILE: changePublicAddress.sh
#
#       USAGE: changePublicAddress.sh URL [Teamware deploy directory]
#
# DESCRIPTION: Changes the public address of a Teamware instance.
#              The required parameter is the new public URL.
#              The optional parameter is the Teamware instance's Tomcat deploy 
#              directory. Defaults to the current directory.
#==============================================================================

# script parameters:
PUBLIC_ADDRESS="$1" 	# the new public address to set
DEPLOY_DIR="$2" 	# the instance's deploy directory (<instanceName>-webapps)

function printUsage () {
	echo;
    echo "Usage: `basename $0 .sh` URL [Teamware deploy directory]"
    echo;
    exit 0
}

function initializePaths () {
	if [ -z "$DEPLOY_DIR" ]
	then
		DEPLOY_DIR="@DEPLOYDIR@"
	fi
	if [ ${DEPLOY_DIR:(-1)} != "/" ]
	then
		DEPLOY_DIR="$DEPLOY_DIR/"
	fi
	if [ ${PUBLIC_ADDRESS:0:7} = "http://" ]
	then
		PUBLIC_ADDRESS=${PUBLIC_ADDRESS:7}
	fi
	echo;
	echo "Will update the public Teamware address to: $PUBLIC_ADDRESS"
	echo "Using $DEPLOY_DIR as teamware webapps directory"
	echo;
}

#==============================================================================
#            NAME: updateProperties
#      DESCRIPTON: Updates a properties file's keys with the new URL
#     PARAMETER 1: Full path to the property file
#     PARAMETER 2: The property keys to update
#==============================================================================
function updateProperties () {
	# location of the properties file
	propertiesFilePath=$1
	# the property keys to update with the new URL
	propertyKeys=$2
	# properties file name
	propertiesFile=`basename $propertiesFilePath`
	if [ -e "$propertiesFilePath" ]
	then
		echo "Updating public URL in the `basename $propertiesFilePath` file"
		for key in "${propertyKeys[@]}"
		do
			echo "  Updating value for key '$key'"
			# performing the actual substitution
			sed -i "s#\(^$key=http://\)\([0-9A-Za-z$.+!:_*'(),-]\+\)#\1${PUBLIC_ADDRESS}#" $propertiesFilePath
		done
		echo "Done with `basename $propertiesFilePath`"
		echo;
	else
		echo "ERROR: File $propertiesFile not found in `dirname $propertiesFilePath`"
		echo;
		echo "Update of public address NOT successful!"
		echo;
		exit 1
	fi
	echo;
}

function updateLocalProperties () {
	propertiesFilePath="${DEPLOY_DIR}executive/WEB-INF/classes/local.properties"
	propertyKeys=( docservice.url callback.url url.base gos.url )
	updateProperties $propertiesFilePath $propertyKeys
}

function updateForumProperties () {
	propertiesFilePath="${DEPLOY_DIR}executive/WEB-INF/config/jforum-custom.conf"
	propertyKeys=( forum.link homepage.link )
	updateProperties $propertiesFilePath $propertyKeys
}

function changePublicAddress () {
	updateLocalProperties
	updateForumProperties
}

if [ "$#" -eq 0 ] || [ "$1" = "help" ] 
then
	printUsage
else
	initializePaths
	changePublicAddress
fi
