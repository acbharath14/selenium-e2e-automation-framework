pipeline {
    agent any

    tools {
        // Requires a Jenkins JDK installation named 'jdk-17' (Manage Jenkins → Tools)
        jdk 'jdk-17'
        // Requires a Maven installation named 'maven-3' (Manage Jenkins → Tools)
        maven 'maven-3'
    }

    options {
        // Keep the last 10 builds; abort if a newer build starts
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 60, unit: 'MINUTES')
    }

    parameters {
        booleanParam(name: 'HEADLESS', defaultValue: true,
            description: 'Run Chrome in headless mode')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Preflight') {
            steps {
                sh 'chmod +x scripts/preflight.sh && ./scripts/preflight.sh'
            }
        }

        stage('Test') {
            steps {
                sh "mvn -B clean test -Dheadless=${params.HEADLESS}"
            }
        }
    }

    post {
        always {
            // Surefire results → Jenkins test trend
            junit 'target/surefire-reports/*.xml'

            // Allure report (requires the Allure Jenkins plugin)
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            ])

            // JaCoCo coverage (requires the JaCoCo Jenkins plugin)
            jacoco(
                execPattern: 'target/jacoco.exec',
                classPattern: 'target/classes',
                sourcePattern: 'src/main/java',
                exclusionPattern: '**/*Test*'
            )

            archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
        }
    }
}
