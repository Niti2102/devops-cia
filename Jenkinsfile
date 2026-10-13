
pipeline {
    agent any

    tools {
        maven 'Maven 3.9.16'
    }

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
                    script {
                        def reports = 'course-registration-app/target/surefire-reports/TEST-*.xml'
                        if (fileExists('course-registration-app/target/surefire-reports')) {
                            junit reports
                        } else {
                            echo 'No Surefire reports were generated; check the Maven build output above.'
                        }
                    }
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
