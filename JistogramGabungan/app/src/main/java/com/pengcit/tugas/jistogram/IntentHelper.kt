package com.pengcit.tugas.jistogram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import java.io.File

object IntentHelper {

    public val GALLERY = 1
    public val CAMERA = 2

    var mImageUri : Uri? = null

    fun showPictureDialog(activity: Activity) {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
//        val pictureDialogItems = arrayOf("Select photo from gallery")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> selectImageFromGallery(activity)
                1 -> takePhotoFromCamera(activity)
            }
        }
        pictureDialog.show()
    }

    fun saveToSdCard(activity: Activity) {
//        TODO()
    }

    fun takePhotoFromCamera(activity: Activity){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var photo : File? = null
        try {
            photo = createTemporaryFile("picture", ".jpg");
//            photo.delete();
        }
        catch(e : Exception) {
            Log.v("Error", "Can't create file to take picture!")
        }
        mImageUri = Uri.fromFile(photo)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        //start camera intent
        activity.startActivityForResult(intent, CAMERA)
    }

    fun selectImageFromGallery(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(galleryIntent, GALLERY)
    }

    @Throws(Exception::class)
    private fun createTemporaryFile(part: String, ext: String): File {
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.getAbsolutePath() + "/.temp/")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(part, ext, tempDir)
    }
}
