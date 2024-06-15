certutil -hashfile graph4j-1.0.7.jar MD5 > graph4j-1.0.7.jar.md5
certutil -hashfile graph4j-1.0.7.jar SHA1 > graph4j-1.0.7.jar.sha1
gpg --detach-sign -a graph4j-1.0.7.jar

certutil -hashfile graph4j-1.0.7.pom MD5 > graph4j-1.0.7.pom.md5
certutil -hashfile graph4j-1.0.7.pom SHA1 > graph4j-1.0.7.pom.sha1
gpg --detach-sign -a graph4j-1.0.7.pom

certutil -hashfile graph4j-1.0.7-javadoc.jar MD5 > graph4j-1.0.7-javadoc.jar.md5
certutil -hashfile graph4j-1.0.7-javadoc.jar SHA1 > graph4j-1.0.7-javadoc.jar.sha1
gpg --detach-sign -a graph4j-1.0.7-javadoc.jar

certutil -hashfile graph4j-1.0.7-sources.jar MD5 > graph4j-1.0.7-sources.jar.md5
certutil -hashfile graph4j-1.0.7-sources.jar SHA1 > graph4j-1.0.7-sources.jar.sha1
gpg --detach-sign -a graph4j-1.0.7-sources.jar
