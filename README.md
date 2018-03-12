# Spark Police Department Incidents data example

This is a sample project with the scope of understanding the Banzai Cloud's [Banzai Pipeline](https://github.com/banzaicloud/pipeline) CI/CD workflow.

The project consists of a simple Spark job that performs basic operations on the SF police incident data.

The format and the description of the data is available here
[here](https://data.sfgov.org/Public-Safety/Police-Department-Incidents/tmnf-yvry "SFData")

## Prerequisites

* A Banzai Cloud Control Plane needs to be running and accessible
* The data from the above mentioned location needs to be made available for your cluster (S3 or WASB)


## Setup

* fork the project into your repository
* make a copy of the flow descriptor template corresponding to your chosen cloud provider:
```
cp .pipeline.yml.[azure|aws|gke].template .pipeline.yml
```
* check the flow descriptor file and replace the placeholders with your specific values (cluster name, bucket / folder names etc ...)

> In short: the CI/CD flow descriptor file contains the steps needed from provisioning the environment, building the code, running tests to being deployed and executed along with project specific variables (eg.:credentials, program arguments, etc ...).

* navigate to the CI/CD user interface (that usually runs on the Banzai Cloud Control plane instance)
* enable the project build from the list of available repositories
* add the following secrets to the build:

```
PLUGIN_ENDPOINT = [control-plane]/pipeline/api/v1
PLUGIN_TOKEN = "oauthToken"
```

The project is configured now for the Banzai Cloud CI/CD flow. On each commit to the repository a new flow will be triggered. You can check the progress on the CI/CD user interface.

>  Warning: You must change the dataset locations. Edit the '.pipeline.yml' file and change the value of the `spark_submit_app_args`, `--dataPath` argument to point to the proper location of your dataset:

```yml
...
spark_submit_app_args:
  - --dataPath s3a://<your-bucket>/<your-pdi-data>.csv
```

or

```yml
...
spark_submit_app_args:
  - --dataPath wasb://[blobcontainer]@[storage_account_name].blob.core.windows.net/Police_Department_Incidents.csv
```

or

```yml
...
spark_submit_app_args:
  - --dataPath gs://<your-bucket>/<folder>/<your-pdi-data>.csv
```
