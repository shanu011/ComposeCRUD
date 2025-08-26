package com.rajat.composecrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.rajat.composecrud.ui.theme.ComposeCRUDTheme
import com.rajat.composecrud.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCRUD()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ComposeCRUD(){
    var db = Firebase.firestore
    var context = LocalContext.current
    val userViewModel: UserViewModel = viewModel()
    val firebaseStatus by userViewModel.userAs.observeAsState()
    val users by userViewModel.userList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var alertShow by remember { mutableStateOf(false) }
    var userList = remember { mutableListOf<UserModel?>() }
    var index by remember { mutableStateOf<Int?>(-1) }
    var userName by remember { mutableStateOf("") }
    var userError by remember { mutableStateOf<String?>(null) }
    //LaunchedEffect(Unit) {
    val isLoading by userViewModel.isLoading.collectAsState()







    if (showDialog) {
        if (index!! > -1) {
        userName = userList[index!!]?.name.toString()
    }
        Dialog(
            onDismissRequest = {
                showDialog = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)

        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Add User Name",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold,

                    )
                    OutlinedTextField(
                        value = userName,
                        onValueChange = {
                            userName = it
                            userError = null
                        },
                        label = {
                            Text("User Name")
                        },
                        isError = userError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),

                        )

                    userError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if(userName.isEmpty()){
                                userError = "Enter User Name"
                            }else {
                                if (index!! > -1) {
                                  //  userList.set(index!!, userName)
                                    userName = ""
                                    showDialog = false
                                } else {
                                    var userModel = UserModel(name = userName)
                                   userViewModel.addUsers(context,userModel)
                                    userName = ""
                                    showDialog = false

                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp),
                        contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp),
                        shape = RoundedCornerShape(5.dp),


                        )
                    {
                        Text("ADD")
                    }
                }

            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("CRUD in Compose")
            },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray,
                    titleContentColor = Color.Black
                )
            )
        },


        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            },
                containerColor = Color.Gray
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },

        floatingActionButtonPosition = FabPosition.End



    )
    {innerPadding->
        if(isLoading) {
            Box(Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }else {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(users!!.size) { position ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp).pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        alertShow = true
                                        index = position
                                    },
                                    onTap = {
                                        index = position
                                        showDialog = true
                                    }
                                )
                            },
                        elevation = CardDefaults.cardElevation(4.dp),

                        ) {
                        Text(
                            users!![position]?.name.toString(),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

if(alertShow){
    AlertDialog(onDismissRequest = {alertShow = false},
        title = { Text("Delete") },
        text = {
            Text("Delete")
        },
        confirmButton = {
            TextButton(onClick = {
                userList.removeAt( index = index!!)
                alertShow = false
            }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                alertShow = false
            }) { Text("Cancel") }
        }
        )
}
}


