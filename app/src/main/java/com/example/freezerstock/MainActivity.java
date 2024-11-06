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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView itemListView;
    private Button addButton, editButton, deleteButton;
    private FirebaseFirestore db;
    private List<FreezerItem> itemList;
    private FreezerItemAdapter adapter;
    private FreezerItem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        itemListView = findViewById(R.id.itemListView);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

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

        addButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddItemActivity.class)));

        editButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("itemId", selectedItem.getId());
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                confirmDelete(selectedItem);
            }
        });
    }

    private void loadItems() {
        db.collection("freezer_items").get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        db.collection("freezer_items").document(item.getId()).delete().addOnSuccessListener(aVoid -> {
            itemList.remove(item);
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show());
    }
}