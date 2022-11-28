package dev.pgm.poembox.roomUtils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileOutputStream


class User(var userName: String?, var mail: String?, var register: Boolean) : Activity() {
    @RequiresApi(Build.VERSION_CODES.M)
    val encryptedFile = getEncryptedFile()
    private val dataModel = buildString {
        append(userName)
        append("#")
        append(mail)
    }
    val dataBytes = dataModel.toByteArray()
    val context: Context = applicationContext
    @RequiresApi(Build.VERSION_CODES.M)
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    @RequiresApi(Build.VERSION_CODES.M)
    val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    val file = File(context.cacheDir, "my-secret-file")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.register = false
        val fileOutput: FileOutputStream = writeFileEncrypt()

        if (isUserRegister()) {//user register
            val dataUser = getFileEncryptUser().split("#")
            val userName = dataUser[0]
            this.mail = dataUser[1]
            register = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isUserRegister(): Boolean {
        val dataReader: String = getFileEncryptUser()
        return dataReader.contains("#")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @JvmName("getEncryptedFile1")
    private fun getEncryptedFile() = EncryptedFile.Builder(
        file,
        context,
        mainKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()


    @RequiresApi(Build.VERSION_CODES.M)
    private fun writeFileEncrypt(): FileOutputStream {
        return encryptedFile.openFileOutput().apply {
            write(dataBytes)
            flush()
            close()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getFileEncryptUser(): String {
        encryptedFile.openFileInput().apply {
            val decryptedBytes = readBytes()
            close()
            return String(decryptedBytes)
        }
    }

}
