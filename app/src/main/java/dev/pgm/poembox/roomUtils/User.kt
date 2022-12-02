package dev.pgm.poembox.roomUtils


import android.os.Build.*
import android.os.Build.VERSION_CODES.*
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dev.pgm.poembox.ContextContentProvider
import java.io.File
import java.io.FileOutputStream


@RequiresApi(M)
class User(var userName: String?, var mail: String?, var register: Boolean) {
    companion object {
        const val MASTER_KEY_ALIAS = "qretxAAAA"
        const val KEY_SIZE = 256
    }

    @RequiresApi(M)
    val encryptedFile = getEncryptedFile()
    private val dataModel = buildString {
        append(userName)
        append("#")
        append(mail)
    }
    private val dataBytes = dataModel.toByteArray()


    @RequiresApi(M)
    private val file: File = File(ContextContentProvider.applicationContext()!!.cacheDir, "my-secret-file")
     init {
        this.register = false
        val fileOutput: FileOutputStream? = writeFileEncrypt()
        if (isUserRegister()) {//user register
            val dataUser = getFileEncryptUser().split("#")
            val userName = dataUser[0]
            this.mail = dataUser[1]
            register = true
        }
    }

    @RequiresApi(M)
    fun isUserRegister(): Boolean {
        val dataReader: String = getFileEncryptUser()
        return dataReader.contains("#")
    }

    @RequiresApi(M)
    @JvmName("getEncryptedFile1")
    private fun getEncryptedFile() = getMasterKey()?.let { masterKey ->
        ContextContentProvider.applicationContext()?.let { context ->
            EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }


    @RequiresApi(M)
    private fun writeFileEncrypt(): FileOutputStream? {
        return encryptedFile?.openFileOutput()?.apply {
            write(dataBytes)
            flush()
            close()
        }

    }


    @RequiresApi(M)
    private fun getFileEncryptUser(): String {
        encryptedFile?.openFileInput()?.apply {
            val decryptedBytes = readBytes()
            close()
            return String(decryptedBytes)
        }
        return ""
    }

    @RequiresApi(M)
    fun getMasterKey(): MasterKey? {
        try {
            val spec = KeyGenParameterSpec.Builder(
                MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE)
                .build()
            return ContextContentProvider.applicationContext()?.let {
                MasterKey.Builder(it)
                    .setKeyGenParameterSpec(spec)
                    .build()
            }
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error on getting master key", e)
        }
        return null
    }

}
