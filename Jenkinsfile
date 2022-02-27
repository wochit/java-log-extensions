def service_name = env.JOB_NAME.substring(0, env.JOB_NAME.indexOf('/')).toLowerCase()
def branch_name = env.JOB_NAME.replaceAll(service_name + '/','').replaceAll('\\.','-').replaceAll('_','-')

pipeline {
  options {
    disableConcurrentBuilds()
    }
  agent {
    kubernetes {
      label "jenkins-slave-${service_name}-${branch_name}${env.BUILD_NUMBER}"
      defaultContainer 'maven'
      yaml """
metadata:
  namespace: ci-cd-tools
  labels:
    some-label: some-label-value
spec:
  containers:
    - name: gradle
      image: gradle:6-jdk8
      command:
        - cat
      tty: true
      volumeMounts:
        - mountPath: /tmp/
          name: settings-xml-vol
  nodeSelector:
    nodegroup-type: cicd-workloads
  imagePullSecrets:
    - docker-hub-cred
  volumes:
    - name: settings-xml-vol
      secret:
        secretName: settings-xml
"""
    }
  }
  stages
  {
    stage('gradle')
    {
      steps
      {
        container('gradle')
        {
          sh 'mkdir -p ~/.m2/ && cp /tmp/settings.xml ~/.m2/'
          sh """
          gradle clean  build publish -x test
          """
        }
      }
    }
  }
  post {
    success {
      slackSend (channel: '#jenkins-deploy', color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
      }
    failure {
      slackSend (channel: '#jenkins-deploy', color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
      }
    }
}
