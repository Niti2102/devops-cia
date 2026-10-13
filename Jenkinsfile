
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build and Test') {
            steps {
                dir(env.WORKSPACE) {
                    echo 'Running Maven clean test from the repository root.'
                    bat 'mvn -B clean test'
                }
            }
            post {
                always {
                    echo 'Publishing JUnit test reports.'
                    junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Package') {
            when {
                expression { currentBuild.currentResult == 'SUCCESS' }
            }
            steps {
                dir(env.WORKSPACE) {
                    echo 'Tests passed; packaging from the repository root.'
                    bat 'mvn -B package'
                }
            }
        }

        stage('Archive Artifact') {
            when {
                expression { currentBuild.currentResult == 'SUCCESS' }
            }
            steps {
                echo 'Archiving the course-registration application JAR.'
                archiveArtifacts(
                    artifacts: 'course-registration-app/target/*.jar',
                    fingerprint: true
                )
            }
        }
    }
}
