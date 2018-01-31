# Spark Police Department Incidents data example

This is a sample project with the scope of understanding the Banzai Cloud's [Banzai Pipeline](https://github.com/banzaicloud/pipeline) CI/CD workflow.

The project consists of a simple Spark job that performs basic operations on the SF police incident data.

The format and the description of the data is available here
[here](https://data.sfgov.org/Public-Safety/Police-Department-Incidents/tmnf-yvry "SFData")

## Prerequisites

* A Banzai Cloud Control Plane needs to be running and accessible
* The data from the above mentioned location needs to be made available for your cluster (S3 or WASB)

## Configuration required to hook in into the [Banzai Pipeline](https://github.com/banzaicloud/pipeline) CI/CD workflow

In order for a project to be part of a Banzai Pipeline CI/CD workflow it **must contain** a specific configuration file: ```.pipeline.yml``` in its root folder.

> In short: the configuration file contains the steps the project needs to go through the workflow from provisioning the environment, building the code, running tests to being deployed and executed along with project specific variables (eg.:credentials, program arguments, etc needed to assemble the execution command - `spark-submit` in this case).

Depending on the chosen cloud provider we provide templates to be used to start ([.pipeline.yml.aws.template](.pipeline.yml.aws.template) and [.pipeline.yml.azure.template](.pipeline.yml.azure.template)) Make a copy of the appropriate template and name it *.pipeline.yml*

The templates come the location of the dataset pointing to locations used by us for testing (Warning: You must change these locations!). Edit the '.pipeline.yml' file and change the value of the `spark_app_args`, `--dataPath` argument to point to the proper location of your dataset:

```yml
...
spark_app_args: --dataPath s3a://<your-bucket>/<your-pdi-data>.csv
```

or

```yml
...
spark_app_args: --dataPath wasb://[blobcontainer]@[storage_account_name].blob.core.windows.net/Police_Department_Incidents.csv
```

## Set these secrets on the CI user interface

### URL endpoint for the Pipeline API

    plugin_endpoint: http://<host/ip>/pipeline/api/v1

The pipeline runs on the Banzai Cloud Control Plane, so this value should point to the hostname/ip of the machine hosting it.

### Credentials for the Pipeline API

    plugin_user: <admin>
    plugin_password: <example>

These credentials specify the user of the Pipeline API.
