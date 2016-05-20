package com.brettonw.resolver;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositoryCache;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolver container
 */
public class Resolver {
    /**
     * Resolve a Maven artifact into an array of URLs for the artifact and all of its dependencies
     * @param groupId
     * @param artifactId
     * @param version
     * @param repositoryUrl
     * @param repositoryPath
     * @return
     * @throws DependencyResolutionException
     */
    public static URL[] get (String groupId, String artifactId, String version, String repositoryUrl, File repositoryPath) throws DependencyResolutionException {
        // configure defaults if they got left out
        if (version == null) {
            version = "RELEASE";
        }
        if (repositoryUrl == null) {
            repositoryUrl = "http://central.maven.org/maven2/";
        }
        if (repositoryPath == null) {
            repositoryPath = new File ("m2");
        }

        // create a Maven Repository service locator with File and HTTP transporters, and a
        // repository system from that
        DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator ()
                .addService (RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class)
                .addService (TransporterFactory.class, FileTransporterFactory.class)
                .addService (TransporterFactory.class, HttpTransporterFactory.class);
        RepositorySystem system = serviceLocator.getService (RepositorySystem.class);

        // create a Maven Repository session, and configure our repo from that
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession ();
        LocalRepository localRepo = new LocalRepository (repositoryPath);
        session.setLocalRepositoryManager (system.newLocalRepositoryManager (session, localRepo));
        session.setCache(new DefaultRepositoryCache ());
        session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_ALWAYS);

        // construct the request
        CollectRequest collectRequest = new CollectRequest ();
        Artifact artifact = new DefaultArtifact (groupId, artifactId, "jar", version);

        collectRequest.setRoot (new Dependency (artifact, JavaScopes.RUNTIME));
        List<RemoteRepository> repositories = new ArrayList<> (1);
        repositories.add (new RemoteRepository.Builder ("default", "default", repositoryUrl).build ());
        collectRequest.setRepositories (repositories);
        DependencyRequest dependencyRequest = new DependencyRequest (collectRequest, DependencyFilterUtils.classpathFilter (JavaScopes.RUNTIME));

        // resolve the artifacts
        List<ArtifactResult> artifactResults = system.resolveDependencies (session, dependencyRequest).getArtifactResults ();
        URL result[] = new URL[artifactResults.size ()];
        int i = 0;
        for (ArtifactResult artifactResult : artifactResults) {
            File fileInRepository = artifactResult.getArtifact ().getFile ();
            try {
                result[i++] = fileInRepository.toURI ().toURL ();
            } catch (MalformedURLException malformedUrlException) {
                // XXX do nada
            }
        }

        return result;
    }
}
