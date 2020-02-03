/*
 * Copyright 2020 Alexander Skvortsov (skvalex)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skvalex.callrecorder.decrypt

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import javax.crypto.BadPaddingException

fun main(args: Array<String>) = App().main(args)

class App : CliktCommand(name = "craf_decrypt") {
    init {
        versionOption("1.0")
    }

    private val key: File by option("-k", "--key", help = "Path to the key file which were used to encrypt the .craf file (default: callrecorder.key)").file().default(File("callrecorder.key"))

    private val noMetadata by option("--nometadata", help = "Don't add Call Recorder's metadata to the resulting file").flag()

    private val password: String by argument("password", help = "Key password")

    private val file: File by argument("file", help = "Path to .craf file to decrypt").file(exists = true, readable = true)

    override fun run() {
        try {
            val crafDecrypt = CrafDecrypt()
            crafDecrypt.decrypt(key, password, file, noMetadata)
        } catch (e: BadPaddingException) {
            echo("wrong password")
        }
    }
}
