import org.gradle.api.artifacts.VersionCatalog

fun VersionCatalog.getLibrary(library: String) = findLibrary(library).get()

fun VersionCatalog.getBundle(bundle: String) = findBundle(bundle).get()

/*

internal val VersionCatalog.logcat: Provider<MinimalExternalModuleDependency>
    get() = getLibrary("logcat")

internal val VersionCatalog.ktlint: Provider<MinimalExternalModuleDependency>
    get() = getLibrary("ktlint")

internal val VersionCatalog.playcore: Provider<MinimalExternalModuleDependency>
    get() = getLibrary("androidx.playcore")

internal val VersionCatalog.composeBundle: Provider<ExternalModuleDependencyBundle>
    get() = getBundle("compose")

internal val VersionCatalog.composeTestBundle: Provider<ExternalModuleDependencyBundle>
    get() = getBundle("composetest")

private fun VersionCatalog.getLibrary(library: String) = findLibrary(library).get()

private fun VersionCatalog.getBundle(bundle: String) = findBundle(bundle).get()

*/
