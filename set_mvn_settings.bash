#bash
_ACCESS_ID=$1
_SECRET_ID=$2
_MVN_REPO_PASS=$3
MASTER_PASSWORD=whatever

_EMP=`mvn -emp $MASTER_PASSWORD`

echo "<settingsSecurity>
  <master>$_EMP</master>
</settingsSecurity>" > settings-security.xml

mv settings-security.xml ~/.m2/

_EP=`mvn -ep $_SECRET_ID`
_E_MVN_REPO_PASS=`mvn -ep $_MVN_REPO_PASS`

echo "<settings> 
	<servers> 
		<server> 
			<id>aws.amazon.com</id> 
			<username>$_ACCESS_ID</username> 
			<password>$_EP</password> 
		</server>
		<server>
              <id>37coins.myMavenRepo.read</id>
              <username>myMavenRepo</username>
              <password>$_E_MVN_REPO_PASS</password>
		</server>
	</servers>
</settings>" > settings.xml

mv settings.xml ~/.m2/

