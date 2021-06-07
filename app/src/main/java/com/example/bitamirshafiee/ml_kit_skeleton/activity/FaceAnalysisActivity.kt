package com.example.bitamirshafiee.ml_kit_skeleton.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.bitamirshafiee.ml_kit_skeleton.R
import com.example.bitamirshafiee.ml_kit_skeleton.models.Report
import com.example.bitamirshafiee.ml_kit_skeleton.util.PrefrenceManager
import com.google.android.gms.measurement.module.Analytics
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.bottom_sheet_face_detection.*
import kotlinx.android.synthetic.main.image_face_detection.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/*
By
Abdulaziz AlKandry - S00012791
Ahmad Younis – S00046274
Feraas El Sabaa – S00046163

 */

/*
This Class allows the user to use the camera
or upload a picture from their phone
and performs the face detection operation
using the ML kit face detection API,
after that it uses intent to bridge to "AnalyticsActivity"
 */

class FaceAnalysisActivity : AppCompatActivity()
{
    companion object {
    private const val TAG = "FaceAnalysisActivity"
    private const val TAKE_PICTURE = 0
    private const val SELECT_PICTURE = 1

    private const val PERMISSION_CAMERA = 1
    private const val PERMISSION_WRITE = 2
    private const val PERMISSION_READ = 3
}
    var currentPhotoPath: String? = null

    var bottomSheetBehavior : BottomSheetBehavior<*>? = null

    // Max width (portrait mode)
    private var mImageMaxWidth: Int? = null
    // Max height (portrait mode)
    private var mImageMaxHeight: Int? = null

    private var rating: Int? = null

    private var savedPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_analysis)

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        rating = PrefrenceManager(this@FaceAnalysisActivity).rate

        //Log.e(TAG, "setFeeling: ${rating}" )

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun cameraFaceDetection(view : View){
        //check if we have camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            //if not request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )

            //if yes take the picture
        }else{
            dispatchTakePictureIntent()

        }
    }


    private fun dispatchTakePictureIntent() {
        //check if we have access gallery permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //write permission is not granted lets request
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE
            )
        }else{
            //if yes take the picture
            writeOnFile()
        }

    }
    private fun writeOnFile(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        intent.resolveActivity(packageManager)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.d(TAG,"exception: $ex")
            null

        }

        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.bitamirshafiee.ml_kit_skeleton.fileprovider",
            photoFile!!
        )
        Log.d(TAG,"photo uri: $photoURI")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, TAKE_PICTURE)
    }

    fun galleryFaceDetection(view : View){
        //request permission read if not granted for gallery
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_READ
            )
        }else{
            val selectPicture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(selectPicture, SELECT_PICTURE)
        }
    }


    private fun createImageFile() : File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())

        val storageDirectory : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("image_$timeStamp",".jpg", storageDirectory).apply {
            currentPhotoPath = this.absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){

            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            if (requestCode == SELECT_PICTURE) {
                val selectedImage = data?.data
                val selectedImageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage))
                image_face_detection.setImageBitmap(selectedImageBitmap)

                runFaceDetection(imageProcess(selectedImageBitmap))
            }else if(requestCode == TAKE_PICTURE){

                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)

                val ei = ExifInterface(currentPhotoPath)
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)
                var rotatedBitmap : Bitmap? = null

                rotatedBitmap = when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->  rotateImage(bitmap, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 ->  rotateImage(bitmap, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 ->  rotateImage(bitmap, 270F);
                    else ->  bitmap
                }

                image_face_detection.setImageBitmap(rotatedBitmap)
                runFaceDetection(imageProcess(rotatedBitmap!!))
            }


        }
    }



    /*
    This Method creates a firebasevisionImage Object
    from the user’s taken or uploaded picture (as a Bitmap),
    changes the default settings of FirebaseVisionFaceDetectorOptions,
    then passes the image object to method processFaceDetection
     */
    private fun runFaceDetection(bitmap: Bitmap){

        val image = FirebaseVisionImage.fromBitmap(bitmap)


        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS).build()

        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        detector.detectInImage(image)
            .addOnSuccessListener {
                processFaceDetection(it)
            }.addOnFailureListener {
                Toast.makeText(this, "process Failed", Toast.LENGTH_SHORT).show()
            }
    }
