package co.atrasvida.avidawebapi.compiler

/**
 * Custom Kotlin Class Builder which returns file content string
 * This is for learning purpose only.
 * Use KotlinPoet for production app
 * KotlinPoet can be found at https://github.com/square/kotlinpoet
 */
class KotlinClassBuilder(className: String,
                         packageName:String,
                         imports:String,
                         greeting:String = "Merry Christmas!!"
){

    private val contentTemplate = """
        package $packageName
      
        $imports
        
        class $className : Consumer<Throwable> {
           
             $greeting
        }

    """.trimIndent()

    fun getContent() : String{

        return contentTemplate

    }

    //fun greeting() = "$greeting"
}