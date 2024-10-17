pipeline {
    agent any

    tools {
        // Tools
        maven 'Maven-3.9.8'
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        // Checkout the code from the Git repository
                        git url: 'https://github.com/abijith-suresh/authentication-service', branch: 'main'
                    }
                }
            }
        }

        stage('Pre-Build') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        // Pre-build Docker cleanup steps
                        bat '''
                        docker stop authentication-sr || true
                        docker rm authentication-sr || true
                        docker rmi -f authentication-sr:latest || true
                        '''
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        // Build the project using Maven
                        bat 'mvn clean install'
                    }
                }
            }
        }

        stage('Create Docker Network') {
             steps {
                script {
                    // Create a Docker network, ignore errors if it already exists
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        bat 'docker network create my-network || echo "Network already exists"'
                    }
                }
            }
        }

        stage('Post-Build') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        // Build and run the Docker container
                        bat '''
                        docker build -t authentication-sr:latest .
                        docker run -d --network my-network -p 9095:9095 --name authentication-sr authentication-sr:latest
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed!'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline had errors, but execution continued.'
        }
    }
}
