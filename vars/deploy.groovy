def call(Map deploymentParams) {
pipeline {
    agent any
    
    parameters {
        choice(choices: ['Deployment' , 'Rollback'], description: '',name: 'Process')
        choice(choices: ['QA' , 'Pre-prod'], description: '',name: 'REQUESTED_ACTION')
        string(name: 'IP', description: 'Please enter you backend IP')
        //gitParameter branchFilter: 'origin/(.*)', defaultValue: 'master', name: 'BRANCH', type: 'PT_BRANCH', useRepository: 'https://github.com/vennavenkatesh/devops-sample.git'
    }
    stages {
        stage ("process") {
            when {
            expression { params.Process == 'Deployment' }
            }
   
   
    stages {
        stage("Git checkout") {
            steps {
                script {
                    if (params.BRANCH == 'master') {
                        echo 'I only execute on the master branch'
                        git branch: deploymentParams.BRANCH, url: "https://github.com/vennavenkatesh/devops-sample.git" , poll: true
                    } 
                    else {
                        echo 'I execute elsewhere'
                    }
                }    
            }
        }
        
        stage("sonar") {
            when {
                expression { params.REQUESTED_ACTION == 'QA' }
            }
            steps {
                echo "executing the Sonar-Report"
               // sh "cd $WORKSPACE && mvn sonar:sonar"
            }
        }
        

        stage("Test-cases & Build") {
            steps {
                echo "executing the test cases"
                sh "cd $WORKSPACE && mvn clean install"
            }
        }
        
         stage("Nexus-Deploy") {
            steps {
                echo "executing the test cases"
                //sh "cd $WORKSPACE && mvn clean deploy"
            }
        }

        stage("IP") {
            steps {
                    
                        echo "Print the workspave path"
                        echo "Hello ${params.NAME}"
                        sh "echo $WORKSPACE"
                       /* sh '''
                        #!/bin/bash
                        ssh tomcat@$IP << EOF
                        cd /opt/apache-tomcat-9.0.56/webapps
                        mv *.war /opt/apache-tomcat-9.0.56/backup
                        exit 0
                        EOF'''*/
                }
            }
        

        stage("Deploy-tomcat-server") {
            steps {
                echo "Deploy into tomcat server"
                sh "cd $WORKSPACE/target/"
               // sh "cd $WORKSPACE/target/ && scp -r *.war tomcat@3.109.56.173:/opt/apache-tomcat-9.0.56/webapps/"
                
                echo " Deployment has been completed successfully"
            }    
        }
    }

}

    stage("process-rollback") {
        when {
        expression { params.Process == 'Rollback' }
        }
        stages {
            stage("Rollback") {
                steps {
                echo "Rollback-process"
                //git branch: "${params.BRANCH}", url: "https://github.com/vennavenkatesh/devops-sample.git" , poll: true
                }
            }        
        }
    }
}

}
