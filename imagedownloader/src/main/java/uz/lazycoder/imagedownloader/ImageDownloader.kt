package uz.lazycoder.imagedownloader

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageDownloader {
    companion object {

        fun downloadImage(context: Context, imgUrl: String) {
            val errorMessage = "Image not saved"
            val bitmap = getBitmapFromUrl(imgUrl)

            try {
                if (bitmap == null) throw Exception(errorMessage)

                val fileName =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_DCIM}/ImageDownloader"
                        )
                    }

                    val uri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    resolver.openOutputStream(uri ?: Uri.EMPTY).use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        it?.flush()
                        it?.close()
                    }
                } else {
                    val filePath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath

                    val dir = File("${filePath}/ImageDownloader")
                    if (!dir.exists()) dir.mkdir()

                    val file = File(dir, "${fileName}.jpg")
                    file.createNewFile()

                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(file.path),
                        arrayOf("image/jpeg"),
                        null
                    )
                }

                Toast.makeText(context, "Image successfully saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: errorMessage, Toast.LENGTH_SHORT).show()
            }

        }

        private fun getBitmapFromUrl(imgUrl: String): Bitmap? {
            var result: Bitmap? = null

            try {
                Picasso.get().load(imgUrl).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        result = bitmap
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                        throw Exception()
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                })
            } catch (e: Exception) {
                return null
            }

            return result
        }

    }
}