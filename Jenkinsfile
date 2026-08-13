pipeline {
    agent any


    environment {
        DOCKERHUB_CREDS = credentials('dockerhub-creds')
        DOCKERHUB_USER  = "${DOCKERHUB_CREDS_USR}"
        IMAGE_TAG       = "${env.BUILD_NUMBER}"
        BACKEND_IMAGE   = "${DOCKERHUB_USER}/weather-backend"
        FRONTEND_IMAGE  = "${DOCKERHUB_USER}/weather-frontend"
        KUBE_NAMESPACE  = "weather-app"
    }    

    stages {

        stage('checkout') {
            steps{
                checkout scm
            }

        }

        stage('Build Backend JAR') {Credentials Binding
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh "docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                sh "docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
            }
        }    
        
    }
}