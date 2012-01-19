package grails.plugin.svn

import org.tmatesoft.svn.core.SVNAuthenticationException
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNNodeKind
import org.tmatesoft.svn.core.SVNProperties
import org.tmatesoft.svn.core.SVNPropertyValue
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.ISVNStatusHandler
import org.tmatesoft.svn.core.wc.SVNCommitClient
import org.tmatesoft.svn.core.wc.SVNCopyClient
import org.tmatesoft.svn.core.wc.SVNCopySource
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.wc.SVNStatus
import org.tmatesoft.svn.core.wc.SVNStatusClient
import org.tmatesoft.svn.core.wc.SVNStatusType
import org.tmatesoft.svn.core.wc.SVNUpdateClient
import org.tmatesoft.svn.core.wc.SVNWCClient
import org.tmatesoft.svn.core.wc.SVNWCUtil

/**
 * Abstraction layer for doing Subversion work using the SVNKit library.
 * The SVNKit classes make unit testing difficult, so it makes sense to
 * consolidate and isolate their usage in this one class. It also simplifies
 * working with Subversion.
 */
class SvnClient {
    private repoUrl
    private authManager

    static {
        DAVRepositoryFactory.setup()
        FSRepositoryFactory.setup()
        SVNRepositoryFactoryImpl.setup()
    }

    /**
     * Creates a new client instance that works against the Subversion repository
     * at the given URL. If the URL ends with '/trunk', that bit of the path is
     * stripped out and the rest used as the base repository URL.
     */
    SvnClient(URI repoUrl) {
        this(repoUrl.toString())
    }

