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
    <a href=\\"${BUILD_URL}\\">Jenkins #${BUILD_NUMBER} (DEPLOY Logs)</a>",
  "tags": [ "${event}", "env:fakeEnv", "application:fakeApp", "commit:fakeCommitId" ]
}

EOF

 curl -s -X POST http://43.204.130.109:3000/api/annotations \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${grafanaToken}" \
            --data @EventAnnotation.txt
        
        """

    }
}

