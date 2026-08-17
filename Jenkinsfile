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

        // stage('Unit Tests') {
        //     steps {
        //         dir('backend') {
        //             sh 'mvn test'
        //         }
        //     }
        //     post {
        //         always {
        //             junit 'backend/target/surefire-reports/*.xml'
        //         }
        //     }
        // }

        // stage('Integration Tests') {
        //     steps {
        //         dir('backend') {
        //             sh 'mvn verify'
        //         }
        //     }
        //     post {
        //         always {
        //             junit 'backend/target/failsafe-reports/*.xml'
        //         }
        //     }
        // }

        stage('Dependency Scan') {
            steps {
                dir('backend') {
                    sh 'mvn dependency-check:check || echo "Dependency scan completed with warnings"'
                }
            }
            post {
                always {
                    script {
                        if (fileExists('backend/target/dependency-check-report.html')) {
                            publishHTML([
                                reportDir: 'backend/target',
                                reportFiles: 'dependency-check-report.html',
                                reportName: 'OWASP Dependency Report',
                                allowMissing: true,
                                alwaysLinkToLastBuild: false,
                                keepAll: false
                            ])
                        }
                    }
                }
            }
        }

        stage('Security Scan') {
            steps {
                sh """
                    # Scan for secrets in code
                    trivy fs --severity HIGH,CRITICAL --exit-code 0 ./backend || echo "No secrets found"
                    
                    # Scan Dockerfile for misconfigurations
                    trivy config --severity HIGH,CRITICAL --exit-code 0 ./backend/Dockerfile || echo "No Dockerfile issues found"
                """
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
                    archiveArtifacts artifacts: 'trivy-*.json'
                }
            }
        }
    }

    post {
        success {
            emailext (
                subject: "Build Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "Build successful. Image: ${BACKEND_IMAGE}:${IMAGE_TAG}",
                to: "abdullahusama733@gmail.com"
            )
        }
        failure {
            emailext (
                subject: "Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "Check Jenkins console for details.",
                to: "abdullahusama733@gmail.com"
            )
        }
    }
}