package dev.pgm.poembox

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        internal const val ALIAS = "protectedKey"
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    internal fun encrypt(byteArray: ByteArray, outputStream: OutputStream): ByteArray {

        val encryptBytes = encryptCipher.doFinal(byteArray)
        outputStream.use { stream ->
            stream.write(encryptCipher.iv.size)
            stream.write(encryptCipher.iv)
            stream.write(encryptBytes.size)
            stream.write(encryptBytes)
        }
        return encryptBytes
    }

    internal fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use { stream ->
            val ivSize = stream.read()

            if (ivSize == -1) {
                return "".toByteArray()
            } else {
                val iv = ByteArray(ivSize)
                stream.read(iv)

                val encryptedByteSize = stream.read()
                val encryptedBytes = ByteArray(encryptedByteSize)
                stream.read(encryptedBytes)

                getDecryptCipherForIv(iv).doFinal(encryptedBytes)
            }
        }
    }
}