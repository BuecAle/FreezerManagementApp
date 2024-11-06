package com.example.freezerstock;

// EditItemDialog.java
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditItemDialog {
    private final Context context;
    private final FirebaseFirestore db;
    private final FreezerItem item;
    private final Runnable onItemUpdated;  // Callback for refreshing the list in MainActivity

    public EditItemDialog(Context context, FirebaseFirestore db, FreezerItem item, Runnable onItemUpdated) {
        this.context = context;
        this.db = db;
        this.item = item;
        this.onItemUpdated = onItemUpdated;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);
        builder.setView(dialogView);

        EditText quantityEditText = dialogView.findViewById(R.id.editQuantityEditText);
        Spinner unitSpinner = dialogView.findViewById(R.id.editUnitSpinner);

        quantityEditText.setText(String.valueOf(item.getQuantity()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.unit_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(item.getUnit());
        unitSpinner.setSelection(spinnerPosition);

        builder.setTitle("Edit Item")
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        int newQuantity = Integer.parseInt(quantityEditText.getText().toString());
                        if (newQuantity <= 0) {
                            Toast.makeText(context, "Quantity must be greater than zero.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String newUnit = unitSpinner.getSelectedItem().toString();

                        item.setQuantity(newQuantity);
                        item.setUnit(newUnit);

                        // Update item in Firebase
                        db.collection("freezer_items").document(item.getId())
                                .set(item.toMap())  // Assuming FreezerItem has a toMap() method for Firebase
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Item updated successfully", Toast.LENGTH_SHORT).show();
                                    onItemUpdated.run();  // Refresh the list in MainActivity
                                })
                                .addOnFailureListener(e -> Log.e("EditItemDialog", "Error updating item", e));
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Invalid quantity input", Toast.LENGTH_SHORT).show();
                        Log.e("EditItemDialog", "Invalid quantity input", e);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
