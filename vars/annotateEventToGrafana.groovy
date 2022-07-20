//Annotate Event to Grafana
def annotateEventToGrafana(event) {
    withCredentials([[$class: 'StringBinding', credentialsId: 'grafana-today', variable: 'grafanaToken']]) {
        grafanaToken = "${grafanaToken}"

        if (event == 'deploy') {
            listener_type = "Test"
        } else {
            listener_type = "Live"
        }

sh"""
  cat << EOF > EventAnnotation.txt

{
  "text": "${event.capitalize()} commit fakeCommitId to fakeApp fakeregion fakeEnv fakeListenerType Listener\\n\\n
      <a href=\\"${GIT_URL}/commit/${BUILD_USER}\\">LiveListener: (${BUILD_USER}) </a>\\n
      <a href=\\"${JOB_BASE_NAME}/commit/${GIT_COMMIT}\\">TestListener: (${BUILD_USER}) (${GIT_COMMIT})</a>\\n
      <a href=\\"${JOB_URL}\\">Jenkins #${BUILD_NUMBER} (DEPLOY Logs)</a>",
  "tags": [ "${event}", "env:fakeEnv", "application:fakeApp", "commit:fakeCommitId" ]
}

EOF

 curl -s -X POST http://52.66.243.121:3000/api/annotations \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${grafanaToken}" \
            --data @EventAnnotation.txt
        
        """

    }
}