    /**
     * Creates a new client instance that works against the Subversion repository
     * at the given URL. If the URL ends with '/trunk', that bit of the path is
     * stripped out and the rest used as the base repository URL.
     */
    SvnClient(String repoUrl) {
        repoUrl = stripTrunkPath(repoUrl)

        // Extract username and password from the URL.
        def (url, username, password) = tokenizeUrl(repoUrl)

        this.repoUrl = SVNURL.parseURIDecoded(url)

        if (username) {
            this.authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password)
        }
        else {
            this.authManager = SVNWCUtil.createDefaultAuthenticationManager()
        }
    }

    /**
     * Creates a new client instance that works against the Subversion repository
     * from which the given working copy was created.
     */
    SvnClient(File wc) {
        this.authManager = SVNWCUtil.createDefaultAuthenticationManager()

        def wcClient = new SVNStatusClient(authManager, null)
        def url = wcClient.doStatus(wc.canonicalFile, false).URL
        this.repoUrl = SVNURL.create(url.protocol, url.userInfo, url.host, url.port, stripTrunkPath(url.path), false)
    }

    def getRepoUrl() { return this.repoUrl }
    def getAuthManager() { return this.authManager }

    def setCredentials(username, password) {
        this.authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password)
    }

    /**
     * Determines whether the given directory is a working copy for the
     * given path in this client's repository.
     */
    boolean isWorkingCopyForRepository(wc, path) {
        try {
            def wcClient = new SVNWCClient(authManager, null)
            def info = wcClient.doInfo(wc.canonicalFile, SVNRevision.HEAD)
            return repoUrl.appendPath(path, false) == info.URL
        }
        catch (SVNAuthenticationException ex) {
            // This one must be propagated!
            throw ex
        }
        catch (SVNException ex) {
            // Assuming that this means the given directory is not a
            // working copy.
            return false
        }
    }

    /**
     * Sets the ignore list for the given working copy path. The ignore
     * patterns are provided as a '\n'-delimited string.
     * @param wcPath The full path to the directory or file to add the
     * ignores to. This should not be relative to the base working copy
     * directory but preferably and absolute path.
     * @param patterns The '\n'-delimited list of ignore patterns.
     */
    void ignore(wcPath, String patterns) {
        def wcClient = new SVNWCClient(authManager, null)
        wcClient.doSetProperty(
            wcPath as File,
            "svn:ignore",
            SVNPropertyValue.create(patterns),
            false,
            SVNDepth.EMPTY,
            null,
            []) 
    }

    /**
     * Returns a list of all unversioned files and directories in the given
     * working copy directory.
     * @param wc The base directory of the Subversion working copy of interest.
     */
    def getUnversionedFiles(wc) {
        def statusClient = new SVNStatusClient(authManager, null)
        def files = []
        def handler = { SVNStatus status ->
            if (status.contentsStatus == SVNStatusType.STATUS_UNVERSIONED) {
                files << status.file
            }
        } as ISVNStatusHandler

        statusClient.doStatus(wc, SVNRevision.HEAD, SVNDepth.INFINITY, false, true, false, false, handler, [])

        return files
    }

    /**
     * Returns <code>true</code> if the given file or directory is under
     * source control.
     */
    def isVersioned(file) {
        def statusClient = new SVNStatusClient(authManager, null)
        return statusClient.doStatus(file, false).contentsStatus != SVNStatusType.STATUS_UNVERSIONED
    }

    /**
     * Checks out a path within the configured Subversion repository to
     * a local working copy.
     * @param wc (File) The location of the working copy.
     * @param relUrl The path to check out, relative to the base repository
     * URL.
     */
    def checkOut(wc, relUrl) {
        def updateClient = new SVNUpdateClient(authManager, null)
        updateClient.doCheckout(repoUrl.appendPath(relUrl, false), wc, SVNRevision.HEAD, SVNRevision.HEAD, true)
    }

    /**
     * Determines whether the given working copy is up to date with respect to
     * the remote Subversion repository.
     */
    def upToDate(wc) {
        def statusClient = new SVNStatusClient(authManager, null)
        def status = statusClient.doStatus(wc, false)
        def repos = SVNRepositoryFactory.create(repoUrl)
        repos.authenticationManager = authManager

        return status.revision.number == repos.latestRevision
    }

    /**
     * Updates the given working copy to HEAD from the repository.
     */
    def update(wc) {
        def updateClient = new SVNUpdateClient(authManager, null)
        updateClient.doUpdate(wc.canonicalFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false)
    }

    /**
     * Commits the changes in a working copy to its corresponding
     * Subversion repository.
     * @param wc (File) The working copy directory.
     * @param msg The commit message to use.
     * @return SVNCommitInfo if the commit was successful
     */
    def commit(wc, msg) {
        def commitClient = new SVNCommitClient(authManager, null)

        // The following line can't be inlined because that breaks GMock
        // 0.8.0 in the unit tests.
        def dirs = [ wc.canonicalFile ] as File[]
        return commitClient.doCommit(dirs, false, msg, true, true)
    }

    /**
     * Imports a local file path (either a file or a directory) into the
     * repository under the given path.
     * @param file (File) The file or directory to import.
     * @param targetPath The path in the repository to import into.
     * @param msg The commit message to use for the import.
     * @return SVNCommitInfo if the commit was successful
     */
    def importIntoRepo(file, targetPath, msg) {
        def importClient = new SVNCommitClient(authManager, null)
        return importClient.doImport(file, targetPath, msg, true)
    }

    /**
     * Makes sure that a given set of files are added to the Subversion
     * repository on the next commit if they aren't already there. This
     * method assumes that the files reside in a Subversion working copy.
     * Also, this method does not hit the network/internet, since it just
     * uses the information in the working copy.
     * @param files A collection of files that should be added to the
     * repository on the next commit.
     */
    def addFilesToSvn(files) {
        def statusClient = new SVNStatusClient(authManager, null)
        def wcClient = new SVNWCClient(authManager, null)

        for (file in files) {
            // Check whether this file is already in the repository.
            def addFile = false
            try {
                def status = statusClient.doStatus(file, true)
                if (status.kind == SVNNodeKind.NONE || status.kind == SVNNodeKind.UNKNOWN) addFile = true
            }
            catch (SVNException) {
                addFile = true
            }

            // It's not in the repository, so we have to add it (so that
            // we can commit it later).
            if (addFile) wcClient.doAdd(file, true, false, false, false)
        }
    }

    /**
     * Makes sure that a given directory and all the sub-directories and
     * files it contains are added to the Subversion repository on the
     * next commit if they aren't already there. This method assumes that
     * the directory resides in a Subversion working copy.
     * Also, this method does not hit the network/internet, since it just
     * uses the information in the working copy.
     * @param dir Adds everything in the given directory to Subversion
     * (except for those files/dirs in the ignore lists).
     */
    def addDirToSvn(dir) {
        def statusClient = new SVNStatusClient(authManager, null)
        def wcClient = new SVNWCClient(authManager, null)
        wcClient.doAdd(dir, true, false, false, SVNDepth.INFINITY, false, false)
    }

    /**
     * Creates a tag in a Subversion repository from a given source URL.
     * @param srcPath The path (relative to the configured Subversion
     * repository URL) to tag.
     * @param tagsPath The relative path to where the tags are created.
     * @param tag The name of the tag to create.
     * @param msg The commit message to use when creating the tag.
     */
    def tag(srcPath, tagsPath, tag, msg) {
        def copyClient = new SVNCopyClient(authManager, null)
        def commitClient = new SVNCommitClient(authManager, null)

        // Ensure that the 'tags' folder exists in the repository.
        def tagsUrl = repoUrl.appendPath(tagsPath, false)
        try {
            // The following line can't be inlined because that breaks GMock
            // 0.8.0 in the unit tests.
            def arg1 = [ tagsUrl ] as SVNURL[]
            commitClient.doMkDir(arg1, msg)
        }
        catch (SVNException e) {
            // ok - already exists
        }

        // Delete the tag if it exists.
        def newTagUrl = tagsUrl.appendPath(tag, false)
        try {
            // The following line can't be inlined because that breaks GMock
            // 0.8.0 in the unit tests.
            def arg1 = [ newTagUrl ] as SVNURL[]
            commitClient.doDelete(arg1, msg) }
        catch (SVNException e) {
            // ok - the tag doesn't exist yet
        }

        // Create the new tag from the source URL.
        def copySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, repoUrl.appendPath(srcPath, false))
        def arg1 = [ copySource ] as SVNCopySource[]
        copyClient.doCopy(arg1, newTagUrl, false, false, true, msg, new SVNProperties())
    }
    
    /**
     * Determines whether the given path exists in the configured repository.
     * Returns <code>true</code> if the path does exist.
     */
    boolean pathExists(path) {
        def repos = SVNRepositoryFactory.create(repoUrl)
        repos.authenticationManager = authManager

        return repos.info(path, -1) != null
    }

    /**
     * Creates a path in the repository if it doesn't exist. Equivalent
     * to a Subversion 'mkdir'. This method creates all the required
     * parent directories automatically.
     * @param path The relative path to create.
     * @param msg The commit message to use.
     */
    def createPath(path, msg) {
        def commitClient = new SVNCommitClient(authManager, null)
        def firstArg = [ repoUrl.appendPath(path, false) ] as SVNURL[]
        commitClient.doMkDir(firstArg, msg, new SVNProperties(), true)
    }

    /**
     * Retrieves the most recent revision of a file from the repository.
     * @param path The path of the file to fetch relative to the root of
     * the repository.
     * @param localFile (File) Where to put the file locally.
     */
    def fetchFile(path, localFile) {
        def repo = SVNRepositoryFactory.create(repoUrl)
        repo.authenticationManager = authManager

        // Make sure we're trying to fetch a file that exists.
        def nodeKind = repo.checkPath(path, -1)
        if (nodeKind == SVNNodeKind.NONE) {
            throw new Exception("The remote file does not exist: " + repoUrl.appendPath(path, false))
        }
        else if (nodeKind != SVNNodeKind.FILE) {
            throw new Exception("The remote path is not a file: " + repoUrl.appendPath(path, false))
        }

        // Copy the remote file's data to our local file. 
        localFile.withOutputStream { os ->
            repo.getFile(path, -1L, new SVNProperties(), os)
        }
    }

    /**
     * Returns the revision of HEAD in the repository.
     */
    def getLatestRevision() {
        def repo = SVNRepositoryFactory.create(repoUrl)
        repo.authenticationManager = authManager
        return repo.latestRevision
    }

    /**
     * Extracts any username and password from a URL and returns the base URL,
     * username, and password as separate elements.
     * @param url The URL to be parsed.
     * @return A tuple of base URL, username, and password.
     */
    protected final tokenizeUrl(url) {
        // Parse the URL using SVNKit.
        def svnUrl = SVNURL.parseURIDecoded(url)

        // Start with the base URL.
        def result = []
        if (!svnUrl.hasPort()) {
            // URL doesn't explicitly specify a port, so we don't include
            // it in the base URL either.
            result << "${svnUrl.protocol}://${svnUrl.host}${svnUrl.path}".toString()
        }
        else {
            result << "${svnUrl.protocol}://${svnUrl.host}:${svnUrl.port}${svnUrl.path}".toString()
        }

        // Now extract the user information.
        def userInfo = svnUrl.userInfo
        if (userInfo) {
            def userInfoArray = userInfo.split(":")
            result << userInfoArray[0]
            result << (userInfoArray.size() > 1 ? userInfoArray[1] : "")
        }
        else {
            result << "" << ""
        }

        return result
    }

    /**
     * Strips any trailing 'trunk' sub-path and '/' from the given path.
     */
    private stripTrunkPath(String path) {
        // Strip off any 'trunk' end to the path.
        def m = path =~ '(.*)/trunk/?$' 
        if (m) path = m[0][1]

        // Strip off any trailing '/'.
        if (path[-1] == '/') path = path[0..-2]
        return path
    }
}
