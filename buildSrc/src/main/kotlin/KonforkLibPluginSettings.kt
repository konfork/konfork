import org.gradle.api.provider.Property

interface KonforkLibPluginSettings {
    val buildJs: Property<Boolean>
    val buildJvm: Property<Boolean>
}
