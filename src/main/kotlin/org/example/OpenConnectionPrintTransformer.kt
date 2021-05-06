package org.example

import soot.*
import soot.Unit
import soot.javaToJimple.LocalGenerator
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.Stmt
import soot.jimple.StringConstant
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList

/**
 * Instrument all the calls to openConnection
 * Will cause an identifier to be printed when openConnection is called in the apk
 */
class OpenConnectionPrintTransformer: BodyTransformer() {
    // These body transformers are run in a multi-threaded fashion by soot
    // Each id will correspond to a unique openConnection call-site within the application
    private val counter = AtomicLong()

    /**
     * Method called by soot on all bodies loaded
     */
    override fun internalTransform(b: Body?, phaseName: String?, options: MutableMap<String, String>?) {
        val body = b as JimpleBody

        // stores all the open connection units found
        // we cannot change the body while iterating through it in our search
        val openConnectionUnits = ArrayList<Unit>()

        for (u in body.units) {
            val stmt = u as Stmt
            val urlSc = getURLSootClasses()

            // check for openConnection call
            if (stmt.containsInvokeExpr() && stmt.invokeExpr.method.name == "openConnection") {
                val baseSc = stmt.invokeExpr.method.declaringClass
                if (urlSc.any { Scene.v().activeHierarchy.isClassSubclassOfIncluding(baseSc, it) }) {
                    openConnectionUnits.add(stmt)
                }
            }
            b.validate()
        }

        for (u in openConnectionUnits) {
            val id = counter.addAndGet(1)
            val genUnits = getPrintUnits(body, "OPENCONNECTION HAS BEEN CALLED: id=[$id]")
            body.units.insertBefore(genUnits, u)
        }
    }

    companion object {
        /**
         * The classes that we check that each openConnection reference type is derived from
         * Needs to be loaded dynamically because it relies on Soot being setup
         */
        fun getURLSootClasses(): List<SootClass> {
            return listOf(
                Scene.v().getSootClass("java.net.URL"),
                Scene.v().getSootClass("java.net.HttpURLConnection"),
                Scene.v().getSootClass("java.net.URLConnection")
            )
        }

        /**
         * Gets the list of units for a System.out.print method call that prints the [msg]
         * This list of units can be inserted into the [body]
         */
        private fun getPrintUnits(body: Body, msg: String): List<Unit> {
            val generatedUnits = ArrayList<Unit>()

            // In order to call "System.out.println" we need to create a local containing "System.out" value
            val psLocal = LocalGenerator(body).generateLocal(RefType.v("java.io.PrintStream"))

            // Now we assign "System.out" to psLocal
            val sysOutField  = Scene.v().getField("<java.lang.System: java.io.PrintStream out>")
            val sysOutAssignStmt = Jimple.v().newAssignStmt(psLocal, Jimple.v().newStaticFieldRef(sysOutField.makeRef()))
            generatedUnits.add(sysOutAssignStmt)

            // Create println method call and provide its parameter
            val printlnMethod = Scene.v().grabMethod("<java.io.PrintStream: void println(java.lang.String)>")
            val printlnParameter: Value = StringConstant.v(msg)
            val printlnMethodCallStmt = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(psLocal, printlnMethod.makeRef(), printlnParameter))
            generatedUnits.add(printlnMethodCallStmt)

            return generatedUnits
        }
    }
}
