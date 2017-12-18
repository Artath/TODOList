package com.example.todolist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.*
import android.view.View.MeasureSpec

class MainActivity : AppCompatActivity() {

    private var databaseHelper  = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        populateListView()
    }

    private fun populateListView() {
        var data = databaseHelper.data

        var listOfActiveTasks = ArrayList<String>()
        var listOfDoneTasks = ArrayList<String>()


        while(data.moveToNext()) {
            var currentStatus = data.getInt(data.getColumnIndex("status"))
            if(currentStatus == 1) {
                listOfActiveTasks.add(data.getString(data.getColumnIndex("task")))
            }
            if (currentStatus == 0) {
                listOfDoneTasks.add(data.getString(data.getColumnIndex("task")))
            }
        }
        data.close()


        var adapterActive = ArrayAdapter<String>(applicationContext, R.layout.item_1, listOfActiveTasks)
        listViewActive.adapter = adapterActive
        setDynamicHeight(listViewActive)
        var adapterDone = ArrayAdapter<String>(applicationContext, R.layout.item_2, listOfDoneTasks)
        listViewDone.adapter = adapterDone
        setDynamicHeight(listViewDone)

        listViewActive.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itemTask = adapterView.getItemAtPosition(i).toString()
            val itemIdCursor = databaseHelper.getItemID(itemTask)
            val itemStatusCursor = databaseHelper.getItemStatus(itemTask)
            var itemStatus = 1
            while(itemStatusCursor.moveToNext()){
                itemStatus = itemStatusCursor.getInt(0)
            }
            var itemID = -1
            while (itemIdCursor.moveToNext()) {
                itemID = itemIdCursor.getInt(0)
            }
            if (itemID > -1) {
                val taskEditText = EditText(this)
                taskEditText.setText(itemTask)
                val dialog = AlertDialog.Builder(this)
                        .setTitle("Edit Task")
                        .setView(taskEditText)
                        .setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
                            val newTask = taskEditText.text.toString()
                            if (newTask != "") {
                                databaseHelper.updateTask(newTask, itemID, itemTask)
                                populateListView()
                            } else {
                                toastMessage("You must enter a task")
                            }
                        })
                        .setNegativeButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                            databaseHelper.deleteTask(itemID, itemTask)
                            populateListView()
                        })
                        .setNeutralButton("Change status", DialogInterface.OnClickListener { dialog, which ->
                            if(itemStatus == 1){
                                databaseHelper.updateStatus(0, itemID, itemTask)
                                populateListView()
                            }else{
                                databaseHelper.updateStatus(1, itemID, itemTask)
                                populateListView()
                            }
                        })
                        .create()
                dialog.show()
            } else {
                toastMessage("No ID associated with that task")
            }
        }

        listViewDone.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itemTask = adapterView.getItemAtPosition(i).toString()
            val itemIdCursor = databaseHelper.getItemID(itemTask)
            val itemStatusCursor = databaseHelper.getItemStatus(itemTask)
            var itemStatus = 1
            while(itemStatusCursor.moveToNext()){
                itemStatus = itemStatusCursor.getInt(0)
            }
            var itemID = -1
            while (itemIdCursor.moveToNext()) {
                itemID = itemIdCursor.getInt(0)
            }
            if (itemID > -1) {
                val taskEditText = EditText(this)
                taskEditText.setText(itemTask)
                val dialog = AlertDialog.Builder(this)
                        .setTitle("Edit Task")
                        .setView(taskEditText)
                        .setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
                            val newTask = taskEditText.text.toString()
                            if (newTask != "") {
                                databaseHelper.updateTask(newTask, itemID, itemTask)
                                populateListView()
                            } else {
                                toastMessage("You must enter a task")
                            }
                        })
                        .setNegativeButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                            databaseHelper.deleteTask(itemID, itemTask)
                            populateListView()
                        })
                        .setNeutralButton("Change status", DialogInterface.OnClickListener { dialog, which ->
                            if(itemStatus == 1){
                                databaseHelper.updateStatus(0, itemID, itemTask)
                                populateListView()
                            }else{
                                databaseHelper.updateStatus(1, itemID, itemTask)
                                populateListView()
                            }
                        })
                        .create()
                dialog.show()
            } else {
                toastMessage("No ID associated with that task")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater!!.inflate(R.menu.add_data_menu_button, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //intent = Intent(applicationContext, AddDataActivity::class.java)
        //startActivity(intent)
        //return true
        if (item != null) {
            when (item.itemId) {
                R.id.plus_btn -> {
                    val taskEditText = EditText(this)
                    val dialog = AlertDialog.Builder(this)
                            .setTitle("Add New Task")
                            .setMessage("What do you want to do next?")
                            .setView(taskEditText)
                            .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                                var newEntry = taskEditText.text.toString()
                                if (taskEditText.length() != 0) {
                                    addData(newEntry)
                                    populateListView()
                                } else {
                                    toastMessage("You must put something in the text field!")
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                    dialog.show()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addData(newEntry: String) {
        val insertData = databaseHelper.addData(newEntry)

        if (insertData) {
            toastMessage("Data Successfully Inserted!")
        } else {
            toastMessage("Something went wrong")
        }
    }

    private fun setDynamicHeight(listView: ListView) {
        val adapter = listView.adapter ?: return
        var height = 0
        val desiredWidth = MeasureSpec.makeMeasureSpec(listView.width, MeasureSpec.UNSPECIFIED)
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, listView)
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            height += listItem.measuredHeight
        }
        val layoutParams = listView.layoutParams
        layoutParams.height = height + listView.dividerHeight * (adapter.count - 1)
        listView.layoutParams = layoutParams
        listView.requestLayout()
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
