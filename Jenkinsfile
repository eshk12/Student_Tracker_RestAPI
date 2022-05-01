pipeline {
    agent any

    options {
        timestamps()
        timeout(time:15, unit:'MINUTES')
        
    }

    stages {

        stage('checkout') {
            steps {
                deleteDir()
                echo 'Pulling... ' + env.GIT_BRANCH
                checkout scm
                echo "commit hash : ${env.GIT_COMMIT}, tag_name: ${env.TAG_NAME}, author: ${env.GIT_AUTHOR_NAME}"
                }
        }
        
        stage('build') {
            steps {
                echo "building the application from ${env.GIT_BRANCH}"
                sh "mvn verify"
                sh "docker build -t app ."
                sh "docker-compose up -d"
                sh "sleep 120"
                sh "docker-compose down"
                }
        }

        stage('test') {
            steps {
                echo "Test"
                }
        }

    }

    post {
    always {
        echo "Job done"
        }
    }
}
