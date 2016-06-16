package io.codearte.accurest.maven;

import io.codearte.accurest.maven.stubrunner.LocalStubRunner;
import io.codearte.accurest.maven.stubrunner.RemoteStubRunner;
import io.codearte.accurest.stubrunner.BatchStubRunner;
import io.codearte.accurest.stubrunner.StubRunner;
import io.codearte.accurest.stubrunner.StubRunnerOptions;
import io.codearte.accurest.stubrunner.StubRunnerOptionsBuilder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.aether.RepositorySystemSession;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static com.google.common.base.Strings.isNullOrEmpty;

@Mojo(name = "run", requiresProject = false, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RunMojo extends AbstractMojo {

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.build.directory}/accurest/mappings")
    private File stubsDirectory;

    @Parameter(property = "stubsDirectory", defaultValue = "${basedir}")
    private File destination;

    /**
     * HTTP port for WireMock server
     */
    @Parameter(property = "accurest.http.port", defaultValue = "8080")
    private int httpPort;

    /**
     * Set this to "true" to bypass accurest execution.
     */
    @Parameter(property = "accurest.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Set this to "true" to bypass accurest test generation.
     */
    @Parameter(property = "accurest.skipTestOnly", defaultValue = "false")
    private boolean skipTestOnly;

    @Parameter(property = "accurest.stubs")
    private String stubs;

    @Parameter(property = "accurest.http.minPort", defaultValue = "10000")
    private int minPort;

    @Parameter(property = "accurest.http.maxPort", defaultValue = "15000")
    private int maxPort;

    /**
     * Classifier used by stubs artifacts.
     */
    @Parameter(defaultValue = "stubs")
    private String stubsClassifier = "stubs";

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    private final LocalStubRunner localStubRunner;

    private final RemoteStubRunner remoteStubRunner;

    @Inject
    public RunMojo(LocalStubRunner localStubRunner, RemoteStubRunner remoteStubRunner) {
        this.localStubRunner = localStubRunner;
        this.remoteStubRunner = remoteStubRunner;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip || skipTestOnly) {
            getLog().info("Skipping accurest execution: accurest.skip=" + String.valueOf(skip));
            return;
        }
        BatchStubRunner batchStubRunner = null;
        StubRunnerOptionsBuilder optionsBuilder = new StubRunnerOptionsBuilder()
                .withStubsClassifier(stubsClassifier);
        if (isNullOrEmpty(stubs)) {
            StubRunnerOptions options = optionsBuilder
                    .withPort(httpPort)
                    .build();
            StubRunner stubRunner = localStubRunner.run(resolveStubsDirectory().getAbsolutePath(), options);
            batchStubRunner = new BatchStubRunner(Collections.singleton(stubRunner));
        } else {
            StubRunnerOptions options = optionsBuilder
                    .withStubs(stubs)
                    .withMinMaxPort(minPort, maxPort)
                    .build();
            batchStubRunner = remoteStubRunner.run(options, repoSession);
        }
        pressAnyKeyToContinue();
        if (batchStubRunner != null) {
            try {
                batchStubRunner.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Fail to close batch stub runner", e);
            }
        }
    }

    private File resolveStubsDirectory() {
        if (isInsideProject()) {
            return stubsDirectory;
        } else {
            return destination;
        }
    }

    private void pressAnyKeyToContinue() {
        getLog().info("Press ENTER to continue...");
        try {
            System.in.read();
        } catch (Exception ignored) {
        }
    }

    private boolean isInsideProject() {
        return mavenSession.getRequest().isProjectPresent();
    }

}
