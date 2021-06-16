package com.example.lightcircle

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var adapter: NfcAdapter? = null
    //private val memory : TextView = findViewById<TextView>(R.id.tv_memory)
    //private lateinit var memory_field: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNfcAdapter()
        var memory_field: TextView = findViewById<TextView>(R.id.tv_memory)
        var read_button: Button = findViewById<Button>(R.id.b_read)

        read_button.setOnClickListener {
            memory_field.text = getUID()
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = WritableTag(tagFromIntent)
        } catch (e: FormatException) {
            return
        }
        tagId = tag!!.tagId
        showToast("Tag tapped: $tagId")

//        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
//            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
//            if (rawMsgs != null) {
//                onTagTapped(NfcUtils.getUID(intent), NfcUtils.getData(rawMsgs))
//            }
//        }
    }

    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            adapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            //Log.e(getTag(), "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            //Log.e(getTag(), "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun getUID(intent: Intent): String {
        val myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val str = String(myTag!!.id)
        return str
    }
}
