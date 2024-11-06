package com.example.freezerstock;

// EditItemActivity.java
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditItemActivity extends AppCompatActivity {
    private EditText quantityEditText;
    private Spinner unitSpinner;
    private Button saveButton;
    private FirebaseFirestore db;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        db = FirebaseFirestore.getInstance();
        quantityEditText = findViewById(R.id.quantityEditText);
        unitSpinner = findViewById(R.id.unitSpinner);
        saveButton = findViewById(R.id.saveButton);

        itemId = getIntent().getStringExtra("itemId");

        saveButton.setOnClickListener(v -> updateItem());
    }

    private void updateItem() {
        int quantity = Integer.parseInt(quantityEditText.getText().toString());
        String unit = unitSpinner.getSelectedItem().toString();

        db.collection("freezer_items").document(itemId).update("quantity", quantity, "unit", unit).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditItemActivity.this, "Item updated", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(EditItemActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show());
    }
}