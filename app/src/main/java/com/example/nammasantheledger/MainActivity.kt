package com.example.nammasantheledger

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nammasantheledger.data.AppDatabase
import com.example.nammasantheledger.data.Transaction
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class TransactionAdapter(context: Context, private val items: List<Transaction>) :
    ArrayAdapter<Transaction>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_transaction, parent, false)
        val item = items[position]
        view.findViewById<TextView>(R.id.avatarText).text =
            item.customerName.first().uppercaseChar().toString()
        view.findViewById<TextView>(R.id.customerNameText).text = item.customerName
        val phoneTv = view.findViewById<TextView>(R.id.phoneNumberText)
        phoneTv.text = if (item.phoneNumber.isNullOrEmpty()) "No Phone" else item.phoneNumber
        view.findViewById<TextView>(R.id.timestampText).text = item.timestamp

        // Use transactionType to format the list
        val amountTv = view.findViewById<TextView>(R.id.amountText)
        if (item.transactionType == -1) {
            amountTv.text = "- ₹${String.format("%.2f", kotlin.math.abs(item.amount))}"
            amountTv.setTextColor(android.graphics.Color.parseColor("#2E7D32")) // Green for repay
        } else {
            amountTv.text = "+ ₹${String.format("%.2f", kotlin.math.abs(item.amount))}"
            amountTv.setTextColor(android.graphics.Color.RED) // Red for lend
        }

        return view
    }
}

class MainActivity : AppCompatActivity() {

    private val transactions = ArrayList<Transaction>()
    private var adapter: TransactionAdapter? = null
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var db: AppDatabase
    private lateinit var totalTransactionsTv: TextView
    private lateinit var totalAmountTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        val customerNameInput = findViewById<EditText>(R.id.customerName)
        val phoneInput        = findViewById<EditText>(R.id.phoneNumber)
        val amountInput       = findViewById<EditText>(R.id.amount)
        val historyList       = findViewById<ListView>(R.id.historyList)
        totalTransactionsTv   = findViewById(R.id.totalTransactions)
        totalAmountTv         = findViewById(R.id.totalAmount)
        val dateText          = findViewById<TextView>(R.id.dateText)
        val clearAll          = findViewById<TextView>(R.id.clearAll)
        val lendButton = findViewById<Button>(R.id.lendButton)
        val repayButton = findViewById<Button>(R.id.repayButton)

        lendButton.setOnClickListener {
            processTransaction(customerNameInput, phoneInput, amountInput, 1) // 1 for Lend
        }

        repayButton.setOnClickListener {
            processTransaction(customerNameInput, phoneInput, amountInput, -1) // -1 for Repay
        }

        // Load profile shop name into header
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val shopNameTv   = findViewById<TextView>(R.id.shopNameHeader)
        shopNameTv.text  = profilePrefs.getString("shopName", "Namma Santhe Ledger") ?: "Namma Santhe Ledger"

        dateText.text = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date())

        loadTransactions()
        adapter = TransactionAdapter(this, transactions)
        historyList.adapter = adapter

        historyList.setOnItemClickListener { _, _, position, _ ->
            val t = transactions[position]
            showUpdateDialog(t)
        }


        // ── Long Press to Delete ─────────────────────────────────────────────
