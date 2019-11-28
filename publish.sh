#!/usr/bin/env bash

#sbt in > pgp-cmd gen-key
#sbt in > pgp-cmd send-key {KeyName} hkp://pool.sks-keyservers.net

mv build.sbt proj-build.sbt #Backup original build.sbt.
cp proj-build.sbt build.sbt #Copy build.sbt for include pom config.
cat pom.temp >> build.sbt #Combine configuration files project and pom.

sbt clean update compile #Compile with new configuration.

sbt publishSigned #Publish staging.
sbt sonatypeRelease #Publish public central.

rm build.sbt #Remove configuration temp file.
mv proj-build.sbt build.sbt #Restore original build.sbt.
