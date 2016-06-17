package io.codearte.accurest.maven

import org.apache.maven.model.Plugin
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.archiver.jar.Manifest

class ManifestCreator {
    static Manifest createManifest(MavenProject project) {
        Manifest manifest = new Manifest();
        Plugin verifierMavenPlugin = project.getBuildPlugins().find { it.artifactId == 'spring-cloud-contract-verifier-maven-plugin' }
        manifest.addConfiguredAttribute(new Manifest.Attribute("Spring-Cloud-Contract-Verifier-Maven-Plugin-Version", verifierMavenPlugin.version));
        if (verifierMavenPlugin.getDependencies()) {
            String verifierVersion = verifierMavenPlugin.getDependencies().find {
                it.artifactId == 'spring-cloud-contract-verifier-core'
            }.version
            manifest.addConfiguredAttribute(new Manifest.Attribute("Spring-Cloud-Contract-Verifier-Version", verifierVersion));
        }
        return manifest
    }
}
