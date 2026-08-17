pipeline {
    agent any

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds abortPrevious: true
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        DOCKERHUB_CREDS = credentials('dockerhub-creds')
        DOCKERHUB_USER  = "${DOCKERHUB_CREDS_USR}"
        IMAGE_TAG       = "${env.BUILD_NUMBER}"
        BACKEND_IMAGE   = "${DOCKERHUB_USER}/weather-backend"
        FRONTEND_IMAGE  = "${DOCKERHUB_USER}/weather-frontend"
    }    

    stages {

        stage('checkout') {
            steps{
                checkout scm
            }
        }

        stage('Build & Lint Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests -Dcheckstyle.skip=true'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh "docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                sh "docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
            }
        }    

        stage('Trivy Vulnerability Scan') {
            steps {
                sh """
                    # Scan Backend with JSON report
                    trivy image --severity HIGH,CRITICAL --exit-code 0 \
                        --format json -o trivy-backend-report.json \
                        ${BACKEND_IMAGE}:${IMAGE_TAG}
                    
                    # Scan Frontend with JSON report
                    trivy image --severity HIGH,CRITICAL --exit-code 0 \
                        --format json -o trivy-frontend-report.json \
                        ${FRONTEND_IMAGE}:${IMAGE_TAG}
                """
            }
            post {
                always {
                    // Archive JSON reports
                    archiveArtifacts artifacts: 'trivy-*.json'
                }
            }
        }
    }
}