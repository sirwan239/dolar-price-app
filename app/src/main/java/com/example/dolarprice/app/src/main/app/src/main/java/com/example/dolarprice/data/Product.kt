package com.example.dolarprice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val purchasePriceToman: Long,
    val dollarRateAtPurchase: Long,
    val category: String = "عمومی"
)
