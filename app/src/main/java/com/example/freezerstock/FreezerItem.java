package com.example.freezerstock;

import java.util.HashMap;
import java.util.Map;

// FreezerItem.java
public class FreezerItem {
    private String id;
    private String name;
    private int quantity;
    private String unit;

    public FreezerItem() {}

    public FreezerItem(String id, String name, int quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }

    // Method to convert the FreezerItem object to a Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("quantity", quantity);
        map.put("unit", unit);
        return map;
    }
}