package com.example.fakenamegenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButtonExamples(context: Context) {
    val generatedName = remember { mutableStateOf("Tap to Generate") }
    
    Column(
        modifier = Modifier
            .padding(48.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("নাম জেনারেটর অ্যাপ:")
        Text("Generated: ${generatedName.value}")
        
        Text("সাধারণ ফ্লোটিং অ্যাকশন বাটন:")
        Example(
            onClick = {
                generatedName.value = generateUSName()
                Log.d("FAB", "FAB clicked. Generated: ${generatedName.value}")
            }
        )
        
        Text("ছোট ফ্লোটিং অ্যাকশন বাটন:")
        SmallExample(
            onClick = {
                generatedName.value = generateUSName()
                Log.d("FAB", "Small FAB clicked.")
            }
        )
        
        Text("বড় ফ্লোটিং অ্যাকশন বাটন:")
        LargeExample(
            onClick = {
                generatedName.value = generateUSName()
                Log.d("FAB", "Large FAB clicked.")
            }
        )
        
        Text("টেক্সট সহ এক্সটেন্ডেড FAB:")
        ExtendedExample(
            onClick = {
                generatedName.value = generateUSName()
                Log.d("FAB", "Extended FAB clicked.")
            },
            context = context,
            generatedName = generatedName.value
        )
    }
}

// [START android_compose_components_fab]
@Composable
fun Example(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
    ) {
        Icon(Icons.Filled.Add, "নাম জেনারেট করুন")
    }
}
// [END android_compose_components_fab]

// [START android_compose_components_extendedfab]
@Composable
fun ExtendedExample(onClick: () -> Unit, context: Context, generatedName: String) {
    ExtendedFloatingActionButton(
        onClick = { 
            onClick()
            // নাম শেয়ার করার লজিক
            shareGeneratedName(context, generatedName)
        },
        icon = { Icon(Icons.Filled.Share, "শেয়ার করুন") },
        text = { Text(text = "শেয়ার করুন") },
    )
}
// [END android_compose_components_extendedfab]

// [START android_compose_components_smallfab]
@Composable
fun SmallExample(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, "ছোট FAB")
    }
}
// [END android_compose_components_smallfab]

// [START android_compose_components_largefab]
@Composable
fun LargeExample(onClick: () -> Unit) {
    LargeFloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
    ) {
        Icon(Icons.Filled.Add, "বড় FAB")
    }
}
// [END android_compose_components_largefab]

// নাম জেনারেট করার ফাংশন
fun generateUSName(): String {
    val first = arrayOf("Emma", "Olivia", "Ava", "Sophia", "Mia", "James", "Robert", "Michael", "William", "David")
    val last = arrayOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez")
    val random = kotlin.random.Random
    return "${first[random.nextInt(first.size)]} ${last[random.nextInt(last.size)]}"
}

// নাম শেয়ার করার ফাংশন
fun shareGeneratedName(context: Context, name: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Generated Name: $name")
    }
    context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
}

// নাম ক্লিপবোর্ডে কপি করার ফাংশন
fun copyNameToClipboard(context: Context, name: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Generated Name", name)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied: $name", Toast.LENGTH_SHORT).show()
}
