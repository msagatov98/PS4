package com.example.ps4.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ps4.EditClientActivity
import com.example.ps4.R
import com.example.ps4.util.ClientDialog
import com.example.ps4.util.ClientViewHolder
import com.example.ps4.util.TopSpacingItemDecoration
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_client_list.*

class ClientListActivity : BaseActivity(0) {

    private lateinit var mStorage: StorageReference
    private lateinit var mDatabase: DatabaseReference
    private lateinit var recyclerView : RecyclerView
    private lateinit var options: FirebaseRecyclerOptions<FirebaseClient>
    private lateinit var clientAdapter: FirebaseRecyclerAdapter<FirebaseClient, ClientViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_list)
        enableBottomNavMenu()

        recyclerView = recycler_view

        mDatabase = FirebaseDatabase.getInstance().reference.child("client")

    }

    fun showDialog(client: FirebaseClient?) {
        val dialog = ClientDialog(this)

        //client_fullname.text = client?.name

        //Glide.with(this).load(mStorage.child("${client?.id}/photo").downloadUrl).placeholder(R.drawable.person).error(R.drawable.person).into(icon_client)

        dialog.show()
    }

    class FirebaseClient {
        var id: Int? = null
        var name: String? = null
        var email: String? = null
        var number: String? = null
        var surname: String? = null
        var whatsapp: String? = null
        var birthDate: String? = null
        var instagram: String? = null
    }

    override fun onStart() {
        super.onStart()

        options = FirebaseRecyclerOptions.Builder<FirebaseClient>().setQuery(mDatabase, FirebaseClient::class.java).build()
        mStorage = FirebaseStorage.getInstance().getReference("client/")

        clientAdapter = object : FirebaseRecyclerAdapter<FirebaseClient, ClientViewHolder> (options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.client_list_item, parent, false)
                return ClientViewHolder(view)
            }

            override fun onBindViewHolder(holder: ClientViewHolder?, position: Int, client: FirebaseClient?) {
                holder?.clientId?.text = client?.id.toString()

                val fullName = "${client?.name} ${client?.surname}"

                holder?.clientFullName?.text = fullName

                mStorage.child("${client?.id}/photo").downloadUrl.addOnSuccessListener {
                    Glide.with(this@ClientListActivity).load(it).placeholder(R.drawable.person).error(R.drawable.person).into(holder?.clientImage!!)
                }

                holder?.clientImage?.setOnClickListener {
                    //showDialog(client)
                }

                holder?.clientDelete?.setOnClickListener {
                    mDatabase.child("${client?.id}").removeValue()
                    mStorage.child("${client?.id}/photo").delete()
                }

                holder?.clientEdit?.setOnClickListener {
                    val intent = Intent(this@ClientListActivity, EditClientActivity::class.java)
                    intent.putExtra("clientId", client?.id)
                    startActivity(intent)
                }
            }
        }

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@ClientListActivity)
            addItemDecoration(TopSpacingItemDecoration(30))
            adapter = clientAdapter
        }

        clientAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        clientAdapter.stopListening()
    }
}
