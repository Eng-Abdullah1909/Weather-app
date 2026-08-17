pipeline {
    agent any


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
        //             sh 'mvn verify'  // Runs *IT.java
        //         }
        //     }
        //     post {
        //         always {
        //             junit 'backend/target/failsafe-reports/*.xml'
        //         }
        //     }
        // }
 


        stage('Build Docker Images') {
            steps {
                sh "docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                sh "docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
            }
        }    


        stage('Trivy Vulnerability Scan') {
            steps {
                sh """
                    # Scan Backend with HTML report
                    trivy image --severity HIGH,CRITICAL --exit-code 0 \
                        --format html -o trivy-backend-report.html \
                        ${BACKEND_IMAGE}:${IMAGE_TAG}
                    
                    # Scan Frontend with HTML report
                    trivy image --severity HIGH,CRITICAL --exit-code 0 \
                        --format html -o trivy-frontend-report.html \
                        ${FRONTEND_IMAGE}:${IMAGE_TAG}
                """
            }
            post {
                always {
                    // Archive reports
                    archiveArtifacts artifacts: 'trivy-*.html'
                    
                    // Publish HTML report (requires HTML Publisher plugin)
                    publishHTML([
                        reportDir: '.',
                        reportFiles: 'trivy-backend-report.html',
                        reportName: 'Trivy Backend Scan'
                    ])
                    publishHTML([
                        reportDir: '.',
                        reportFiles: 'trivy-frontend-report.html',
                        reportName: 'Trivy Frontend Scan'
                    ])
                }
            }
        }





        
    }
}