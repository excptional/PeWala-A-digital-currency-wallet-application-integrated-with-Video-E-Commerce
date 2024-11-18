package com.te.celer.db

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

class AESCrypt {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(plainText: String, key: ByteArray): String? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
            val keySpec = SecretKeySpec(key, "AES")
            val iv = ByteArray(cipher.blockSize)
            val ivParameterSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray())
            Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            Log.e("AESCrypt", "Error encrypting: ${e.message}")
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(encryptedText: String, key: ByteArray): String? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
            val keySpec = SecretKeySpec(key, "AES")
            val iv = ByteArray(cipher.blockSize)
            val ivParameterSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
            val encryptedBytes = Base64.getDecoder().decode(encryptedText)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            Log.e("AESCrypt", "Error decrypting: ${e.message}")
            null
        }
    }
}

