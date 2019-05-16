#!/bin/sh
cat pom.xml | grep '<version>' | head -1 | cut -d'>' -f2 | cut -d'<' -f1
