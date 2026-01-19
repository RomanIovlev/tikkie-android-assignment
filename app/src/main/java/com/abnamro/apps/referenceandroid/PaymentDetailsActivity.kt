package com.abnamro.apps.referenceandroid

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.abnamro.apps.referenceandroid.model.PaymentStatus
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)

        val paymentId = intent.getStringExtra("PAYMENT_ID")
        val payment = paymentId?.let { TikkieRepository.getPaymentById(it) }

        if (payment == null) {
            finish()
            return
        }

        setupViews(payment)
    }

    private fun setupViews(payment: com.abnamro.apps.referenceandroid.model.TikkiePayment) {
        val backButton: ImageButton = findViewById(R.id.backButton)
        val title: TextView = findViewById(R.id.title)
        val subtitle: TextView = findViewById(R.id.subtitle)
        val statusButton: TextView = findViewById(R.id.statusButton)
        val totalSettled: TextView = findViewById(R.id.totalSettled)
        val paymentName: TextView = findViewById(R.id.paymentName)
        val amount: TextView = findViewById(R.id.amount)
        val tikkieId: TextView = findViewById(R.id.tikkieId)
        val executionDate: TextView = findViewById(R.id.executionDate)
        val paidBy: TextView = findViewById(R.id.paidBy)
        val createdOn: TextView = findViewById(R.id.createdOn)
        val deleteButton: View = findViewById(R.id.deleteButton)
        val expandButton: ImageButton = findViewById(R.id.expandButton)
        val amountRow: View = findViewById(R.id.amountRow)
        val collapsibleContent: View = findViewById(R.id.collapsibleContent)

        backButton.setOnClickListener {
            finish()
        }

        // Setup collapsible section - expanded by default
        var isExpanded = true
        
        // Set initial state to expanded
        collapsibleContent.visibility = View.VISIBLE
        expandButton.setImageResource(R.drawable.ic_expand_less)
        
        val toggleExpand = {
            isExpanded = !isExpanded
            if (isExpanded) {
                collapsibleContent.visibility = View.VISIBLE
                expandButton.setImageResource(R.drawable.ic_expand_less)
            } else {
                collapsibleContent.visibility = View.GONE
                expandButton.setImageResource(R.drawable.ic_expand_more)
            }
        }

        expandButton.setOnClickListener { toggleExpand() }
        amountRow.setOnClickListener { toggleExpand() }

        title.text = payment.title
        subtitle.text = getString(R.string.tikkie_of, "${payment.currency} ${String.format("%.2f", payment.amount)}")

        when (payment.status) {
            PaymentStatus.PAID_EXPIRED -> {
                statusButton.text = getString(R.string.paid_expired, 1)
                statusButton.visibility = View.VISIBLE
            }
            else -> {
                statusButton.visibility = View.GONE
            }
        }

        totalSettled.text = getString(R.string.total_settled, "${payment.currency} ${String.format("%.2f", payment.amount)}")

        paymentName.text = payment.title
        amount.text = "${payment.currency} ${String.format("%.2f", payment.amount)}"

        payment.tikkieId?.let {
            tikkieId.text = getString(R.string.tikkie_id, it)
            tikkieId.visibility = View.VISIBLE
        } ?: run {
            tikkieId.visibility = View.GONE
        }

        payment.executionDate?.let {
            val dateFormat = SimpleDateFormat(getString(R.string.date_time_format), Locale.getDefault())
            executionDate.text = getString(R.string.execution_date, dateFormat.format(it))
            executionDate.visibility = View.VISIBLE
        } ?: run {
            executionDate.visibility = View.GONE
        }

        payment.paidBy?.let {
            paidBy.text = getString(R.string.paid_on_account, it)
            paidBy.visibility = View.VISIBLE
        } ?: run {
            paidBy.visibility = View.GONE
        }

        payment.createdAt?.let {
            val dateFormat = SimpleDateFormat(getString(R.string.date_format_long), Locale.getDefault())
            createdOn.text = getString(R.string.created_on, dateFormat.format(it))
            createdOn.visibility = View.VISIBLE
        } ?: run {
            createdOn.visibility = View.GONE
        }

        deleteButton.setOnClickListener {
            // Handle delete action
            finish()
        }
    }
}