/*
processFaceDetection method receives the past image object and uses the API’s method
to get the smiling probability then uses
intent to go to Analytics Activity.
 */
    private fun processFaceDetection(faces : MutableList<FirebaseVisionFace>){

        var smiling = ""

        graphic_overlay.clear()

        if (faces.size == 0){
            Toast.makeText(this, "No face detected", Toast.LENGTH_SHORT).show()
            return
        }

        for ( i in faces.indices) {

            val face = faces[i]

            val faceGraphic =
                com.example.bitamirshafiee.ml_kit_skeleton.activity.FaceContourGraphic(
                    graphic_overlay
                )
            graphic_overlay.add(faceGraphic)
            faceGraphic.updateFace(face)

            val smilingProb = face.smilingProbability

            val leftEyeOpen = face.leftEyeOpenProbability
            val rightEyeOpen = face.rightEyeOpenProbability

            val eyeOpen = (leftEyeOpen + rightEyeOpen) / 2

            val report = Report()
            report.path = savedPath
            report.emotion = ""+PrefrenceManager(this@FaceAnalysisActivity).rate
            report.happienesa = "${smilingProb * 100}"
            report.eye = "${eyeOpen * 100}"

            PrefrenceManager(this@FaceAnalysisActivity).report = report

            val list = PrefrenceManager(this@FaceAnalysisActivity).history
            list!!.add(report)
            PrefrenceManager(this@FaceAnalysisActivity).history = list

            startActivity(Intent(this@FaceAnalysisActivity, AnalyticsActivity::class.java))
            finish()

//            var ss = "Left eye closed"
//            var right = "Right eye closed"

//            var x = i + 1

//            if (leftEyeOpen < 0.7)
//            {ss= "Left eye is closed \n Probability of left eye being open is: $leftEyeOpen"}
//
//
//            else {ss= "Left eye is open \n Probability of left eye being open is: $leftEyeOpen"}
//
//            /*if (rightEyeOpen < 0.7)
//            {right= "Right eye is closed \n Probability of right eye being open is: $rightEyeOpen"}
//
//
//            else {right= "Right eye is open \n Probability of right eye being open is: $rightEyeOpen"}*/
//
//            if (smilingProb< 0.7)
//            {smiling += "Face $x : Smiling Probability: $smilingProb \n  you're not smiling, smile more! \n $ss  \n \n"}
//
//
//            else { smiling += "Face $x : Smiling Probability: $smilingProb \n It looks like you are happy, keep smiling! \n $ss  \n \n"}
//
//            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//            text_view_image_labeling.text = smiling



        }


    }

    private fun imageProcess(bitmap: Bitmap): Bitmap {

        val targetedSize = getTargetedWidthHeight()

        val targetWidth = targetedSize.first
        val maxHeight = targetedSize.second

        // Determine how much to scale down the image
        val scaleFactor = Math.max(
            bitmap.getWidth().toFloat() / targetWidth.toFloat(),
            bitmap.getHeight().toFloat() / maxHeight.toFloat()
        )

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.getWidth() / scaleFactor).toInt(),
            (bitmap.getHeight() / scaleFactor).toInt(),
            true
        )

        savedPath = saveImageToInternalStorage(resizedBitmap)

        return resizedBitmap

    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int
        val maxWidthForPortraitMode = getImageMaxWidth()!!
        val maxHeightForPortraitMode = getImageMaxHeight()!!
        targetWidth = maxWidthForPortraitMode
        targetHeight = maxHeightForPortraitMode
        return Pair(targetWidth, targetHeight)
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxWidth(): Int? {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = image_face_detection.getWidth()
        }

        return mImageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxHeight(): Int? {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight = image_face_detection.getHeight()
        }

        return mImageMaxHeight
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_CAMERA ->{
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //camera permission is granted
                    dispatchTakePictureIntent()
                }
            }
            PERMISSION_WRITE ->{
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    writeOnFile()
                }
            }
            PERMISSION_READ -> {
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val selectPicture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(selectPicture, SELECT_PICTURE)
                }

            }
        }
    }
    // Method to save an image to internal storage
    private fun saveImageToInternalStorage(bitmap:Bitmap):String{

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return file.absolutePath
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@FaceAnalysisActivity, RatingActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this@FaceAnalysisActivity, RatingActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
