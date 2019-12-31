package io.ghyeok.stickyswitchdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.ghyeok.stickyswitch.widget.StickySwitch
import io.ghyeok.stickyswitch.widget.StickySwitch.OnSelectedChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set Selected Change Listener
        stickySwitch.onSelectedChangeListener = object : OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                Log.d(TAG, "Now Selected : " + direction.name + ", Current Text : " + text)
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}