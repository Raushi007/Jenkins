//Annotate Event to Grafana
def annotateEventToGrafana(event) {
    withCredentials([[$class: 'StringBinding', credentialsId: 'grafana_token', variable: 'grafanaToken']]) {
        grafanaToken = "${grafanaToken}"

        if (event == 'deploy') {
            listener_type = "DeployonProd"
        } else {
            listener_type = "Live"
        }

sh"""
  cat << EOF > EventAnnotation.txt

{
  "text": "${event.capitalize()} commit ${GIT_COMMIT} to '${JOB_NAME}' ${listener_type} \\n\\n
    <a href=\\"${GIT_URL}/commit/${LiveColorCommitId}\\">LiveListener: (${LiveColor}) (${LiveColorCommitId})</a>\\n
    <a href=\\"${JOB_URL}/console\\">Jenkins #${BUILD_NUMBER} (DEPLOY Logs)</a>",
  "tags": [ "${event}", "env:fakeEnv", "application:fakeApp", "commit:fakeCommitId" ]
}

EOF

 curl -s -X POST http://13.127.138.84:3000/api/annotations \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${grafanaToken}" \
            --data @EventAnnotation.txt
        
        """

    }
}

