package edu.put.inf151894

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import coil.load
import edu.put.inf151894.databinding.ActivityGameDetailViewBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt


class GameDetailActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var imageFullScreen: ImageView

    private lateinit var avgRating: TextView
    private lateinit var playerCount: TextView
    private lateinit var releasedIn: TextView
    private lateinit var preview: PreviewView

    private lateinit var dbHandler: MyDBHandler

    private lateinit var viewBinding: ActivityGameDetailViewBinding
    private lateinit var game: Game

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var pictures: List<String>
    private var currentPictureIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGameDetailViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val gameId = intent.getIntExtra("gameId", 0)
        val type = intent.getStringExtra("type")
        dbHandler = MyDBHandler(this, null,null,1)

        if (type == "game") {
            game = dbHandler.getGameById(gameId)
        } else {
            game = dbHandler.getExpansionById(gameId)
        }
        pictures = dbHandler.getPicturesByGame(game)

        preview = findViewById(R.id.viewFinder)

        image = findViewById(R.id.imageView)
        image.load(game.image)

        imageFullScreen = findViewById(R.id.imageFullScreen)
        imageFullScreen.load(game.image)

        cameraExecutor = Executors.newSingleThreadExecutor()

        avgRating = findViewById(R.id.avgRating)
        playerCount = findViewById(R.id.playerCount)
        releasedIn = findViewById(R.id.released)

        avgRating.text = "Average rating: ${(game.avgRating * 10).roundToInt().toDouble() / 10}"
        playerCount.text = "Players: ${game.minPlayers} - ${game.maxPlayers}"
        releasedIn.text = "Released in: ${game.released}"



        image.setOnClickListener {
            imageFullScreen.visibility = View.VISIBLE
            image.visibility = View.INVISIBLE
            avgRating.visibility = View.INVISIBLE
            playerCount.visibility = View.INVISIBLE
            releasedIn.visibility = View.INVISIBLE
        }
        imageFullScreen.setOnClickListener {
            image.visibility = View.VISIBLE
            imageFullScreen.visibility = View.GONE
            avgRating.visibility = View.VISIBLE
            playerCount.visibility = View.VISIBLE
            releasedIn.visibility = View.VISIBLE
        }
        viewBinding.btnAdd.setOnClickListener {
            showImageSourceSelector()
        }
        viewBinding.btnNext.setOnClickListener {
            if (currentPictureIndex < pictures.size) {
                currentPictureIndex += 1
                image.load(pictures[currentPictureIndex - 1])
                imageFullScreen.load(pictures[currentPictureIndex - 1])
            }
        }
        viewBinding.btnPrev.setOnClickListener {
            if (currentPictureIndex > 1) {
                currentPictureIndex -= 1
                image.load(pictures[currentPictureIndex - 1])
                imageFullScreen.load(pictures[currentPictureIndex - 1])

            } else if (currentPictureIndex == 1) {
                currentPictureIndex = 0
                image.load(game.image)
                imageFullScreen.load(game.image)

            }
        }
        viewBinding.btnDelete.setOnClickListener {
            if (currentPictureIndex > 0) {
                val picture = pictures[currentPictureIndex - 1]
                dbHandler.deletePictureById(picture)
                pictures = dbHandler.getPicturesByGame(game)
                image.load(game.image)
                currentPictureIndex = 0
            }
        }
        viewBinding.btnTakePhoto.setOnClickListener {
            takePhoto()
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BoardCollector")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo saved"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    dbHandler.addPicture(game, output.savedUri.toString())
                    pictures = dbHandler.getPicturesByGame(game)

                    preview.visibility = View.GONE
                    image.visibility = View.VISIBLE
                    viewBinding.btnTakePhoto.visibility = View.GONE
                    cameraExecutor.shutdown()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private val launchPhotoSelector = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
                val selectedImageUri = data.data
                dbHandler.addPicture(game, selectedImageUri.toString())
                pictures = dbHandler.getPicturesByGame(game)
            }
        }
    }

    private fun showImageSourceSelector() {
        val items = arrayOf("Camera", "Gallery")
        val dialog = AlertDialog.Builder(this)
            .setTitle("Select image source:")
            .setItems(items) { _, choice ->
                when (choice){
                    0 -> {
                        setupCamera()
                        image.visibility = View.GONE
                        preview.visibility = View.VISIBLE
                        viewBinding.btnTakePhoto.visibility = View.VISIBLE
                    }
                    1 -> {
                        val i = Intent()
                        i.type = "image/*"
                        i.action = Intent.ACTION_GET_CONTENT
                        launchPhotoSelector.launch(i)
                    }
                }
            }
            .create()
        dialog.show()
    }
}