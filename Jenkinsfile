pipeline {
    agent {
        docker {
            image 'eclipse-temurin:17-jdk-jammy'  // ARM64-compatible base image
            args '--platform linux/arm64/v8 --shm-size=2g'  // M1 settings
        }
    }

    environment {
        CHROME_REPO = "deb [arch=arm64] http://dl.google.com/linux/chrome/deb/ stable main"
        LATEST_CHROME_DRIVER_VERSION = ""  // Will be populated dynamically
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'test_listner',
                     url: 'https://github.com/njacksonnie/SeleniumTestFX.git'
            }
        }

        stage('Setup Chrome') {
            steps {
                sh '''
                    # Install latest ARM Chrome
                    apt-get update
                    apt-get install -y wget gnupg curl unzip xvfb \
                        libnss3 libx11-6 libgconf-2-4 fonts-liberation

                    # Add Chrome repo
                    echo "$CHROME_REPO" > /etc/apt/sources.list.d/google-chrome.list
                    wget -qO- https://dl.google.com/linux/linux_signing_key.pub | apt-key add -

                    # Install latest Chrome
                    apt-get update
                    apt-get install -y google-chrome-stable --no-install-recommends

                    # Get matching ChromeDriver version
                    CHROME_VERSION=$(google-chrome --version | awk '{print $3}')
                    LATEST_CHROME_DRIVER_VERSION=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json" \
                        | grep -Po '"version": "\K\d+\.\d+\.\d+\.\d+' | head -1)

                    # Download and install ARM ChromeDriver
                    wget -q "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${LATEST_CHROME_DRIVER_VERSION}/linux-arm64/chromedriver-linux-arm64.zip"
                    unzip chromedriver-linux-arm64.zip
                    mv chrome-for-testing/${LATEST_CHROME_DRIVER_VERSION}/linux-arm64/chromedriver /usr/local/bin/
                    chmod +x /usr/local/bin/chromedriver
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh '''
                    # Pass paths to Maven
                    mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng/runChrome.xml \
                        -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver \
                        -Dchrome.binary=/usr/bin/google-chrome
                '''
            }
        }

        // Keep your existing report stages
        stage('Verify Reports') { ... }
        stage('Publish Reports') { ... }
    }

    post {
        always {
            cleanWs()
        }
    }
}