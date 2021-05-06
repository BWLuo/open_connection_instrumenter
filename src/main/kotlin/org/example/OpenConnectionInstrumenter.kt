package org.example

import soot.*
import soot.options.Options
import java.util.*

class OpenConnectionInstrumenter {
    /**
     * Setup the soot with the necessary configurations to instrument the program
     */
    fun setupSoot(platform: String, apiVersion: Int, apkPath: String, outPath: String) {
        G.reset()

        // Soot options to properly load all needed library classes
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_prepend_classpath(true);

        // Read the necessary library files for android
        Options.v().set_android_jars(platform); // The path to Android Platforms
        Options.v().set_android_api_version(apiVersion)

        // Load the apk for instrumentation
        Options.v().set_src_prec(Options.src_prec_apk); // Determine the input is an APK
        Options.v().set_process_dir(Collections.singletonList(apkPath)); // Provide paths to the APK
        Options.v().set_process_multiple_dex(true);  // Inform Dexpler that the APK may have more than one .dex files
        Options.v().set_include_all(true);

        // The output directory of the instrumented apk
        Options.v().set_output_format(Options.output_format_dex)
        Options.v().set_output_dir(outPath)
        Options.v().set_validate(true)

        // Load classes needed for giving print output on API call
        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES)
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES)
        Scene.v().loadNecessaryClasses()
    }

    /**
     * Starts the instrumentation process and outputs the apk once done
     */
    fun instrument() {
        println("Instrumenting Calls to openConnection")
        PackManager.v().getPack("jtp").add(Transform("jtp.myLogger", OpenConnectionPrintTransformer()))
        PackManager.v().runBodyPacks()
        PackManager.v().writeOutput()
    }
}