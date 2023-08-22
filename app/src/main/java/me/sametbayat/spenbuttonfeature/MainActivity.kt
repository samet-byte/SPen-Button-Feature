package me.sametbayat.spenbuttonfeature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.samsung.android.sdk.penremote.AirMotionEvent
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEventListener
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenUnit
import com.samsung.android.sdk.penremote.SpenUnitManager
import me.sametbayat.spenbuttonfeature.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {const val TAG ="SPEN_TAG"}
    private var _b : ActivityMainBinding? = null
    private val  b get() = _b!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        Log.d(TAG, "onCreate: Trial started")

            val mSpenUnitManager: SpenUnitManager? = null
            val spenRemote : SpenRemote = SpenRemote.getInstance()

            try {
                Log.d(TAG, "onCreate: CONNECTION CHECK")
                if (!spenRemote.isConnected) {
                    spenRemote.connect(
                        this@MainActivity,
                        object : SpenRemote.ConnectionResultCallback {
                            override fun onSuccess(mSpenUnitManager: SpenUnitManager) {
                                //////success event
                                Log.d(TAG, "onSuccess: CONNECTED")
                                spenButtonStuffNote10LiteButtonOnly(mSpenUnitManager)
                            }
                            override fun onFailure(error: Int) {
                                Log.d(TAG, "onFailure: FAILED CONNECTION")
                                Log.d(TAG, "onFailure: $error")
                            }
                        }
                    )
                }
            } catch (e: NoClassDefFoundError) {
                Log.d(TAG, "onCreate: $e" )
            }
    }

    private var initTime : Long = 0
    private var duration : Long = 0

    private fun spenButtonStuffNote10LiteButtonOnly(mSpenUnitManager: SpenUnitManager) {
        Log.d(TAG, "spenButtonStuff: BUTTON LOOK-UP")

        val mButtonEventListener = SpenEventListener { ev ->
            Log.d(TAG, "onEvent() ACTION DETECTED")
            val buttonEvent = ButtonEvent(ev)

            when (buttonEvent.action) {
                ButtonEvent.ACTION_DOWN -> {
                    Log.d(TAG, "onEvent: SPEN PRESSED")
                    initTime = System.currentTimeMillis()
                }

                ButtonEvent.ACTION_UP -> {
                    duration = System.currentTimeMillis() - initTime
                    initTime = 0
                    val outText = "${duration / 1000.000}sec."
                    b.textButtonDuration.text = outText
                    Log.d(TAG, "onEvent: SPEN RELEASED :  Duration :  $outText")
                }
            }
        }
        val button: SpenUnit = mSpenUnitManager.getUnit(SpenUnit.TYPE_BUTTON)
        mSpenUnitManager.registerSpenEventListener(mButtonEventListener, button)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_G -> if (event!!.metaState and KeyEvent.META_CTRL_ON != 0) Toast.makeText(this,"Single Pressed", Toast.LENGTH_SHORT).show()
        }

        return super.onKeyDown(keyCode, event)
    }
    
    override fun onDestroy() {
        killConnection()
        super.onDestroy()
    }

    private fun killConnection() {
        val spenRemote = SpenRemote.getInstance()
        spenRemote.disconnect(this@MainActivity)
    }
}