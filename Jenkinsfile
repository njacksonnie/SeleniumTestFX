pipeline {
    agent {
        docker {
            image 'eclipse-temurin:17-jdk-jammy'
            args '--platform linux/arm64/v8 --shm-size=2g'
        }
    }

    environment {
        CHROME_REPO = "deb [arch=arm64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'test_listner',
                     url: 'https://github.com/njacksonnie/SeleniumTestFX.git'
            }
        }

        stage('Setup Chrome & ChromeDriver') {
            steps {
                sh '''
                    #!/bin/bash
                    set -e  # Exit immediately on error

                    # Install system dependencies
                    apt-get update
                    apt-get install -y wget gnupg curl unzip xvfb \
                        libnss3 libx11-6 libgconf-2-4 fonts-liberation

                    # Configure Chrome repository securely
                    wget -qO- https://dl.google.com/linux/linux_signing_key.pub \
                        | tee /usr/share/keyrings/google-chrome-keyring.gpg > /dev/null
                    echo "$CHROME_REPO" | tee /etc/apt/sources.list.d/google-chrome.list

                    # Install Chrome
                    apt-get update
                    apt-get install -y google-chrome-stable --no-install-recommends

                    # Get ChromeDriver version from official API
                    CHROME_VERSION=$(google-chrome-stable --version | awk '{print $3}')
                    echo "Installed Chrome version: $CHROME_VERSION"

                    # Parse ChromeDriver version matching Chrome
                    LATEST_CHROME_DRIVER_VERSION=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json" \
                        | awk -F'"' '/"version":/ && /Stable/ {print $4; exit}')
                    echo "Using ChromeDriver version: $LATEST_CHROME_DRIVER_VERSION"

                    # Download and install ChromeDriver
                    wget -q "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${LATEST_CHROME_DRIVER_VERSION}/linux-arm64/chromedriver-linux-arm64.zip"
                    unzip -o chromedriver-linux-arm64.zip -d /tmp/chromedriver
                    mv /tmp/chromedriver/chrome-for-testing/${LATEST_CHROME_DRIVER_VERSION}/linux-arm64/chromedriver /usr/local/bin/
                    chmod +x /usr/local/bin/chromedriver

                    # Verify installation
                    echo "ChromeDriver path: $(which chromedriver)"
                    chromedriver --version
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh '''
                    #!/bin/bash
                    set -e  # Exit on test failure

                    # Dynamically locate Chrome binary
                    CHROME_BIN=$(which google-chrome-stable || which google-chrome)
                    echo "Using Chrome binary at: $CHROME_BIN"

                    # Execute tests with explicit paths
                    mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng/runChrome.xml \
                        -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver \
                        -Dchrome.binary="$CHROME_BIN"
                '''
            }
        }

        stage('Reports') {
            parallel {
                stage('Verify Reports') {
                    steps {
                        sh 'echo "Verify test reports"'
                        // Add actual verification commands here
                    }
                }
                stage('Publish Reports') {
                    steps {
                        sh 'echo "Publish test reports"'
                        // Add publishing logic here
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
            script {
                echo "Pipeline completed - ${currentBuild.result}"
            }
        }
        success {
            slackSend color: 'good', message: "Build ${env.BUILD_NUMBER} succeeded!"
        }
        failure {
            slackSend color: 'danger', message: "Build ${env.BUILD_NUMBER} failed!"
        }
    }
}