//        historyList.setOnItemLongClickListener { _, _, position, _ ->
//            val t = transactions[position]
//            val show = AlertDialog.Builder(this)
//                .setTitle("🗑️ Delete Transaction")
//                .setMessage(
//                    "Delete ${t.customerName}'s entry of ₹${
//                        String.format(
//                            "%.2f",
//                            t.amount
//                        )
//                    }?"
//                )
//                .setPositiveButton("Delete") { _, _ ->
//                    executor.execute {
//                        db.transactionDao().delete(t)
//                        val all = db.transactionDao().getAll()
//                        runOnUiThread {
//                            transactions.clear()
//                            transactions.addAll(all)
//                            adapter!!.notifyDataSetChanged()
//                            updateSummary()
//                            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .setNegativeButton("Cancel", null)
//                .show()
//            true
//        }

        // ── Clear All ────────────────────────────────────────────────────────
        clearAll.setOnClickListener {
            if (transactions.isEmpty()) {
                Toast.makeText(this, "Nothing to clear", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("⚠️ Clear All")
                .setMessage("Delete all ${transactions.size} transactions?")
                .setPositiveButton("Clear All") { _, _ ->
                    executor.execute {
                        db.transactionDao().deleteAll()
                        runOnUiThread {
                            transactions.clear()
                            adapter!!.notifyDataSetChanged()
                            updateSummary()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    private fun showUpdateDialog(transaction: Transaction) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_transaction, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val titleTv = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val balanceTv = dialogView.findViewById<TextView>(R.id.currentBalanceTv)
        val input = dialogView.findViewById<EditText>(R.id.adjustAmountInput)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)
        val btnReduce = dialogView.findViewById<Button>(R.id.btnReduce)

        titleTv.text = "Update ${transaction.customerName}"
        balanceTv.text = "Current Balance: ₹${String.format("%.2f", transaction.amount)}"

        // Helper function to save changes
        fun applyAdjustment(isAddition: Boolean) {
            val amountValue = input.text.toString().toDoubleOrNull()
            if (amountValue == null || amountValue <= 0) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return
            }

            executor.execute {
                // If "Gave More", we add to debt. If "Got Payment", we subtract.
                if (isAddition) transaction.amount += amountValue
                else transaction.amount -= amountValue

                db.transactionDao().update(transaction)
                val all = db.transactionDao().getAll()

                runOnUiThread {
                    transactions.clear()
                    transactions.addAll(all)
                    adapter?.notifyDataSetChanged()
                    updateSummary()
                    dialog.dismiss()
                    Toast.makeText(this, "Balance Updated", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnAdd.setOnClickListener { applyAdjustment(true) }
        btnReduce.setOnClickListener { applyAdjustment(false) }

        dialog.show()
    }
    private fun processTransaction(nameEt: EditText, phoneEt: EditText, amountEt: EditText, type: Int) {
        val name = nameEt.text.toString().trim()
        val phone = phoneEt.text.toString().trim()
        val amtStr = amountEt.text.toString().trim()

        if (name.isEmpty() || amtStr.isEmpty()) {
            Toast.makeText(this, "Name and amount are required", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amtStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

        // Create transaction with the 'transactionType' field
        val transaction = Transaction(
            customerName = name,
            phoneNumber = phone,
            amount = amount,
            timestamp = timestamp,
            transactionType = type
        )

        executor.execute {
            db.transactionDao().insert(transaction)
            val all = db.transactionDao().getAll()
            runOnUiThread {
                transactions.clear()
                transactions.addAll(all)
                adapter?.notifyDataSetChanged()
                updateSummary()
                nameEt.setText("")
                phoneEt.setText("")
                amountEt.setText("")
                val msg = if (type == 1) "Lent ₹$amount" else "Received ₹$amount"
                Toast.makeText(this, "✅ $msg", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh shop name if profile was updated
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        findViewById<TextView>(R.id.shopNameHeader).text =
            profilePrefs.getString("shopName", "Namma Santhe Ledger")
    }

    // ── Options Menu (3-dot) ─────────────────────────────────────────────────
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout") { _, _ ->
                        getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("isLoggedIn", false).apply()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadTransactions() {
        executor.execute {
            val all = db.transactionDao().getAll()
            runOnUiThread {
                transactions.clear()
                transactions.addAll(all)
                adapter?.notifyDataSetChanged()
                updateSummary()
            }
        }
    }

    private fun updateSummary() {
        totalTransactionsTv.text = transactions.size.toString()

        // Multiply amount by transactionType (1 or -1) to get the net balance
        val balance = transactions.sumOf { it.amount * it.transactionType }

        totalAmountTv.text = "₹${String.format("%.2f", balance)}"

        // Visual Polish: Red if customers owe you money, Green if settled
        if (balance > 0) {
            totalAmountTv.setTextColor(android.graphics.Color.RED)
        } else {
            totalAmountTv.setTextColor(android.graphics.Color.parseColor("#2E7D32")) // Dark Green
        }
    }
}
