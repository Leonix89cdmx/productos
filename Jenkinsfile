pipeline {
    agent any

    environment {
        IMAGE_NAME = 'productos'
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
        }

        stage('Build image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }
    }
}
