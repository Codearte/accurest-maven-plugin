package io.codearte.accurest.maven

import org.apache.maven.model.Plugin
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.archiver.jar.Manifest

class ManifestCreator {
    static Manifest createManifest(MavenProject project) {
        Manifest manifest = new Manifest();
        Plugin accurestMavenPlugin = project.getBuildPlugins().find { it.artifactId == 'accurest-maven-plugin' }
        manifest.addConfiguredAttribute(new Manifest.Attribute("Accurest-Maven-Plugin-Version", accurestMavenPlugin.version));
        if (accurestMavenPlugin.getDependencies()) {
            String accurestVersion = accurestMavenPlugin.getDependencies().find {
                it.artifactId == 'accurest-core'
            }.version
            manifest.addConfiguredAttribute(new Manifest.Attribute("Accurest-Version", accurestVersion));
        }
        return manifest
    }
}
