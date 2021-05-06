package org.example

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("android_platform=${args[0]}, api_version=${args[1]} apk_file=${args[2]} out_dir=${args[3]}")
            val finder = OpenConnectionInstrumenter()

            finder.setupSoot(args[0], args[1].toInt(), args[2], args[3])
            finder.instrument()
        }
    }
}
