package com.rajat.composecrud.repository

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rajat.composecrud.UserModel

class UserRepository {
    private val database = FirebaseFirestore.getInstance()

    fun fetchAllUsers(callBack: (List<UserModel?>?) -> Unit) {
    database.collection("User").addSnapshotListener { value, error ->
            if (error != null) {
                callBack(emptyList())
                return@addSnapshotListener
            }
            var list = value?.documents?.map {
                it.toObject(UserModel::class.java)?.copy(id = it.id)
            }
            println("Repository List: $list")
            callBack(list)
        }
    }
    fun addUsers(context: Context,userModel: UserModel,callBack: (Boolean) -> Unit){
        database.collection("User").add(userModel).addOnCompleteListener {
            if(it.isSuccessful){
                callBack(true)
                Toast.makeText(context,"User Added",Toast.LENGTH_SHORT).show()
            }else{
                callBack(false)
                println("Data Not Added Error: ${it.exception}")
            }
        }.addOnFailureListener {
            println("Check Exception Error: ${it.message}")
        }
    }
    fun updateUsers(context: Context,userId: String,userModel: UserModel,callBack: (Boolean) -> Unit){
        database.collection("User").document(userId).set(userModel).addOnCompleteListener {
            if(it.isSuccessful){
                callBack(true)
                Toast.makeText(context,"User Updated",Toast.LENGTH_SHORT).show()
            }else{
                callBack(false)
                println("Data Not Updated Error: ${it.exception}")
            }
        }.addOnFailureListener {
            println("Check Exception Error: ${it.message}")
        }
    }
    fun deleteUsers(context: Context,userId:String,callBack:(Boolean)->Unit){
        database.collection("User").document(userId).delete().addOnCompleteListener {
            if(it.isSuccessful){
                callBack(true)
            }else{
                callBack(false)
                println("Data Not Deleted: ${it.exception}")
            }
        }.addOnFailureListener {
            callBack(false)
            println("Check Exception of Delete: ${it.message}")
        }
    }
}