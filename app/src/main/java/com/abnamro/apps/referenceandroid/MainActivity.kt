package com.abnamro.apps.referenceandroid

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abnamro.apps.referenceandroid.adapter.PaymentAdapter
import com.abnamro.apps.referenceandroid.model.TikkiePayment

class MainActivity : AppCompatActivity() {

    private lateinit var paymentRecyclerView: RecyclerView
    private lateinit var addFab: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paymentRecyclerView = findViewById(R.id.paymentRecyclerView)
        addFab = findViewById(R.id.addFab)
        val profileButton: ImageButton = findViewById(R.id.profileButton)

        setupRecyclerView()
        setupFab()
        setupProfileButton(profileButton)
    }

    private fun setupRecyclerView() {
        val payments = TikkieRepository.getAllPayments()
        val adapter = PaymentAdapter(
            payments = payments,
            onPaymentClick = { payment ->
                openPaymentDetails(payment)
            }
        )
        paymentRecyclerView.layoutManager = LinearLayoutManager(this)
        paymentRecyclerView.adapter = adapter
    }

    private fun setupFab() {
        addFab.setOnClickListener {
            openStep1(null)
        }
    }

    private fun openPaymentDetails(payment: TikkiePayment) {
        val intent = Intent(this, PaymentDetailsActivity::class.java)
        intent.putExtra("PAYMENT_ID", payment.id)
        startActivity(intent)
    }

    private fun openStep1(payment: TikkiePayment?) {
        val intent = Intent(this, RequestPaymentStep1Activity::class.java)
        payment?.let {
            intent.putExtra("PAYMENT_ID", it.id)
        }
        startActivity(intent)
    }

    private fun setupProfileButton(profileButton: ImageButton) {
        profileButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.profile_settings -> {
                        true
                    }
                    R.id.profile_logout -> {
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
