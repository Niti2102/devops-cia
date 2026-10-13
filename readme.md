
# Course Registration Application

## Project Structure

This project uses a multi-module Maven build.

- `pom.xml`: Parent POM, which defines shared Java properties,
  JUnit dependency management, Maven plugins, and the child module.
- `course-registration-app/pom.xml`: Child POM, which inherits
  configuration from the parent.
- `course-registration-app/src/main/java`: Application source code.
- `course-registration-app/src/test/java`: JUnit test classes.

## Build and Test

Run the following commands from the repository root:

```bash
mvn clean test
mvn package
```

The parent POM builds the child module through Maven's reactor.

## Test Reports and Artifact

JUnit reports:
`course-registration-app/target/surefire-reports/`

Packaged JAR:
`course-registration-app/target/`


## Jenkins Declarative Pipeline

The project includes a `Jenkinsfile` at the repository root for a Jenkins Declarative Pipeline targeting a Windows agent.

### Pipeline Stages

1. **Checkout:** Retrieves the source code from the configured repository.
2. **Build and Test:** Runs `mvn -B clean test` from the repository root and publishes JUnit test reports in the stage's `post { always { ... } }` action, including when the test command fails.
3. **Package:** Runs `mvn -B package` from the repository root only when the build result is successful.
4. **Archive Artifact:** Archives the generated application JAR only when the build result is successful. A failed test therefore prevents both packaging and artifact archiving.

### Pipeline Paths

- JUnit reports: `course-registration-app/target/surefire-reports/TEST-*.xml`
- Packaged artifact: `course-registration-app/target/*.jar`

### Execution Environment

The pipeline targets a Windows Jenkins agent and uses Windows `bat` steps. Configure the agent with Java, Maven and Git available on `PATH`, install the Jenkins JUnit plugin, and configure Jenkins to check out this repository. The Maven commands explicitly run in `WORKSPACE`, the repository checkout root.

**Console evidence:** Open the build's **Console Output** in Jenkins. It should show checkout, the `Running Maven clean test from the repository root.` marker, Maven's reactor and test summary, the JUnit publishing marker, and then the package and archive markers on a successful build. If tests fail, inspect the test output and report-publishing action; the `Package` and `Archive Artifact` stages are skipped. Do not present local Maven output as a Jenkins run.

**Execution status:** No Jenkins execution is claimed here. The Jenkinsfile is ready for a Windows Jenkins agent. Local verification (not Jenkins output): `mvn -B clean test` completed with 12 tests, 0 failures, 0 errors and 0 skipped; `mvn -B package` also completed successfully.

## Git Evidence

**Feature branch:** `feature/course-registration-refactor`

The changes were developed on the feature branch and merged into `main`.

Relevant commit history:

- `cef2bff` — Initial student assessment starter
- `135efd9` — Corrected course-capacity boundary
- `db1d98a` — Added course-capacity boundary tests
- `1b2d995` — Increased maximum registered credits to 24
- `0f7a903` — Merged course-registration changes into main

To verify the history:

```bash
git log --oneline --graph --decorate --all
git status
```

## Assigned Change Request — Question Set A

The maximum permitted total registered credits was increased from 20 to 24.

The registration service now permits registration when the proposed total does not exceed 24 credits, provided all other registration rules are satisfied. Registration is rejected if the proposed total exceeds 24 credits.

Focused JUnit tests cover the exact 24-credit boundary, attempts to exceed the limit, and preservation of registration state after rejection. Course-capacity boundary tests were also added.

## Kubernetes Deployment Evidence

The project includes these Kubernetes configuration files:

- `kubernetes/deployment.yaml` — Defines an Nginx Deployment using `nginx:1.27`, initially configured with two replicas.
- `kubernetes/service.yaml` — Defines a NodePort Service exposing port 80 through node port 30080.

The application was deployed locally using Minikube.

**Observed results:**

- Initial Deployment: `2/2` replicas ready.
- After scaling: `3/3` replicas ready.
- Self-healing: After deleting a Pod, Kubernetes created a replacement Pod and restored three running replicas.
- Service: `nginx-service`, type `NodePort`, with port mapping `80:30080/TCP`.

Commands used to verify the deployment:

```bash
kubectl get deployments
kubectl get pods -o wide
kubectl get services
kubectl scale deployment/nginx-deployment --replicas=3
kubectl delete pod <actual-pod-name>
kubectl get pods -w
```

Actual terminal screenshots and command outputs are included as supporting evidence.

## Change Summary and Modified Files

The course-capacity defect was corrected to reject registration when a course is already at capacity. The assigned credit-limit change increased the maximum from 20 to 24 credits.

Relevant project files:

- `pom.xml` — Parent Maven configuration.
- `course-registration-app/pom.xml` — Child Maven configuration.
- `course-registration-app/src/main/java/com/university/RegistrationService.java` — Registration logic and credit limit.
- `course-registration-app/src/test/java/com/university/RegistrationServiceTest.java` — Credit-limit and capacity tests.
- `Jenkinsfile` — Jenkins Declarative Pipeline.
- `kubernetes/deployment.yaml` — Kubernetes Deployment.
- `kubernetes/service.yaml` — Kubernetes Service.
- `README.md` — Project documentation and submission evidence.

## Known Limitations

- The Jenkins pipeline has been written but has not been executed locally because Jenkins is not installed in the development environment.
- Maven tests were verified separately in the local environment.
- Kubernetes was tested locally with Minikube, not on a production cluster.
- The Minikube service URL may require the terminal running `minikube service nginx-service --url` to remain open.
