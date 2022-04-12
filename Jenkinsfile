#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@v1.5.1', 'github.com/cloudogu/ces-build-lib@v1.48.0'])
import com.cloudogu.ces.cesbuildlib.*
import com.cloudogu.ces.dogubuildlib.*

node('docker') {
    properties([
            // Keep only the last 10 build to preserve space
            buildDiscarder(logRotator(numToKeepStr: '10')),
            // Don't run concurrent builds for a branch, because they use the same workspace directory
            disableConcurrentBuilds(),
            // Parameter to activate dogu upgrade test on demand
            parameters([
              booleanParam(defaultValue: false, description: 'Test dogu upgrade from latest release or optionally from defined version below', name: 'TestDoguUpgrade'),
              string(defaultValue: '', description: 'Old Dogu version for the upgrade test (optional; e.g. 2.222.1-1)', name: 'OldDoguVersionForUpgradeTest'),
              booleanParam(defaultValue: true, description: 'Enables the video recording during the test execution', name: 'EnableVideoRecording'),
              booleanParam(defaultValue: true, description: 'Enables cypress to take screenshots of failing integration tests.', name: 'EnableScreenshotRecording'),
            ])
    ])

    String defaultEmailRecipients = env.EMAIL_RECIPIENTS

    doguName = 'usermgt'
    branch = "${env.BRANCH_NAME}"
    Maven mvn = new MavenWrapper(this)
    Git git = new Git(this, "cesmarvin")
    git.committerName = 'cesmarvin'
    git.committerEmail = 'cesmarvin@cloudogu.com'
    GitFlow gitflow = new GitFlow(this, git)
    GitHub github = new GitHub(this, git)
    Changelog changelog = new Changelog(this)

    // Workaround SUREFIRE-1588 on Debian/Ubuntu. Should be fixed in Surefire 3.0.0
    mvn.additionalArgs = '-DargLine="-Djdk.net.URLClassPath.disableClassPathURLCheck=true"'

    stage('Checkout') {
        checkout scm
        //  Don't remove folders starting in "." like * .m2 (maven), .npm, .cache, .local (bower)
        git.clean('".*/"')
    }

    stage('Lint') {
      lintDockerfile()
      // TODO: Change this to shellCheck("./resources") as soon as https://github.com/cloudogu/dogu-build-lib/issues/8 is solved
      shellCheck("./resources/startup.sh")
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
        }
    }

    stage('SonarQube') {
      withSonarQubeEnv {
        docker.image('sonarsource/sonar-scanner-cli')
          .inside("-e SONAR_HOST_URL=http://${env.SONAR_HOST_URL} -v ${WORKSPACE}:/usr/src") {

            sh "git config 'remote.origin.fetch' '+refs/heads/*:refs/remotes/origin/*'"
            gitWithCredentials("fetch --all")

            def sonarParameters = "-Dsonar.host.url=${env.SONAR_HOST_URL} " +
              "-Dsonar.github.oauth=${env.SONAR_AUTH_TOKEN} "

            if (branch == "master") {
              echo "This branch has been detected as the master branch."
              sh "sonar-scanner " + sonarParameters
            } else if (branch == "develop") {
              echo "This branch has been detected as the develop branch."
              sh "sonar-scanner -Dsonar.branch.name=${branch} -Dsonar.branch.target=master " + sonarParameters
            } else if (env.CHANGE_TARGET) {
              echo "This branch has been detected as a pull request."
              sh "sonar-scanner -Dsonar.branch.name=${env.CHANGE_BRANCH}-PR${env.CHANGE_ID} -Dsonar.branch.target=${env.CHANGE_TARGET} " + sonarParameters
            } else if (branch.startsWith("feature/")) {
              echo "This branch has been detected as a feature branch."
              sh "sonar-scanner -Dsonar.branch.name=${branch} -Dsonar.branch.target=develop " + sonarParameters
            } else if (branch.startsWith("bugfix/")) {
              echo "This branch has been detected as a bugfix branch."
              sh "sonar-scanner -Dsonar.branch.name=${branch} -Dsonar.branch.target=develop " + sonarParameters
            } else if (branch.startsWith("release/")) {
              echo "This branch has been detected as a release branch."
              sh "sonar-scanner -Dsonar.branch.name=${branch} -Dsonar.branch.target=master " + sonarParameters
            } else {
              echo "The branch type of branch ${branch} could not be detected"
            }
          }
      }
      timeout(time: 2, unit: 'MINUTES') { // Needed when there is no webhook for example
        def qGate = waitForQualityGate()
        if (qGate.status != 'OK') {
          unstable("Pipeline unstable due to SonarQube quality gate failure")
        }
      }
    }

    EcoSystem ecoSystem = new EcoSystem(this, "gcloud-ces-operations-internal-packer", "jenkins-gcloud-ces-operations-internal")
    try {

      stage('Provision') {
        ecoSystem.provision("/dogu");
      }

      stage('Setup') {
        ecoSystem.loginBackend('cesmarvin-setup')
        ecoSystem.setup()
      }

      stage('Wait for dependencies') {
        timeout(15) {
          ecoSystem.waitForDogu("cas")
        }
      }

      stage('Build') {
        ecoSystem.build("/dogu")
      }

      stage('Verify') {
        ecoSystem.verify("/dogu")
      }

      stage('Integration Tests') {
         echo "run integration tests."
         ecoSystem.runCypressIntegrationTests([
                 cypressImage: "cypress/included:8.6.0",
                 enableVideo: params.EnableVideoRecording,
                 enableScreenshots    : params.EnableScreenshotRecording,
          ])
      }

      if (params.TestDoguUpgrade != null && params.TestDoguUpgrade){
        stage('Upgrade dogu') {
          // Remove new dogu that has been built and tested above
          ecoSystem.purgeDogu(doguName)

          if (params.OldDoguVersionForUpgradeTest != '' && !params.OldDoguVersionForUpgradeTest.contains('v')){
            println "Installing user defined version of dogu: " + params.OldDoguVersionForUpgradeTest
            ecoSystem.installDogu("official/" + doguName + " " + params.OldDoguVersionForUpgradeTest)
          } else {
            println "Installing latest released version of dogu..."
            ecoSystem.installDogu("official/" + doguName)
          }
          ecoSystem.startDogu(doguName)
          ecoSystem.waitForDogu(doguName)
          ecoSystem.upgradeDogu(ecoSystem)

          // Wait for upgraded dogu to get healthy
          ecoSystem.waitForDogu(doguName)
        }

        stage('Integration Tests - After Upgrade') {
          echo "No integration test exists."
//          ecoSystem.runCypressIntegrationTests([
//                  cypressImage: "cypress/included:8.6.0",
//                  enableVideo: params.EnableVideoRecording,
//                  enableScreenshots    : params.EnableScreenshotRecording,
//          ])
        }
      }

      if (gitflow.isReleaseBranch()) {
        String releaseVersion = git.getSimpleBranchName();

        stage('Finish Release') {
          gitflow.finishRelease(releaseVersion)
        }

        stage('Push Dogu to registry') {
          ecoSystem.push("/dogu")
        }

        stage ('Add Github-Release'){
          github.createReleaseWithChangelog(releaseVersion, changelog)
        }
      }

    } finally {
      stage('Clean') {
        ecoSystem.destroy()
      }
    }

    // Archive Unit and integration test results, if any
    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml,**/target/surefire-reports/TEST-*.xml,**/target/jest-reports/TEST-*.xml'

    mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))
}

void gitWithCredentials(String command){
  withCredentials([usernamePassword(credentialsId: 'cesmarvin', usernameVariable: 'GIT_AUTH_USR', passwordVariable: 'GIT_AUTH_PSW')]) {
    sh (
      script: "git -c credential.helper=\"!f() { echo username='\$GIT_AUTH_USR'; echo password='\$GIT_AUTH_PSW'; }; f\" " + command,
      returnStdout: true
    )
  }
}
