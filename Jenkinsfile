#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@v3.0.0', 'github.com/cloudogu/ces-build-lib@4.0.1'])
import com.cloudogu.ces.cesbuildlib.*
import com.cloudogu.ces.dogubuildlib.*

productionReleaseBranch = "master"
developmentBranch = "develop"
currentBranch = "${env.BRANCH_NAME}"

EcoSystem ecoSystem = new EcoSystem(this, "gcloud-ces-operations-internal-packer", "jenkins-gcloud-ces-operations-internal")

Maven mvn = new MavenWrapper(this)
Git git = new Git(this, "cesmarvin")
git.committerName = 'cesmarvin'
git.committerEmail = 'cesmarvin@cloudogu.com'
GitFlow gitflow = new GitFlow(this, git)
GitHub github = new GitHub(this, git)
Changelog changelog = new Changelog(this)
String defaultEmailRecipients = env.EMAIL_RECIPIENTS
String doguName = 'usermgt'
String base_url=ecosystem.getExternalIP()

parallel(
     "source code": {
        node('docker') {
            timestamps {

                stage('Checkout') {
                    checkout scm
                    //  Don't remove folders starting in "." like * .m2 (maven), .npm, .cache, .local (bower)
                    git.clean('".*//*  *//* "')
                    createNpmrcFile("jenkins")
                }

                stage('Lint Dockerfile') {
                    Dockerfile dockerfile = new Dockerfile(this)
                    dockerfile.lint()
                }

                stage('Shellcheck') {
                    // TODO: Change this to shellCheck("./resources") as soon as https://github.com/cloudogu/dogu-build-lib/issues/8 is solved
                    shellCheck("./resources/startup.sh")
                }

                stage('Bats Tests') {
                    Bats bats = new Bats(this, docker)
                    bats.checkAndExecuteTests()
                }

                stage('Check markdown links') {
                    Markdown markdown = new Markdown(this, "3.11.2")
                    markdown.check()
                }

                // Run inside of docker container, because karma always starts on port 9876 which might lead to errors when two
                // builds run concurrently (e.g. feature branch, PR and develop)
                new Docker(this).image('timbru31/java-node:8-jdk-18')
                        .mountJenkinsUser()
                        .inside {
                            dir('app') {
                                stage('Build') {
                                    sh './mvnw clean install -DskipTests'
                                    archive '**//*  *//* target *//*  *//*.jar,**//*  *//* target *//*  *//*.zip'
                                }

                                stage('ESLint') {
                                    dir('src/main/ui') {
                                        sh "yarn lint"
                                    }
                                }

                                stage('Unit Test') {
                                    sh './mvnw test jacoco:report'
                                }
                            }
                        }

            stage('SonarQube') {
                stageStaticAnalysisSonarQube()
            }

        // Archive Unit and integration test results, if any
                junit allowEmptyResults: true, testResults: '**//*  *//* target/failsafe-reports/TEST-*.xml,**//*  *//* target/surefire-reports/TEST-*.xml,**//*  *//* target/jest-reports/TEST-*.xml'

                mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))
            }
        }
    },
    "dogu-integration": {
        node('vagrant') {
            timestamps {
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
                                choice(name: 'TrivySeverityLevels', choices: [TrivySeverityLevel.CRITICAL, TrivySeverityLevel.HIGH_AND_ABOVE, TrivySeverityLevel.MEDIUM_AND_ABOVE, TrivySeverityLevel.ALL], description: 'The levels to scan with trivy', defaultValue: TrivySeverityLevel.CRITICAL),
                                choice(name: 'TrivyStrategy', choices: [TrivyScanStrategy.UNSTABLE, TrivyScanStrategy.FAIL, TrivyScanStrategy.IGNORE], description: 'Define whether the build should be unstable, fail or whether the error should be ignored if any vulnerability was found.', defaultValue: TrivyScanStrategy.UNSTABLE),
                        ])
                ])

                stage('Checkout') {
                    checkout scm
                    git.clean('".*/"')
                    createNpmrcFile("jenkins")
                }

                try {
                    stage('Provision') {
                        // change namespace to prerelease_namespace if in develop-branch
                        if (gitflow.isPreReleaseBranch()) {
                            sh "make prerelease_namespace"
                        }
                        ecoSystem.provision("/dogu");
                    }

                    stage('Setup') {
                        ecoSystem.loginBackend('cesmarvin-setup')
                        ecoSystem.setup([registryConfig: """
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
                        // purge usermgt from official namespace to prevent conflicts while building prerelease_official/usermgt
                        if (gitflow.isPreReleaseBranch()) {
                            ecoSystem.purgeDogu("usermgt")
                        }
                        ecoSystem.build("/dogu")
                    }

                    stage('Trivy scan') {
                        ecoSystem.copyDoguImageToJenkinsWorker("/dogu")
                        Trivy trivy = new Trivy(this)
                        trivy.scanDogu(".", params.TrivySeverityLevels, params.TrivyStrategy)
                        trivy.saveFormattedTrivyReport(TrivyScanFormat.TABLE)
                        trivy.saveFormattedTrivyReport(TrivyScanFormat.JSON)
                        trivy.saveFormattedTrivyReport(TrivyScanFormat.HTML)
                    }

                    stage('Verify') {
                        ecoSystem.verify("/dogu")
                    }

                    new Docker(this).image('mcr.microsoft.com/playwright:v1.50.0-noble')
                            .mountJenkinsUser()
                            .inside {
                                dir('playwright') {
                                    stage('e2e-tests') {
                                        sh 'npm ci'
                                        sh "npx bddgen && BASE_URL=https://${base_url} npx playwright test"
                                    }

                                }

                            }

                   /* stage('Integration Tests') {
                        echo "setup mailhog"
                        ecoSystem.vagrant.sshOut 'chmod +x /dogu/resources/setup-mailhog.sh'
                        ecoSystem.vagrant.sshOut "/dogu/resources/setup-mailhog.sh"
                        echo "wait for postfix"
                        timeout(15) {
                            ecoSystem.waitForDogu("postfix")
                        }
                        echo "run integration tests."
                        ecoSystem.runCypressIntegrationTests([
                                cypressImage     : "cypress/included:12.9.0",
                                enableVideo      : params.EnableVideoRecording,
                                enableScreenshots: params.EnableScreenshotRecording,
                                timeoutInMinutes : 45,
                        ])
                    }*/

                    if (params.TestDoguUpgrade != null && params.TestDoguUpgrade) {
                        stage('Upgrade dogu') {
                            // Remove new dogu that has been built and tested above
                            ecoSystem.purgeDogu(doguName)

                            if (params.OldDoguVersionForUpgradeTest != '' && !params.OldDoguVersionForUpgradeTest.contains('v')) {
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

                        /*stage('Integration Tests - After Upgrade') {
                            echo "run integration tests."
                            ecoSystem.runCypressIntegrationTests([
                                    cypressImage     : "cypress/included:12.9.0",
                                    enableVideo      : params.EnableVideoRecording,
                                    enableScreenshots: params.EnableScreenshotRecording,
                                    timeoutInMinutes : 45,
                            ])
                        }*/
                    }

                    if (gitflow.isReleaseBranch()) {
                        String releaseVersion = git.getSimpleBranchName();

                        stage('Finish Release') {
                            gitflow.finishRelease(releaseVersion)
                        }

                        stage('Push Dogu to registry') {
                            ecoSystem.push("/dogu")
                        }

                        stage('Add Github-Release') {
                            github.createReleaseWithChangelog(releaseVersion, changelog)
                        }
                    } else if (gitflow.isPreReleaseBranch()) {
                        // push to registry in prerelease_namespace
                        stage('Push Prerelease Dogu to registry') {
                            ecoSystem.pushPreRelease("/dogu")
                        }
                    }

                } finally {
                    stage('Clean') {
                        ecoSystem.destroy()
                        sh "rm -f ${WORKSPACE}/app/src/main/ui/.npmrc"
                    }
                }

                // Archive Unit and integration test results, if any
                junit allowEmptyResults: true, testResults: '**//*  *//* target/failsafe-reports/TEST-*.xml,**//*  *//* target/surefire-reports/TEST-*.xml,**//*  *//* target/jest-reports/TEST-*.xml'

                mailIfStatusChanged(findEmailRecipients(defaultEmailRecipients))


            }
        }
    }
)

void gitWithCredentials(String command) {
    withCredentials([usernamePassword(credentialsId: 'cesmarvin', usernameVariable: 'GIT_AUTH_USR', passwordVariable: 'GIT_AUTH_PSW')]) {
        sh(
                script: "git -c credential.helper=\"!f() { echo username='\$GIT_AUTH_USR'; echo password='\$GIT_AUTH_PSW'; }; f\" " + command,
                returnStdout: true
        )
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

void stageStaticAnalysisSonarQube() {
    def scannerHome = tool name: 'sonar-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    withSonarQubeEnv {
        sh "git config 'remote.origin.fetch' '+refs/heads/*:refs/remotes/origin/*'"
        gitWithCredentials("fetch --all")

        if (currentBranch == productionReleaseBranch) {
            echo "This branch has been detected as the production branch."
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME}"
        } else if (currentBranch == developmentBranch) {
            echo "This branch has been detected as the development branch."
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME}"
        } else if (env.CHANGE_TARGET) {
            echo "This branch has been detected as a pull request."
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.pullrequest.key=${env.CHANGE_ID} -Dsonar.pullrequest.branch=${env.CHANGE_BRANCH} -Dsonar.pullrequest.base=${developmentBranch}"
        } else if (currentBranch.startsWith("feature/")) {
            echo "This branch has been detected as a feature branch."
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME}"
        } else {
            echo "This branch has been detected as a miscellaneous branch."
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME} "
        }
    }
    timeout(time: 2, unit: 'MINUTES') { // Needed when there is no webhook for example
        def qGate = waitForQualityGate()
        if (qGate.status != 'OK') {
            unstable("Pipeline unstable due to SonarQube quality gate failure")
        }
    }
}
