class PayListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Only trigger if the notification comes from Google Pay
        if (sbn.packageName == "com.google.android.apps.nbu.paisa.user") {
            val title = sbn.notification.extras.getString("android.title")
            val text = sbn.notification.extras.getString("android.text")

            // Regex: Looks for "Paid ₹100 to Amazon"
            val regex = Regex("Paid (.*) to (.*)")
            val match = text?.let { regex.find(it) }

            if (match != null) {
                val amount = match.groupValues[1]
                val merchant = match.groupValues[2]
                saveToFirebase(amount, merchant)
            }
        }
    }

    private fun saveToFirebase(amount: String, merchant: String) {
        val database = FirebaseDatabase.getInstance().getReference("expenses")
        val id = database.push().key
        val expense = mapOf("amount" to amount, "merchant" to merchant, "time" to System.currentTimeMillis())
        if (id != null) database.child(id).setValue(expense)
    }
}
