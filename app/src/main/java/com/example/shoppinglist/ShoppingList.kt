package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class ShoppingItems(val id:Int,
                         var name:String,
                         var quantity: Int,
                         var purchased: Boolean = false,
                         var isEditing:Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    var sitems by remember { mutableStateOf(listOf<ShoppingItems>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = { showDialog = true},
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("Add Item")
        }
        if (sitems.isEmpty()) {
            val randomImage = R.drawable.pngwing_com

            Image(
                modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally),
                painter = painterResource(id = randomImage),
                contentDescription = ""
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            ) {
            items(sitems) {
                item ->
                if(item.isEditing){
                    ShoppingItemEditor(item = item, onEditComplete = {
                        editedName, editedQuantity ->
                        sitems = sitems.map{it.copy(isEditing = false)}
                        val editedItem = sitems.find{it.id == item.id}
                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }

                    })
                }else{
                    ShoppingListItem(
                        item = item,
                        onEditClick = {
                        sitems = sitems.map { it.copy(isEditing = it.id == item.id)}

                    },
                        onDeleteClick = {
                            sitems = sitems - item
                        },
                        onItemClick = {
                            sitems = sitems.map {
                                if(it.id == item.id){
                                    it.copy(purchased = !it.purchased)

                                }else{
                                    it
                                }
                            }
                        })

                }

            }

        }
        if(showDialog){
            AlertDialog(onDismissRequest = { showDialog = false },
                confirmButton = {
                        Row ( modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween){
                            Button(onClick = {
                                if(itemName.isNotBlank()){
                                    val newItem = ShoppingItems(
                                        id = sitems.size+1,
                                        name = itemName,
                                        quantity = itemQuantity.toInt()
                                    )
                                    sitems = sitems + newItem
                                    showDialog = false
                                    itemName = ""
                                }

                            }) {
                                Text("Add")
                            }
                            Button(onClick = {showDialog = false}) {
                                Text("Cancel")
                            }
                        }

                },
                title = {Text(text = "Add Items")},
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = {itemName=it},
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                        )
                        OutlinedTextField(
                            value = itemQuantity,
                            onValueChange = {itemQuantity=it},
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                        )
                    }

                }
            )
        }
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItems, onEditComplete: (String, Int)->Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier.fillMaxWidth()
        .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly
    )
    {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = {editedName=it},
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity=it},
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
        }) {
            Text("Save")
        }
    }
}
@Composable
fun ShoppingListItem(
    item: ShoppingItems,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: (Boolean) -> Unit,
    ) {
    Row (
        modifier = Modifier.
        padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color.Gray),
                shape = RoundedCornerShape(20)
        )
            .alpha(if (item.purchased) 0.5f else 1f),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Checkbox(
            checked = item.purchased,
            onCheckedChange = onItemClick,
            modifier = Modifier.padding(8.dp)
        )
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    ShoppingListApp()
}