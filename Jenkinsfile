pipeline {
    agent any  // Use the main Jenkins server to start Vagrant

    stages {
        stage('Start Windows VM') {
            steps {
                sh 'vagrant up'
            }
        }

        stage('Register Windows Agent') {
            steps {
                script {
                    sleep 60 // Wait for the agent to come online
                }
            }
        }

        stage('Checkout Code') {
            agent { label 'Windows-Vagrant-Agent' }  // Run on the Windows VM
            steps {
                checkout scm
            }
        }

        stage('Build Windows App') {
            agent { label 'Windows-Vagrant-Agent' }
            steps {
                bat './gradlew packageDistributionForCurrentOS'
            }
        }

        stage('Create Windows Installer') {
            agent { label 'Windows-Vagrant-Agent' }
            steps {
                bat './gradlew createDistributable'
            }
        }

        stage('Archive Installer') {
            agent { label 'Windows-Vagrant-Agent' }
            steps {
                archiveArtifacts artifacts: 'build/compose/binaries/**/msi/*.msi', fingerprint: true
            }
        }

        stage('Destroy VM') {
            steps {
                sh 'vagrant destroy -f'
            }
        }
    }
}