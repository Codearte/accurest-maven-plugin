package io.codearte.accurest.maven;

import static java.lang.String.format;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.codearte.accurest.AccurestException;
import io.codearte.accurest.TestGenerator;
import io.codearte.accurest.config.AccurestConfigProperties;

@Mojo(name = "generateSpecs", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class GenerateSpecsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${basedir}")
	protected File baseDir;

	@Parameter(defaultValue = "${project.build.directory}")
	protected File projectBuildDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {
		AccurestConfigProperties config = new AccurestConfigProperties();

		config.setContractsDslDir(new File(baseDir, "/src/test/resources/stubs"));
		config.setBasePackageForTests("io.codearte.accurest.tests");
		config.setGeneratedTestSourcesDir(new File(projectBuildDirectory, "/generated-sources/accurest"));

		getLog().info("Accurest Plugin: Invoking test sources generation");
		getLog().info(format("Registering %s as test source directory", config.getGeneratedTestSourcesDir()));

		try {
			TestGenerator generator = new TestGenerator(config);
			int generatedClasses = generator.generate();
			getLog().info(String.format("Generated %s test classes.", generatedClasses));
		} catch (AccurestException e) {
			throw new MojoExecutionException(format("Accurest Plugin exception: %s", e.getMessage()), e);
		}

	}

}