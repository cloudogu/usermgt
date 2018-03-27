#!groovy
@Library('github.com/cloudogu/ces-build-lib@8dd2371')
import com.cloudogu.ces.cesbuildlib.*

node('docker') {

    properties([
            // Keep only the last 10 build to preserve space
            buildDiscarder(logRotator(numToKeepStr: '10')),
            // Don't run concurrent builds for a branch, because they use the same workspace directory
            disableConcurrentBuilds()
    ])

    String defaultEmailRecipients = env.EMAIL_RECIPIENTS

    catchError {

        Maven mvn = new MavenWrapper(this)
        Git git = new Git(this)

        stage('Checkout') {
            checkout scm
            //  Don't remove folders starting in "." like * .m2 (maven), .npm, .cache, .local (bower)
            git.clean('".*/"')
        }

        // Run inside of docker container, because karma always starts on port 9876 which might lead to errors when two
        // builds run concurrently (e.g. feature branch, PR and develop)
        new Docker(this).image('openjdk:8-jdk')
                .mountJenkinsUser()
                .inside {

            dir('app') {

                stage('Build') {
                    mvn 'clean install -DskipTests'
                    archive '**/target/*.jar,**/target/*.zip'
                }

                stage('Unit Test') {
                    mvn 'test'
                }

                stage('SonarQube') {
                    def sonarQube = new SonarQube(this, 'ces-sonar')
                    sonarQube.updateAnalysisResultOfPullRequestsToGitHub('sonarqube-gh-token')

                    mvn.additionalArgs += ' -Dsonar.exclusions=target/**,src/main/webapp/components/** '
                    sonarQube.analyzeWith(mvn)
                }
            }
        }
    }

    // Archive Unit and integration test results, if any
    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml,**/target/surefire-reports/TEST-*.xml,**/target/jest-reports/TEST-*.xml'

    mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))
}