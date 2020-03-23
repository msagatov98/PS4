package com.example.ps4.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.ps4.R
import com.example.ps4.util.Client
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_create_client.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateClientActivity : BaseActivity(2) {

    private val TAKE_PICTURE_REQUEST_CODE = 1
    private lateinit var mImageURI : Uri
    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    private lateinit var idList : ArrayList<Int>
    private lateinit var mStorage: StorageReference
    private lateinit var mDatabase : DatabaseReference

    private var isPhotoTaken = false

    private val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_client)
        enableBottomNavMenu()
        idList = ArrayList()
        mDatabase = FirebaseDatabase.getInstance().getReference("client")
        mStorage = FirebaseStorage.getInstance().reference


        mDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val str = if (month < 9) "${dayOfMonth+1}:0${month+1}:${year}" else "${dayOfMonth+1}:${month+1}:${year}"
            input_birth_date.setText(str)
        }
    }

    fun setBirthDate(v: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, mDateSetListener, year, month, day)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }

    fun createClient(v: View) {
        val id = generateId()
        val name = input_name.text.toString()
        val email = input_email.text.toString()
        val phone = input_phone.text.toString()
        val surname = input_surname.text.toString()
        val whatsapp = input_whatsapp.text.toString()
        val instagram = input_instagram.text.toString()
        val birthDate = input_birth_date.text.toString()

        val client = Client(id = id, name = name, surname = surname, birthDate = birthDate,
            email = email, instagram = instagram, number = phone, whatsapp = whatsapp)

        val ref = mDatabase.child(id.toString())

        ref.setValue(client).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "create", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "doesn't", Toast.LENGTH_LONG).show()
            }
        }

        if (isPhotoTaken)
            saveImageToStorage()
    }


    private fun generateId() : Int {

        val rand = Random()

        var random : Int = rand.nextInt(999999-100000+1) + 100000

        if (idList.isEmpty()) {
            idList.add(random)
        } else {
            while (idList.contains(random)) random = rand.nextInt(999999-100000+1) + 100000

            idList.add(random)
        }

        return idList.last()
    }

    fun takeCameraPicture(v: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val imageFile = createImageFile()

            mImageURI = FileProvider.getUriForFile(this, "com.example.ps4.fileprovider", imageFile)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI)

            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.activity(mImageURI).setAspectRatio(1,1).start(this)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mImageURI = CropImage.getActivityResult(data).uri
            isPhotoTaken = true
        }
    }

    private fun createImageFile() : File {
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun saveImageToStorage() {

        val id = idList.last()

        mStorage.child("client/$id/photo").putFile(mImageURI).addOnCompleteListener {
            if (it.isSuccessful) {
                val url = mStorage.child("client/$id/photo/$mImageURI")
                mDatabase.child("$id/photo").setValue(url.downloadUrl.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.e("CreateClient", "photo saved")

                    } else {
                        Log.e("CreateClient", "photo doesn't saved")
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}