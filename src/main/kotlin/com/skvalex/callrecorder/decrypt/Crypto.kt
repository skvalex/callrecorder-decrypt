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

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Crypto(keyFile: File, password: String) {

    val decryptCipher: Cipher

    init {
        val key = readKey(keyFile, password)
        decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        decryptCipher.init(Cipher.DECRYPT_MODE, key.secretKey, key.ivParameterSpec)
    }

    private fun readKey(keyFile: File, password: String): Key {
        RandomAccessFile(keyFile, "r").use { file ->
            val strength = file.readInt()
            if (strength != 128 && strength != 192 && strength != 256) {
                throw IOException("Unknown key strength: $strength. Maybe it's not Call Recorder's key?")
            }
            val encryptedKey = ByteArray(keyFile.length().toInt() - Integer.BYTES)
            file.read(encryptedKey)
            return Key(strength, Key.decryptWithPassword(password, encryptedKey, strength))
        }
    }

    class Key(private val strength: Int, aesKey: ByteArray) {

        val secretKey: SecretKey
        val ivParameterSpec: IvParameterSpec

        init {
            secretKey = getSecretKey(aesKey)
            ivParameterSpec = IvParameterSpec(byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f))
        }


        private fun getSecretKey(aesKey: ByteArray): SecretKey {
            val spec: KeySpec = PBEKeySpec(toHex(aesKey), MD5_SALT.toByteArray(), 8, strength)
            val tmp = secretKeyFactory.generateSecret(spec)
            return SecretKeySpec(tmp.encoded, "AES")
        }

        companion object {
            private const val MD5_SALT = "callrecorder"
            private const val HEXES = "0123456789ABCDEF"

            private val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

            fun decryptWithPassword(password: String, encryptedData: ByteArray, strength: Int): ByteArray {
                val spec: KeySpec = PBEKeySpec(toHex(passToHash(password)), MD5_SALT.toByteArray(), 8, strength)
                val tmp = secretKeyFactory.generateSecret(spec)
                val secret: SecretKey = SecretKeySpec(tmp.encoded, "AES")
                val paramSpec: AlgorithmParameterSpec = IvParameterSpec(byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f))
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.DECRYPT_MODE, secret, paramSpec)
                return cipher.doFinal(encryptedData)
            }

            private fun toHex(aesKey: ByteArray): CharArray {
                val hex = StringBuilder(2 * aesKey.size)
                for (b in aesKey) {
                    hex.append(HEXES[(b.toInt() and 0xF0) shr 4]).append(HEXES[b.toInt() and 0x0F])
                }
                return hex.toString().toCharArray()
            }

            private fun passToHash(password: String): ByteArray {
                return md5(password + MD5_SALT)
            }

            private fun md5(input: String): ByteArray {
                val bytesOfMessage = input.toByteArray(StandardCharsets.UTF_8)
                val md = MessageDigest.getInstance("MD5")
                return md.digest(bytesOfMessage)
            }
        }
    }
}
