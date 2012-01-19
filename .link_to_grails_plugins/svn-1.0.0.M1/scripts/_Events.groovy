eventInitScm = { basedir, interactive ->
    // Create a new SCM provider if one isn't already registered. The reason we
    // check whether one already exists is because the Release plugin depends
    // on this one, so we may have multiple SCM plugins installed. This plugin
    // should not overrule any others that are installed, hence we check whether
    // the provider is already registered or not.
    if (!scmProvider) {
        scmProvider = classLoader.loadClass("grails.plugin.svn.SvnScmProvider").newInstance(basedir, interactive)
    }
}
