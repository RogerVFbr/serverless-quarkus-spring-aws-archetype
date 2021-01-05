import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption;

dir = new File(new File(request.outputDirectory), request.artifactId)
systemNpmExtension = System.properties['os.name'].toLowerCase().contains('windows') ? '.cmd' : ''

def run(String cmd) {
    def process = cmd.execute(null, dir)
    process.waitForProcessOutput((Appendable)System.out, System.err)
    if (process.exitValue() != 0) {
        throw new Exception("Command '$cmd' exited with code: ${process.exitValue()}")
    }
}

Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
String packagePath = request.properties.get("package").replace(".", "/")

if (!"${secure}".toBoolean()) {
    Files.deleteIfExists projectPath.resolve("").resolve("serverless-cognito.yml")
    Files.deleteIfExists projectPath.resolve("src/test/java/" + packagePath + "/integration").resolve("IntegrationTestsConfigResolver-cognito.java")
    Files.deleteIfExists projectPath.resolve("src/test/java/" + packagePath + "/integration").resolve("PingIntegrationTests-cognito.java")
    Files.deleteIfExists projectPath.resolve("serverless").resolve("serverless-resources-cognito.yml")
    Files.deleteIfExists projectPath.resolve("serverless")
} else {
    Files.move(
            projectPath.resolve("").resolve("serverless-cognito.yml"),
            projectPath.resolve("").resolve("serverless.yml"),
            StandardCopyOption.REPLACE_EXISTING)
    Files.move(
            projectPath.resolve("src/test/java/" + packagePath + "/integration").resolve("IntegrationTestsConfigResolver-cognito.java"),
            projectPath.resolve("src/test/java/" + packagePath + "/integration").resolve("IntegrationTestsConfigResolver.java"),
            StandardCopyOption.REPLACE_EXISTING)
    Files.move(
            projectPath.resolve("src/test/java/" + packagePath + "/integration").resolve("PingIntegrationTests-cognito.java"),
            projectPath.resolve("src/test/java/" + packagePath + "/integration").resolve("PingIntegrationTests.java"),
            StandardCopyOption.REPLACE_EXISTING)
}

run("npm${systemNpmExtension} i")
run("npm${systemNpmExtension} run set-service-credentials -- --key ${awsAccessKeyId} --secret ${awsSecretAccessKey}")

if ("${deploy}".toBoolean()) {
    run("npm${systemNpmExtension} run deploy")
    run("npm${systemNpmExtension} run test-integration-cloud")
}

run("git init")
run("git add .")
run('git commit -m Initial_commit')