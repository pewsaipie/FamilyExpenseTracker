class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if we have permission to listen to notifications
        if (!isNotificationServiceEnabled()) {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        setupFirebaseListener()
    }

    private fun setupFirebaseListener() {
        val ref = FirebaseDatabase.getInstance().getReference("expenses")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Here you would update your UI List and calculate the total sum
                // for your family members to see.
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
