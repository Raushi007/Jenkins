//Annotate Event to Grafana
def annotateEventToGrafana(event) {
    withCredentials([[$class: 'StringBinding', credentialsId: 'viewer', variable: 'grafanaToken']]) {
        grafanaToken = "${grafanaToken}"

        if (event == 'deploy') {
            listener_type = "DeployOnProd"
        } else {
            listener_type = "DeployOnStage"
        }

sh"""
  cat << EOF > EventAnnotation.txt

{
  "text": "${event.capitalize()} commit ${GIT_COMMIT} For >>Service:'${JOB_NAME}' ${listener_type} \\n\\n
    <a href=\\"${GIT_URL}/commit/${LOGNAME}\\">LiveListener: (${GIT_COMMIT})</a>\\n
    <a href=\\"${BUILD_URL}\\">Jenkins #${BUILD_NUMBER} (DEPLOY Logs)</a>",
  "tags": [ "${event}", "env:${params.ENV}", "application:fakeApp", "commit:fakeCommitId" ]
}

EOF

 curl -s -X POST http://43.204.236.246:3000/api/annotations \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${grafanaToken}" \
            --data @EventAnnotation.txt
        
        """

    }
}

