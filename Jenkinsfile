String addFix(String version, String fixNumber){
    return version+"."+fixNumber
}
String incrementBranch(String version){
    splitVersion = version.split("\\.") 
    return splitVersion[0]+"."+splitVersion[1]+"."+splitVersion[2].next()
}
String branch_and_commit(String branch, String massage){
    return branch+" "+massage
}
String e2e = "e2e"


pipeline {
    agent any

    options {
        timestamps()
        timeout(time:15, unit:'MINUTES')
        
    }

    stages {
        /*
        stage('Prep') {
            steps {
                git branch: env.GIT_BRANCH, credentialsId: 'baruch.bazak.devops', url: 'git@github.com:eshk12/Student_Tracker_RestAPI.git'
                script {
                    branchName = env.GIT_BRANCH.split('/')
                    if (branchName[0] == 'feature' || branchName[0] == 'release') {
                        env.VERSION = branchName[1]
                        echo "${env.VERSION}"
                        sh "git tag"
                        TAG = sh (
                                    script: "git tag | tail -n 1 | grep ${env.VERSION} | cut -d '.' -f3",
                                    returnStdout: true
                                    ).trim()

                        env.NEW_TAG = (TAG == "") ? addFix(branchName[1],"0") : addFix(branchName[1],TAG.next())            
                        echo "My new tag: ${env.NEW_TAG}"
                    }
                    commit = sh (
                        script: "git log -1 --oneline",
                        returnStdout: true,
                        ).trim()
                }
            }
        }
        */
        stage('checkout') {
            steps {
                deleteDir()
                echo 'Pulling... ' + env.GIT_BRANCH
                checkout scm
                echo "commit hash : ${env.GIT_COMMIT}, tag_name: ${env.TAG_NAME}, author: ${env.GIT_AUTHOR_NAME}"
                }
        }
        
        stage ('Build') {
            steps{
                script{
                    echo "building the application from ${env.GIT_BRANCH}"
                    if (env.GIT_BRANCH ==~ "main"){
                        sh "mvn versions:set -DnewVersion=${env.VERSION}.${env.NEW_TAG}"
                    }
                    sh """mvn verify
                          docker build -t rest ."""
                }
            }
        }

        stage('Test') {
            steps {
                echo "Test"
                sh """docker-compose up -d
                      sleep 20
                      docker-compose down"""
                }
        }
        
        stage('Publish')
            steps {
                sh """docker tag rest:latest 274129698771.dkr.ecr.eu-central-1.amazonaws.com/rest:${env.VERSION}.${env.NEW_TAG}
                      docker push 274129698771.dkr.ecr.eu-central-1.amazonaws.com/rest:${env.VERSION}.${env.NEW_TAG}"""
            }
        
        }
    
        stage('Deploy') {
            steps {
                if (env.GIT_BRANCH ==~ "main"){
                    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins', keyFileVariable: 'SSH_KEY', usernameVariable: 'ubuntu')]) {
                      sh'''
                        ssh -o StrictHostKeyChecking=no -tt -i $SSH_KEY ubuntu@18.157.123.152 "bash -c 'docker-compose down'
                        '''
                    }
                }
                    
            }
            
        }
    }

    post {
    always {
        echo "Job done!"
        }
    }
