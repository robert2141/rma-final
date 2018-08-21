package com.example.robertstitic.detector

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.robertstitic.detector.tensorflow.Classifier
import com.example.robertstitic.detector.tensorflow.TensorFlowImageClassifier
import com.wonderkiln.camerakit.CameraKitImage
import kotlinx.android.synthetic.main.activity_detect.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

class DetectActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetectActivity"
        private const val INPUT_WIDTH = 300
        private const val INPUT_HEIGHT = 300
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128f
        private const val INPUT_NAME = "Mul"
        private const val OUTPUT_NAME = "final_result"
        private const val MODEL_FILE = "file:///android_asset/rounded_graph.pb"
        private const val LABEL_FILE = "file:///android_asset/retrained_labels.txt"
    }

    private var classifier: Classifier? = null
    private var initializeJob: Job? = null
    private var result: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)
        initializeTensorClassifier()
        btnRecognize.setOnClickListener {
            setVisibilityOnCaptured(false)
            cameraView.captureImage {
                onImageCaptured(it)
            }
        }
        btnMoreInfo.setOnClickListener {
            val intent = Intent(this, MoreInfoActivity::class.java)
            intent.putExtra("Title", result)
            startActivity(intent)
        }
    }

    private fun onImageCaptured(it: CameraKitImage) {
        val bitmap = Bitmap.createScaledBitmap(it.bitmap, INPUT_WIDTH, INPUT_HEIGHT, false)
        showCapturedImage(bitmap)

        classifier?.let {
            try {
                showRecognizedResult(it.recognizeImage(bitmap))
            } catch (e: java.lang.RuntimeException) {
                Log.e(TAG, "Crashing due to classification.closed() before the recognizer finishes!")
            }
        }
    }

    private fun showRecognizedResult(results: MutableList<Classifier.Recognition>) {
        runOnUiThread {
            setVisibilityOnCaptured(true)
            if (results.isEmpty()) {
                textResult.text = getString(R.string.result_no_component_found)
                btnMoreInfo.visibility = View.GONE
            } else {
                val component = results[0].title
                this.result = component
                val confidence = results[0].confidence
                textResult.text = when {
                    confidence > 0.90 -> getString(R.string.result_confident_component_found, component)
                    confidence > 0.75 -> getString(R.string.result_think_component_found, component)
                    else -> getString(R.string.result_maybe_component_found, component)
                }
            }
        }
    }

    private fun showCapturedImage(bitmap: Bitmap?) {
        runOnUiThread {
            imageCaptured.visibility = View.VISIBLE
            imageCaptured.setImageBitmap(bitmap)
        }
    }

    private fun setVisibilityOnCaptured(isDone: Boolean) {
        btnRecognize.isEnabled = isDone
        if (isDone) {
            imageCaptured.visibility = View.VISIBLE
            textResult.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            btnMoreInfo.visibility = View.VISIBLE
        } else {
            imageCaptured.visibility = View.GONE
            textResult.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun initializeTensorClassifier() {
        initializeJob = launch {
            try {
                classifier = TensorFlowImageClassifier.create(
                        assets, MODEL_FILE, LABEL_FILE, INPUT_WIDTH, INPUT_HEIGHT,
                        IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME)

                runOnUiThread {
                    btnRecognize.isEnabled = true
                }
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }
    }

    private fun clearTensorClassifier() {
        initializeJob?.cancel()
        classifier?.close()
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTensorClassifier()
    }
}
