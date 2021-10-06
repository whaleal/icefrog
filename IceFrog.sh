#!/bin/bash


# Help info function
help(){
  echo "--------------------------------------------------------------------------"
  echo ""
  echo "usage: ./IceFrog.sh [install | doc | pack]"
  echo ""
  echo "-install    Install icefrog to your local Maven repository."
  echo "-doc        Generate Java doc api for icefrog, you can see it in target dir"
  echo "-pack       Make jar package by Maven"
  echo ""
  echo "--------------------------------------------------------------------------"
}


# Start
./bin/logo.sh
case "$1" in
  'install')
    bin/install.sh
	;;
  'doc')
    bin/javadoc.sh
	;;
  'pack')
    bin/package.sh
	;;
  *)
    help
esac
