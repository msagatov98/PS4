package com.example.ps4

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ps4.activity.ClientListActivity
import com.example.ps4.util.Client
import com.example.ps4.util.ValueEventListenerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_client.*


class EditClientActivity : AppCompatActivity() {

    private var clientId: Int = 0
    private lateinit var mStorage: StorageReference
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mClient: ClientListActivity.FirebaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_client)

        clientId = intent.getIntExtra("clientId", 0)

        mStorage = FirebaseStorage.getInstance().reference.child("client/${clientId}/photo")
        mDatabase = FirebaseDatabase.getInstance().reference.child("client/$clientId/")

        img_close_edit_client.setOnClickListener {
            startActivity(Intent(this, ClientListActivity::class.java))
            finish()
        }

        ic_apple_edit.setOnClickListener {
            upgradeClient()
        }

        mDatabase.addListenerForSingleValueEvent(ValueEventListenerAdapter {
             mClient = it.getValue(ClientListActivity.FirebaseClient::class.java)!!

            mStorage.downloadUrl.addOnSuccessListener {
                Glide.with(this@EditClientActivity).load(it).placeholder(R.drawable.person).error(R.drawable.person).into(img_client!!)
            }

            input_name.setText(mClient.name, TextView.BufferType.EDITABLE)
            input_email.setText(mClient.email, TextView.BufferType.EDITABLE)
            input_phone.setText(mClient.number, TextView.BufferType.EDITABLE)
            input_surname.setText(mClient.surname, TextView.BufferType.EDITABLE)
            input_whatsapp.setText(mClient.whatsapp, TextView.BufferType.EDITABLE)
            input_instagram.setText(mClient.instagram, TextView.BufferType.EDITABLE)
            input_birth_date.setText(mClient.birthDate, TextView.BufferType.EDITABLE)

        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ClientListActivity::class.java))
        finish()
    }

    private fun upgradeClient() {
        val client = Client(
            id = clientId,
            name = input_name.text.toString(),
            email = input_email.text.toString(),
            number = input_phone.text.toString(),
            surname = input_surname.text.toString(),
            whatsapp = input_whatsapp.text.toString(),
            instagram = input_instagram.text.toString(),
            birthDate = input_birth_date.text.toString()
        )

        val error = validate(client)

        if (error == null) {
            val updateMaps = mutableMapOf<String, Any>()

            if (mClient.name != client.name) updateMaps["name"] = client.name
            if (mClient.email != client.email) updateMaps["email"] = client.email
            if (mClient.number != client.number) updateMaps["number"] = client.number
            if (mClient.surname != client.surname) updateMaps["surname"] = client.surname
            if (mClient.whatsapp != client.whatsapp) updateMaps["whatsapp"] = client.whatsapp
            if (mClient.instagram != client.instagram) updateMaps["instagram"] = client.instagram
            if (mClient.birthDate != client.birthDate) updateMaps["birthDate"] = client.birthDate

            mDatabase.updateChildren(updateMaps).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Client saved", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, ClientListActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun validate(client: Client) : String? = when {
        client.name.isEmpty() -> "Please Enter name"
        client.surname.isEmpty() -> "Please Enter surname"
        else -> null
    }
}