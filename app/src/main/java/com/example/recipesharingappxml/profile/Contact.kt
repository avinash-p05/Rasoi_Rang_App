package com.example.recipesharingappxml.profile



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.recipesharingappxml.R

class Contact : AppCompatActivity() {
    private lateinit var email: TextView
    private lateinit var linkedIn: TextView
    private lateinit var github: TextView
    private lateinit var back : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        email = findViewById(R.id.emailContact)
        linkedIn = findViewById(R.id.linkedin)
        github = findViewById(R.id.github)
        back = findViewById(R.id.backArrowContact)


        back.setOnClickListener(View.OnClickListener {
            finish()
        })

        // Set click listeners for each TextView
        email.setOnClickListener {
            // Replace "your_email@example.com" with your actual email address
            composeEmail("avinashpauskar05@gmail.com", "Bug of Glitch in the app", "Message body")
        }

        linkedIn.setOnClickListener {
            openUrl("https://www.linkedin.com/in/avinash-pauskar-00b597244/")
        }

        github.setOnClickListener {
            openUrl("https://github.com/avinash-p05")
        }
    }

    private fun composeEmail(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
