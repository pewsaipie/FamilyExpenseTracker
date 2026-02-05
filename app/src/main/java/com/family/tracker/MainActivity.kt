package com.family.tracker

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var balanceText: TextView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Simple UI with a TextView to show the balance
        balanceText = TextView(this).apply {
            textSize = 24f
            setPadding(50, 50, 50, 50)
            text = "Loading Family Balance..."
        }
        setContentView(balanceText)

        // 1. Check for Notification Permission
        if (!isNotificationServiceEnabled()) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }

        // 2. Connect to Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("expenses")
        fetchExpenses()
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val names = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return names?.contains(packageName) == true
    }

    private fun fetchExpenses() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0
                val displayData = StringBuilder("Family Expenses:\n\n")

                for (data in snapshot.children) {
                    val merchant = data.child("merchant").value.toString()
                    val amountStr = data.child("amount").value.toString()
                    
                    // Clean the amount string (remove currency symbols like ₹ or $)
                    val amount = amountStr.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                    
                    total += amount
                    displayData.append("📍 $merchant: $amountStr\n")
                }
                
                displayData.append("\n--- Total Spent: $total ---")
                balanceText.text = displayData.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
