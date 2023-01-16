* `cd app/src/main/ui`
* `yarn build`
* `zip -r content dist/*`
* per ssh ins ecosystem
* `docker cp /path/to/content.zip usermgt:/opt/apache-tomcat/webapps/usermgt`
* `docker exec -it usermgt bash`
*  von dort aus dann
* `cd /opt/apache-tomcat/webapps/usermgt && unzip content.zip` => Alles überschreiben
* `docker restart usermgt`
