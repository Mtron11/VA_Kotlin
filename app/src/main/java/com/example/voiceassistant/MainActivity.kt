package com.example.voiceassistent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.db.DBHelper
import com.example.voiceassistent.db.MessageEntity
import com.example.voiceassistent.message.Message
import com.example.voiceassistent.message.MessageListAdapter
import java.io.Serializable
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val sendButton: Button by lazy { findViewById(R.id.sendButton) }
    private val questionField: EditText by lazy { findViewById(R.id.questionField) }
    private lateinit var chatMessageList: RecyclerView
    protected var messageListAdapter: MessageListAdapter = MessageListAdapter()
    private lateinit var textToSpeech: TextToSpeech
    var sPref: SharedPreferences? = null
    val APP_PREFERENCES = "mysettings"
    private var isLight = true
    private val THEME = "THEME"
    private var isVolume = true
    private val VOLUME_ASSISTANT = "VOLUME"
    var dbHelper: DBHelper? = null
    var dataBase: SQLiteDatabase? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i("LOG", "nightModeChanged")

        when (item.itemId) {
            R.id.mode_theme -> {
                if (isLight) {
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                    isLight = false
                } else {
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                    isLight = true
                }
            }

            R.id.voice_btn -> {
                isVolume = !isVolume
                if (isVolume) {
                    item.setIcon(R.drawable.volume_off)
                } else {
                    item.setIcon(R.drawable.volume_on)
                }
            }

            R.id.clearMessages -> {
                dataBase?.delete(dbHelper?.TABLE_NAME, null, null)
                messageListAdapter.messageList.clear()
                messageListAdapter.notifyDataSetChanged()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var menuItem = menu?.findItem(R.id.mode_theme)
        if (isLight) {
            menuItem?.setIcon(R.drawable.mode_night_theme_icon)
        } else {
            menuItem?.setIcon(R.drawable.mode_day_theme_icon)
        }
        menuItem = menu?.findItem(R.id.voice_btn)
        if (isVolume) {
            menuItem?.setIcon(R.drawable.volume_off)
        } else {
            menuItem?.setIcon(R.drawable.volume_on)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun saveMessages() {
        for (i in 0..<messageListAdapter.messageList.size) {
            val entity = MessageEntity(messageListAdapter.messageList[i])
            val contentValues = ContentValues()
            contentValues.put(dbHelper?.FIELD_MESSAGE, entity.text)
            contentValues.put(dbHelper?.FIELD_SEND, entity.isSend)
            contentValues.put(dbHelper?.FIELD_DATE, entity.date)
            dataBase?.insert(dbHelper?.TABLE_NAME, null, contentValues)
        }
    }

    private fun fillMessages() {
        val cursor: Cursor = dataBase!!.query(
            dbHelper!!.TABLE_NAME,
            null, null, null,
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val messageIndex = cursor.getColumnIndex(dbHelper!!.FIELD_MESSAGE)
            val dateIndex = cursor.getColumnIndex(dbHelper!!.FIELD_DATE)
            val sendIndex = cursor.getColumnIndex(dbHelper!!.FIELD_SEND)
            do {
                val entity = MessageEntity(
                    cursor.getString(messageIndex),
                    cursor.getString(dateIndex),
                    cursor.getInt(sendIndex) == 1
                )
                val message = Message("", Date(), false).getMessage(entity)
                messageListAdapter.messageList.add(message)
            } while (cursor.moveToNext())
        }
        cursor.close()
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
    }

    override fun onStop() {
        val editor: SharedPreferences.Editor = sPref!!.edit()
        editor.putBoolean(THEME, isLight)
        editor.putBoolean(VOLUME_ASSISTANT, isVolume)
        editor.apply()
        dataBase?.delete(dbHelper?.TABLE_NAME, null, null)
        saveMessages()
        super.onStop()
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("LOG", "onCreate")
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        isLight = sPref!!.getBoolean(THEME, true)
        isVolume = sPref!!.getBoolean(VOLUME_ASSISTANT, true)
        if (!isLight) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            isLight = false
        }
        dbHelper = DBHelper(this)
        dataBase = dbHelper!!.writableDatabase
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatMessageList = findViewById(R.id.chatMessageList)
        chatMessageList.layoutManager = LinearLayoutManager(this)
        chatMessageList.adapter = messageListAdapter

        if (savedInstanceState != null) {
            messageListAdapter.messageList =
                savedInstanceState.getSerializable("messageList") as MutableList<Message>
        } else {
            fillMessages()
        }
        sendButton.setOnClickListener {
            onSend()
        }

        textToSpeech = TextToSpeech(applicationContext) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }
        }


    }

    override fun onDestroy() {
        dataBase!!.close()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putSerializable("messageList", messageListAdapter.messageList as Serializable)
        }
        super.onSaveInstanceState(outState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun onSend() {
        Log.i("LOG", "onSend")
        val text = questionField.text.toString()
        messageListAdapter.messageList.add(Message(text, isSend = true))

        AI().getAnswer(text) {
            messageListAdapter.messageList.add(Message(it, isSend = false))
            messageListAdapter.notifyDataSetChanged()
            if (isVolume) {
                textToSpeech.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
            }
            questionField.text.clear()

            chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)

            dismissKeyboard()
        }
    }

    private fun dismissKeyboard() {
        Log.i("LOG", "dismissKeyboard")
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}