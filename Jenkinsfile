pipeline {
    agent {
        docker {
            image 'eclipse-temurin:17-jdk-jammy'
            args '--platform linux/arm64/v8 --shm-size=2g'
        }
    }

    environment {
        CHROME_REPO = "deb [arch=arm64] http://dl.google.com/linux/chrome/deb/ stable main"
        LATEST_CHROME_DRIVER_VERSION = ""
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
                    # Install Chrome dependencies
                    apt-get update
                    apt-get install -y wget gnupg curl unzip xvfb \
                        libnss3 libx11-6 libgconf-2-4 fonts-liberation

                    # Add Chrome repo
                    echo "$CHROME_REPO" > /etc/apt/sources.list.d/google-chrome.list
                    wget -qO- https://dl.google.com/linux/linux_signing_key.pub | apt-key add -

                    # Install Chrome
                    apt-get update
                    apt-get install -y google-chrome-stable --no-install-recommends

                    # Get ChromeDriver version using awk (no Perl/jq needed)
                    LATEST_CHROME_DRIVER_VERSION=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json" \\
                        | awk -F'"' '/"version":/{print $4; exit}')

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
                    mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng/runChrome.xml \
                        -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver \
                        -Dchrome.binary=/usr/bin/google-chrome
                '''
            }
        }

        // Add your report stages
        stage('Verify Reports') { steps { echo 'Verify reports' } }
        stage('Publish Reports') { steps { echo 'Publish reports' } }
    }

    post {
        always {
            cleanWs()
        }
    }
}