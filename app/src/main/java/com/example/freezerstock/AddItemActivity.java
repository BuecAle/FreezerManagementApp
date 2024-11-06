package com.example.freezerstock;

// AddItemActivity.java
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddItemActivity extends AppCompatActivity {
    private EditText nameEditText, quantityEditText;
    private Spinner unitSpinner;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        db = FirebaseFirestore.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        unitSpinner = findViewById(R.id.unitSpinner);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        // Populate the unit spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.unit_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveItem());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveItem() {
        String name = nameEditText.getText().toString();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
            return;
        }
        String unit = unitSpinner.getSelectedItem().toString();

        // Generate a unique ID for the new item
        String id = db.collection("freezer_items").document().getId();
        FreezerItem newItem = new FreezerItem(id, name, quantity, unit);

        db.collection("freezer_items").document(id).set(newItem)
                .addOnSuccessListener(aVoid -> {
                Toast.makeText(AddItemActivity.this, "Item saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
        })
        .addOnFailureListener(e -> {
            Log.e("AddItemActivity", "Error adding item", e);
            Toast.makeText(AddItemActivity.this, "Failed to save item", Toast.LENGTH_SHORT).show();
        });
    }
}