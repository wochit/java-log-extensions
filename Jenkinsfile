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
  nodeSelector:
    nodegroup-type: cicd-workloads
  imagePullSecrets:
    - docker-hub-cred
"""
    }
  }
    parameters
    {
      booleanParam(name: 'DEPLOY_RELEASE', defaultValue: false, description: 'Should deploy to releases or snapshot repo        ')
    }
  stages
  {
    stage('gradle')
    {
      steps
      {
        container('gradle')
        {
        if(params.DEPLOY_RELEASE == 'false')
          {
          sh """
          gradle clean  build publish -x test
          """
          }
          else{
          gradle clean  build publish -Prelease=true -x test
          }
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
