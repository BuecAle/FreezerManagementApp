package com.example.freezerstock;

// MainActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Constants
    private static final int ADD_ITEM_REQUEST_CODE = 1;

    private FirebaseFirestore db;
    private List<FreezerItem> itemList;
    private FreezerItemAdapter adapter;
    private FreezerItem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        ListView itemListView = findViewById(R.id.itemListView);
        Button addButton = findViewById(R.id.addButton);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        itemList = new ArrayList<>();
        adapter = new FreezerItemAdapter(this, itemList);
        itemListView.setAdapter(adapter);

        loadItems();

        // Set first item as selected by default
        adapter.setSelectedPosition(0);

        // Item click listener to highlight selected item
        itemListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            selectedItem = itemList.get(position);  // Update the selected item
            adapter.setSelectedPosition(position);  // Highlight the selected item
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
        });

        editButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                // Open edit dialog
                new EditItemDialog(this, db, selectedItem, this::loadItems).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                confirmDelete(selectedItem);
            }
        });
    }

    // Handle result from AddItemActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            loadItems();  // Refresh the item list after adding a new item
        }
    }

    private void loadItems() {
        db.collection(Constants.FREEZER_ITEMS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            itemList.clear();
            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                FreezerItem item = doc.toObject(FreezerItem.class);
                itemList.add(item);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show());
    }

    private void confirmDelete(FreezerItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem(item))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteItem(FreezerItem item) {
        db.collection(Constants.FREEZER_ITEMS).document(item.getId()).delete().addOnSuccessListener(aVoid -> {
            itemList.remove(item);
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show());
    }
}