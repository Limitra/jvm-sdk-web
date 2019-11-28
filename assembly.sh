#!/usr/bin/env bash

cd /hdd/dev/limitra/limitra-sdk/jvm/jvm-database
./assembly.sh
cd /hdd/dev/limitra/limitra-sdk/jvm/jvm-web

mv build.sbt proj-build.sbt #Backup original build.sbt.
cp proj-build.sbt build.sbt #Copy build.sbt for include assembly config.
cat assembly.temp >> build.sbt #Combine configuration files project and pom.

sbt clean update compile #Compile with new configuration.
sbt assembly #Assembly export.

rm build.sbt #Remove configuration temp file.
mv proj-build.sbt build.sbt #Restore original build.sbt.
