import static org.apache.commons.io.FilenameUtils.concat
import static org.apache.commons.io.FileUtils.moveFile

// --------------------------------------------
// --------------- DEFAULTS -------------------
// --------------------------------------------

def props = [projectName: projectDir.name]

// --------------------------------------------
// --------------- QUESTIONS ------------------
// --------------------------------------------

props.awsProfileName    = ask('Profile name to use in your ~/.aws/credentials file? [vanderfox]: ', 'vanderfox', 'awsProfileName')
props.group     = ask('group name used for packages? [com.vanderfox.demo]: ', 'com.vanderfox.demo', 'group')
props.speechletClassName  = ask('Speechlet class name? [DemoSpeechlet]: ', 'DemoSpeechlet', 'speechletClassName')
props.lambdaFuction  = ask('Name of your Lambda function as listed on AWS Lamba Console ? [GroovySkill]: ', 'GroovySkill', 'lambdaFuction')
props.lambdaHandlerClass  = ask('Name of your Lambda function Handler Class as listed on AWS Lambda Console ? [com.vanderfox.demo.SpeechletRequestStreamHandler]: ', 'com.vanderfox.demo.SpeechletRequestStreamHandler', 'lambdaHandlerClass')
props.awsAccountId  = ask('application ID from the developer.amazon.com skill? [123456]: ', '123456', 'awsAccountId')
props.lambdaRole  = ask('Name of your Lambda role your function will use ? [arn:aws:iam::${awsAccountId}:role/lambda_basic_execution]: ', 'arn:aws:iam::${aws.accountId}:role/lambda_basic_execution', 'lambdaRole')
props.lambdaMemsize  = ask('Memory footprint to reserve for your Lambda function ? [512]: ', '512', 'lambdaMemsize')
props.lambaRuntime = ask('Lamba Runtime to use for function? [Runtime.Java8]: ', 'Runtime.Java8', 'lambaRuntime')
props.shadowJarBasename  = ask('Shadow Jar base name? [heroQuiz-fat]: ', 'heroQuiz-fat', 'shadowJarBasename')
props.applicationId  = ask('application ID from the developer.amazon.com skill? [amzn1.echo-sdk-ams.app.9955b0ce-501e-48e0-80b3-093ec393681b]: ', 'amzn1.echo-sdk-ams.app.9955b0ce-501e-48e0-80b3-093ec393681b', 'applicationId')



// --------------------------------------------
// ----------- PROCESSING TEMPLATES -----------
// --------------------------------------------

processTemplates 'README.md', props
processTemplates '**/build.gradle/**', props


// -------------------------------------------------
// --- PROCESSING GROOVY MAIN TEMPLATES -----
// -------------------------------------------------

def defaultBaseCodePath         = new File("${projectDir}${File.separator}", 'code')
def groovyCodePackagePath       = props.defaultPackage.replace('.' as char, '/' as char)

def groovyCodeTemplatesPath     = new File(defaultBaseCodePath, 'main')
def groovyCodeBasePath          = 'src/main/groovy'
def groovyCodeDestinationPath   = new File("${projectDir}${File.separator}", concat(groovyCodeBasePath, groovyCodePackagePath))

processCode(groovyCodeTemplatesPath, groovyCodeDestinationPath, props)

// -------------------------------------------------
// - PROCESSING MOBILE GROOVY TEST TEMPLATES -------
// -------------------------------------------------

//def groovyCodeTestTemplatesPath     = new File("${projectDir}${File.separator}mobile", 'test')
//def groovyCodeTestBasePath          = 'src/androidTest/groovy'
//def groovyCodeTestDestinationPath   = new File("${projectDir}${File.separator}mobile", concat(groovyCodeTestBasePath, groovyCodePackagePath))

//processCode(groovyCodeTestTemplatesPath, groovyCodeTestDestinationPath, props)

// delete 'code' directory
defaultBaseCodePath.deleteOnExit()


/**
 * Process source file templates at fromDir and moves them at
 * toDir. Then deletes source file templates.
 *
 * @param fromDir where the templates are located
 * @param toDir final destination
 **/
void processCode(File fromDir, File toDir, Map projectProperties) {
    fromDir.listFiles().each { File file ->
        // Processing each template
        processTemplates "**/${file.name}", projectProperties

        // Moving groovy file to the right place
        def sourceName = file.name.replace('gtpl','groovy')
        def destination = new File(toDir, sourceName)

        moveFile(file, destination)
    }

    fromDir.delete()
}
