pipeline {
    agent any

    environment {
        IMAGE_NAME = 'productos'
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
        NAMESPACE  = 'productos'
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

        stage('Deploy') {
            steps {
                sh "sed 's|IMAGE_TAG|${IMAGE_TAG}|' k8s/productos.yaml | kubectl apply -f -"
                sh "kubectl rollout status deployment/productos -n ${NAMESPACE} --timeout=120s"
            }
        }

        stage('Verify') {
            steps {
                sh 'curl -fsS --retry 5 --retry-delay 3 http://productos.localhost/api/v1/products'
            }
        }
    }
}
