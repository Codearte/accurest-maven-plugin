package io.codearte.accurest.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;

@Mojo(name = "generateStubs", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class GenerateStubsMojo extends AbstractMojo {

    private static final String STUB_MAPPING_FILE_PATTERN = "**/*.json";
    private static final String ACCUREST_FILE_PATTERN = "**/*.groovy";

    @Parameter(defaultValue = "${project.build.directory}", readonly = true, required = true)
    private File projectBuildDirectory;

    @Parameter(property = "stubsDirectory", defaultValue = "${project.build.directory}/accurest")
    private File outputDirectory;

    /**
     * Set this to "true" to bypass accurest execution..
     */
    @Parameter(property = "accurest.skip", defaultValue = "false")
    private boolean skip;

    @Component
    private MavenProjectHelper projectHelper;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver archiver;

    @Parameter(defaultValue = "true")
    private boolean attachContracts;

    @Parameter(defaultValue = "stubs")
    private String classifier;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping accurest execution: accurest.skip=" + skip);
            return;
        }
        File stubsJarFile = createStubJar(outputDirectory);
        projectHelper.attachArtifact(project, "jar", classifier, stubsJarFile);
    }

    private File createStubJar(File stubsOutputDir) throws MojoFailureException, MojoExecutionException {
        if (!stubsOutputDir.exists()) {
            throw new MojoExecutionException("Stubs could not be found: " + stubsOutputDir.getAbsolutePath() + ".\nPlease make sure that accurest:convert was invoked");
        }
        String stubArchiveName = project.getBuild().getFinalName() + "-" + classifier + ".jar";
        File stubsJarFile = new File(projectBuildDirectory, stubArchiveName);
        try {
            if (attachContracts) {
                archiver.addDirectory(stubsOutputDir, new String[]{STUB_MAPPING_FILE_PATTERN, ACCUREST_FILE_PATTERN}, new String[0]);
            } else {
                getLog().info("Skipping attaching accurest contracts");
                archiver.addDirectory(stubsOutputDir, new String[]{STUB_MAPPING_FILE_PATTERN}, new String[]{ACCUREST_FILE_PATTERN});
            }
            archiver.setCompress(true);
            archiver.setDestFile(stubsJarFile);
            archiver.addConfiguredManifest(ManifestCreator.createManifest(project));
            archiver.createArchive();
        } catch (Exception e) {
            throw new MojoFailureException("Exception while packaging " + classifier + " jar.", e);
        }
        return stubsJarFile;
    }

}
