package grails.plugin.svn

import org.tmatesoft.svn.core.SVNAuthenticationException
import org.tmatesoft.svn.core.wc.SVNWCUtil

/**
 * <p>This is the reference SCM provider that works with Subversion working
 * copies and repositories. It implements the higher-level SCM interface
 * required by the Release plugin (and potentially others). Each instance
 * is designed to work with a single Grails project and a single remote
 * Subversion repository for that project. The remote repository does not
 * have to exist when the provider is first instantiated, but then the
 * {@link importIntoRepo(String, String)} method must be called to ensure it
 * exists.</p>
 * <p>If <code>importIntoRepo(String, String)</code> is not called, then
 * the base directory must be an existing Subversion working copy from
 * which the remote repository URL can be retrieved.</p>
 */
class SvnScmProvider {
    /** A hard-coded set of ignores. */
    static final String SVN_IGNORES = """\
            *.iws
            .settings
            grails-*.zip
            plugin.xml
            stacktrace.log
            target
            tmp
            """.stripIndent()

    File baseDir
    def credentials
    def svnClient
    def interactive

    /**
     * Creates a new provider instance.
     * @param baseDir The root directory of the Grails project that will be
     * managed by this provider instance.
     * @param interactive An object that is used to interact with the user.
     * Should have an 'out' property that resembles a print output stream and
     * an 'askUser' method that displays a message to the user, allows him or
     * her to enter some text, and then returns that entered text.
     */
    SvnScmProvider(baseDir, interactive) {
        this.baseDir = baseDir as File
        this.interactive = interactive
    }

    /**
     * Set the authentication credentials, i.e. username and password, for
     * the remote Subversion repository that this provider will be working
     * against.
     */
    void auth(String username, String password) {
        this.credentials = [username, password]
        if (svnClient) svnClient.setCredentials(*credentials)
    }

    /**
     * Imports the current Grails project source into a Subversion repository.
     * This will automatically create the standard 'trunk', 'tags', 'branches'
     * structure in the repository and set up a set of standard ignores for
     * Grails projects.
     * @param hostUrl The remote repository URL to import the source into. This
     * URL should be specific to the project, so it would normally include the
     * project name as the last path element. For example, for the GWT plugin
     * this might be http://svn.codehaus.org/grails-plugins/grails-gwt.
     * @param msg Optional message to use for the initial commit of the source
     * code to the repository. If this isn't provided, some default text is
     * used.
     */
    void importIntoRepo(String hostUrl, String msg = "") {
        svnClient = new SvnClient(hostUrl)
        if (credentials) svnClient.setCredentials(*credentials)

        // Start by creating the remote repo URL for this project.
        def structMsg = "Creating standard Subversion repository structure."
        handleAuthentication { svnClient.createPath "trunk", structMsg }
        handleAuthentication { svnClient.createPath "branches", structMsg }
        handleAuthentication { svnClient.createPath "tags", structMsg }

        // Check out the trunk to the project directory, add the project files,
        // and then commit everything. It's not technically an import according
        // to Subversion, but it works better because the current directory
        // immediately becomes a working copy.
        def webInfDir = new File(baseDir, "web-app/WEB-INF")
        handleAuthentication { svnClient.checkOut baseDir, "trunk" }
        handleAuthentication { svnClient.ignore baseDir, SVN_IGNORES }
        handleAuthentication { svnClient.addFilesToSvn([ webInfDir.parentFile, webInfDir ]) }
        handleAuthentication { svnClient.ignore webInfDir, "classes" }
        handleAuthentication { svnClient.addDirToSvn baseDir }
        handleAuthentication { svnClient.commit baseDir, msg ?: "Initial commit of the project source." }
    }

