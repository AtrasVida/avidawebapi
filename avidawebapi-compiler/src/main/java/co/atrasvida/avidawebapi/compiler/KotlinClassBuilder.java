package co.atrasvida.avidawebapi.compiler;

/**
 * Custom Kotlin Class Builder which returns file content string
 * This is for learning purpose only.
 * Use KotlinPoet for production app
 * KotlinPoet can be found at https://github.com/square/kotlinpoet
 */
class KotlinClassBuilder {
    String className;
    String packageName;
    String imports;
    String greeting;

    public KotlinClassBuilder(String className, String packageName, String imports, String greeting) {
        this.className = className;
        this.packageName = packageName;
        this.imports = imports;
        this.greeting = greeting;
    }

    String getContent() {
        String contentTemplate =
                "package " + packageName +"\n" +
                        imports + "\n" +
                        "class " + className + ": Consumer<Throwable> {" + "\n" +
                        "    " + greeting +"\n" +
                        "}";
        return contentTemplate;
    }

}