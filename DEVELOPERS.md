
## Releasing

Since java.net stopped working to publish (after their "upgrade"), we've moved to publishing via OSS Sonatype at [https://oss.sonatype.org/index.html](https://oss.sonatype.org/index.html).

  * [Sonatype Maven Repository User Guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide)
  * [Setting up PGP](https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven)

## Publishing to Sonatype

As an abbreviated reminder, follow these steps.

SNAPSHOT Release ([https://oss.sonatype.org/content/repositories/snapshots/](https://oss.sonatype.org/content/repositories/snapshots/))

  1. Having added the Sonatype parent to your POM.


    `<parent>
        <groupId>org.sonatype.oss</groupId>
        <artifactId>oss-parent</artifactId>
    </parent>`


  2. Having previously generated your PGP key, you may need to install it on the box you intend to deploy from. You will have to export it from the original keyring (where you first created the key pair) and install it along with the public key into the keyring on the machine you want to publish from.

   If you followed the steps in [Setting up PGP](https://docs.sonatype.org/displa y/Repository/How+To+Generate+PGP+Signatures+With+Maven) above, you will have published your public key to, lets say, hkp://pool.sks-keyservers.net.

  3. You should just be able to publish via Maven


    `mvn clean deploy`


and as tempus-fugit has been setup with the Maven PGP plugin, it'll automatically sign the artifacts before publishing. You'll just need to enter your pass phrase as part of the build.

## Releasing

  1. Having prepared a release (tagging in Subversion etc) with the [mvn release](http://maven.apache.org/plugins/maven-release-plugin/examples/prepare-release.html) plugin.


    ```mvn release:clean
    mvn release:prepare```

  2. you firstly, have to _stage_ the artifacts with Sonatype.

    `mvn release:perform`

  3. This will upload to Sonatype's staging area where you can promote it through to a release via their GUI at [https://oss.sonatype.org](https://oss.sonatype.org). Pick up the instructions for the GUI from the [User Guide](https://docs.sonaty pe.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8.ReleaseIt).

  4. Enjoy.

### PGP Keys

Download my public key using the following

    $ gpg --keyserver hkp://pool.sks-keyservers.net --recv-keys 8D8988DC

### Maven

Some favorites:


    mvn surefire-report:report-only

To generate the awful Surefire report after running `mvn test`.

    mvn deploy -Dmaven.test.skip=true

To deploy without running the tests.