    /**
     * Determines whether the given file or directory is managed by the source
     * control system. If no argument is given, then the project directory is
     * implied, i.e. the method answers the question "is this project under
     * source control?"
     * @param fileOrDir Optional file or directory path to check. If not provided,
     * "." is implied.
     */
    boolean isManaged(File fileOrDir = null) {
        if (!fileOrDir) fileOrDir = baseDir

        if (fileOrDir.directory) {
            return SVNWCUtil.isVersionedDirectory(fileOrDir)
        } 
        else {
            if (!SVNWCUtil.isVersionedDirectory(fileOrDir.parentFile)) return false
            
            checkSvnClient()
            return svnClient.isVersioned(fileOrDir)
        }
    }
   
    /**
     * Returns a list of all files in the project that aren't currently under
     * source control and aren't in an ignore list.
     */
    List getUnmanagedFiles() {
        checkSvnClient()

        return svnClient.getUnversionedFiles(baseDir)
    }

    /**
     * Puts the given directory or file under source control. If the argument
     * is not given, the project directory is implied. Note that for directories,
     * this method acts recursively, i.e. the directory and all the files and
     * sub-directories in it are added.
     */
    void manage(File fileOrDir = null) {
        checkSvnClient()

        if (!fileOrDir) fileOrDir = baseDir
        if (fileOrDir.directory) {
            svnClient.addDirToSvn fileOrDir
        }
        else {
            svnClient.addFilesToSvn([ fileOrDir ])
        }
    }

    /**
     * Determines whether the project's working copy is up to date with respect
     * to the remote repository.
     */
    boolean upToDate() {
        checkSvnClient()

        return handleAuthentication {
            // Check the status of the base directory.
            return svnClient.upToDate(baseDir)
        }
    }

    /**
     * Commits the current set of changes to the remote repository, using the
     * given text as the commit message.
     */
    void commit(String msg) {
        checkSvnClient()

        handleAuthentication {
            svnClient.commit(baseDir, msg)
        }
    }

    /**
     * Tags the current 'trunk' in the remote repository with the given label.
     * As per Subversion convention, the tag is created under the 'tags'
     * directory in the repository.
     */
    void tag(String label, String msg) {
        checkSvnClient()

        handleAuthentication {
            svnClient.tag("trunk", "tags", label.replaceAll(/\./, '_'), msg)
        }
    }

    /**
     * Synchronizes the working copy with the remote repository. At the moment,
     * this simply performs an Subversion update.
     */
    void synchronize() {
        checkSvnClient()

        handleAuthentication {
            svnClient.update baseDir
        }
    }

    /**
     * Ensures that an {@link SvnClient} instance exists for this provider
     * object. If it doesn't, this method derives the remote repository URL
     * from the working copy information and uses that to create a new
     * <code>SvnClient</code> instance. In the case where the project
     * directory isn't yet a working copy, the method throws an
     * <code>IllegalArgumentException</code>.
     */
    private checkSvnClient() {
        if (!svnClient) {
            // If baseDir is a working copy, pull the repository URL from
            // the WC metadata. Otherwise, throw an because we can't operate
            // without a valid svnClient instance.
            try {
                svnClient = new SvnClient(baseDir)
            }
            catch (Exception ex) {
                throw new IllegalStateException(
                        "Can't perform Subversion operations on a directory that isn't a Subversion working copy.", ex)
            }
        }
    }

    /**
     * Executes a closure that may throw an SVNAuthenticationException.
     * If that exception is thrown, this method asks the user for his
     * username and password, updates the Subversion credentials and
     * tries to execute the closure again. Any exception thrown at that
     * point will propagate out.
     * @param c The closure to execute within the try/catch.
     */
    private handleAuthentication(c, authCount = 0) {
        try {
            return c()
        }
        catch (SVNAuthenticationException ex) {
            // Only allow three authentication attempts.
            if (authCount == 3) throw ex
            else if (authCount > 0) interactive.out.println "Authentication failed - please try again."

            def username = interactive.askUser("Enter your Subversion username: ")
            def password = interactive.askUser("Enter your Subversion password: ")
            svnClient.setCredentials(username, password)
            return handleAuthentication(c, ++authCount)
        }
    }
}
