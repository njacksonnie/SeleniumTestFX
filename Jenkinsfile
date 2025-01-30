pipeline {
    agent any

    environment {
        JAVA_HOME = tool name: 'jdk17', type: 'jdk'
        MAVEN_HOME = tool name: 'maven3'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
        GITHUB_REPO_URL = 'https://github.com/njacksonnie/SeleniumTestFX.git'
    }

    tools {
        jdk 'jdk17' // Ensure JDK 17 is installed in Jenkins
        maven 'maven3' // Ensure Maven 3.x is installed in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'test_listner', url: env.GITHUB_REPO_URL
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh 'mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng/runChrome.xml'
                    } catch (Exception e) {
                        echo "Tests failed, but continuing to generate reports..."
                    }
                }
            }
        }

        stage('Verify Reports') {
            steps {
                script {
                    def testOutputExists = fileExists 'test-output/extent-report.html'
                    if (!testOutputExists) {
                        error("The 'test-output/extent-report.html' file does not exist. Check your test configuration.")
                    }
                }
            }
        }

        stage('Publish Reports') {
            steps {
                // Publish JUnit test results
                junit '**/target/surefire-reports/*.xml'

                // Publish ExtentReports HTML report
                publishHTML(target: [
                    reportName: 'Extent Report',
                    reportDir: 'test-output',
                    reportFiles: 'extent-report.html',
                    alwaysLinkToLastBuild: true
                ])

                // Archive artifacts
                archiveArtifacts artifacts: 'test-output/**/*, screenshots/**/*, logs/application.log', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}