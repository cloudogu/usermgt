#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@v2.3.0', 'github.com/cloudogu/ces-build-lib@1.67.0'])
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
              choice(name: 'TrivyScanLevels', choices: [TrivyScanLevel.CRITICAL, TrivyScanLevel.HIGH, TrivyScanLevel.MEDIUM, TrivyScanLevel.ALL], description: 'The levels to scan with trivy'),
              choice(name: 'TrivyStrategy', choices: [TrivyScanStrategy.UNSTABLE, TrivyScanStrategy.FAIL, TrivyScanStrategy.IGNORE], description: 'Define whether the build should be unstable, fail or whether the error should be ignored if any vulnerability was found.'),
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

    stage('Checkout') {
        checkout scm
        //  Don't remove folders starting in "." like * .m2 (maven), .npm, .cache, .local (bower)
        git.clean('".*/"')
        createNpmrcFile("jenkins")
    }

    stage('Lint') {
      lintDockerfile()
      // TODO: Change this to shellCheck("./resources") as soon as https://github.com/cloudogu/dogu-build-lib/issues/8 is solved
      shellCheck("./resources/startup.sh")
    }

    stage('Shell tests') {
      executeShellTests()
    }

    // Run inside of docker container, because karma always starts on port 9876 which might lead to errors when two
    // builds run concurrently (e.g. feature branch, PR and develop)
    new Docker(this).image('timbru31/java-node:8-jdk-18')
            .mountJenkinsUser()
            .inside {

        dir('app') {
            stage('Build') {
                mvn 'clean install -DskipTests'
                archive '**/target/*.jar,**/target/*.zip'
            }

            stage('ESLint') {
                dir('src/main/ui'){
                    sh "yarn lint"
                }
            }

            stage('Unit Test') {
                mvn 'test jacoco:report'
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
    Trivy trivy = new Trivy(this, ecoSystem)

    try {

      stage('Provision') {
        ecoSystem.provision("/dogu");
      }

      stage('Setup') {
        ecoSystem.loginBackend('cesmarvin-setup')
        ecoSystem.setup([registryConfig:"""
                                        "_global": {
                                            "password-policy": {
                                                "must_contain_capital_letter": "true",
                                                "must_contain_lower_case_letter": "true",
                                                "must_contain_digit": "true",
                                                "must_contain_special_character": "true",
                                                "min_length": "14"
                                            }
                                        }
                                    """])
      }

      stage('Wait for dependencies') {
        timeout(15) {
          ecoSystem.waitForDogu("cas")
        }
      }

      stage('Build') {
        ecoSystem.build("/dogu")
      }

      stage('Trivy scan') {
          trivy.scanDogu("/dogu", TrivyScanFormat.HTML, params.TrivyScanLevels, params.TrivyStrategy)
          trivy.scanDogu("/dogu", TrivyScanFormat.JSON,  params.TrivyScanLevels, params.TrivyStrategy)
          trivy.scanDogu("/dogu", TrivyScanFormat.PLAIN, params.TrivyScanLevels, params.TrivyStrategy)
      }

      stage('Verify') {
        ecoSystem.verify("/dogu")
      }

      stage('Integration Tests') {
         echo "run integration tests."
          ecoSystem.runCypressIntegrationTests([
                  cypressImage: "cypress/included:12.9.0",
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
         echo "run integration tests."
            ecoSystem.runCypressIntegrationTests([
                    cypressImage: "cypress/included:12.9.0",
                    enableVideo: params.EnableVideoRecording,
                    enableScreenshots    : params.EnableScreenshotRecording,
            ])
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
        sh "rm -f ${WORKSPACE}/app/src/main/ui/.npmrc"
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

def executeShellTests() {
    def bats_base_image = "bats/bats"
    def bats_custom_image = "cloudogu/bats"
    def bats_tag = "1.2.1"

    def batsImage = docker.build("${bats_custom_image}:${bats_tag}", "--build-arg=BATS_BASE_IMAGE=${bats_base_image} --build-arg=BATS_TAG=${bats_tag} ./unitTests")
    try {
        sh "mkdir -p target"

        batsContainer = batsImage.inside("--entrypoint='' -v ${WORKSPACE}:/workspace") {
            sh "make unit-test-shell-ci"
        }
    } finally {
        junit allowEmptyResults: true, testResults: 'target/shell_test_reports/*.xml'
    }
}

void createNpmrcFile(credentialsId) {
    withCredentials([usernamePassword(credentialsId: "${credentialsId}", usernameVariable: 'TARGET_USER', passwordVariable: 'TARGET_PSW')]) {
        withEnv(["HOME=${env.WORKSPACE}"]) {
            String NPM_TOKEN = """${sh(
                    returnStdout: true,
                    script: 'echo -n "${TARGET_USER}:${TARGET_PSW}" | openssl base64'
            )}""".trim()
            writeFile encoding: 'UTF-8', file: 'app/src/main/ui/.npmrc', text: """
    @cloudogu:registry=https://ecosystem.cloudogu.com/nexus/repository/npm-releases/
    email=jenkins@cloudogu.com
    always-auth=true
    _auth=${NPM_TOKEN}
        """.trim()
        }
    }
}

/**
 * Wrapper around dogu build calls to apply credentials to authenticate against private github repositories.
 * @param user name
 * @param password or user token
 * @param closure
 */
void useConfig(String userName, String token, Closure closure) {
    try {
        createNpmrcFile("jenkins")
        closure.call()
    } catch (err) {
        throw err
    } finally {
        sh "rm -f ${WORKSPACE}/app/src/main/ui/.npmrc"
    }
}
