//Annotate Event to Grafana
def annotateEventToGrafana(event) {
    withCredentials([[$class: 'StringBinding', credentialsId: 'editor', variable: 'grafanaToken']]) {
        grafanaToken = "${grafanaToken}"

        if (event == 'deploy') {
            listener_type = "DeployOnProd"
        } else {
            listener_type = "DeployOnStage"
        }

sh"""
  cat << EOF > EventAnnotation.txt
{
  "text": "${event.capitalize()} Branch: '${params.BRANCH}' To Service:'${JOB_BASE_NAME}' ${listener_type} \\n\\n
    <a href=\\"${BUILD_URL}\\">Jenkins #${BUILD_NUMBER} (DEPLOY Logs)</a>",
  "tags": [ "${event}", "env:Prod", "application:${JOB_BASE_NAME}","Branch:${params.BRANCH}" ]
}
EOF
 curl -s -X POST http://13.233.98.2:3000/api/annotations \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer ${grafanaToken}" \
            --data @EventAnnotation.txt

        """

    }
}
