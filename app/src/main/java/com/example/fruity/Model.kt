package com.example.fruity
import  android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fruity.ml.BestFloat32
import com.example.fruity.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class Model : ComponentActivity() {

    lateinit var selectBtn: Button
    lateinit var cameraBtn: Button
    lateinit var  bitmap: Bitmap
    private lateinit var photoUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectBtn=findViewById(R.id.selectBtn)
        cameraBtn=findViewById(R.id.cameraBtn)

        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(180,180,ResizeOp.ResizeMethod.BILINEAR))
            .build()

        selectBtn.setOnClickListener{
            var intent:Intent =Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        cameraBtn.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 200)
            } else {
                openCamera()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) { // จากแกลลอรี
                val uri = data?.data
                photoUri = data?.data!!
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                processImage(bitmap) // เรียกใช้ฟังก์ชันการประมวลผล
            } else if (requestCode == 200) { // จากกล้อง
                bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(photoUri))
                processImage(bitmap) // เรียกใช้ฟังก์ชันการประมวลผล
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Handle the error
            null
        }
        // Continue only if the File was successfully created
        photoFile?.also {
            photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, 200)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$timeStamp", ".jpg", storageDir)
    }

    private fun processImage(bitmap: Bitmap) {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(180, 180, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        val model = Model.newInstance(this)

        // สร้างอินพุตสำหรับการประมวลผล
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 180, 180, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(processedImage.buffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
        var result = ""
        // แสดงผลลัพธ์
        if (outputFeature0[0].toInt() == 0) {
            result = "Fresh"
        } else {
            result = "Rotten"
        }

        model.close()
        val Intentresult = Intent(this,Result::class.java).also {
            it.putExtra("EXTRA_RESULT",result)
            it.putExtra("img",photoUri)
            startActivity(it)
        }
    }

    fun backButton(view: View) {
        val Intenthome = Intent(this,Home::class.java)
        startActivity(Intenthome)
    }


}
