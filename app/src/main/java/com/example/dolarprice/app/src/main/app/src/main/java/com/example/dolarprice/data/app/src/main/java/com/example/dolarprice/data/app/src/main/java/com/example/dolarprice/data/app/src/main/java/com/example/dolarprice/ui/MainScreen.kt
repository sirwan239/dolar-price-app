package com.example.dolarprice.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dolarprice.data.Product
import com.example.dolarprice.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val products by viewModel.allProducts.collectAsState(initial = emptyList())
    val currentDollarRate by viewModel.currentDollarRate.collectAsState()

    var productName by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var purchaseDollarRate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("قیمت‌‌یار دلار") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // ورود نرخ روز دلار
            OutlinedTextField(
                value = if (currentDollarRate == 0L) "" else currentDollarRate.toString(),
                onValueChange = { viewModel.updateCurrentDollarRate(it.toLongOrNull() ?: 0L) },
                label = { Text("نرخ امروز دلار (تومان)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("افزایش کالای جدید", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("نام کالا") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    label = { Text("قیمت خرید (تومان)") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = purchaseDollarRate,
                    onValueChange = { purchaseDollarRate = it },
                    label = { Text("نرخ دلار هنگام خرید") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val price = purchasePrice.toLongOrNull() ?: 0L
                    val rate = purchaseDollarRate.toLongOrNull() ?: 0L
                    if (productName.isNotEmpty() && price > 0 && rate > 0) {
                        viewModel.addProduct(productName, price, rate)
                        productName = ""
                        purchasePrice = ""
                        purchaseDollarRate = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ثبت کالا")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("لیست کالاها", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        currentDollarRate = currentDollarRate,
                        onDelete = { viewModel.deleteProduct(product) },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    currentDollarRate: Long,
    onDelete: () -> Unit,
    viewModel: MainViewModel
) {
    val newPrice = viewModel.calculateNewPrice(product, currentDollarRate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "قیمت خرید: ${product.purchasePriceToman} تومان (دلار ${product.dollarRateAtPurchase})")
                Text(
                    text = "قیمت پیشنهادی امروز: $newPrice تومان",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف")
            }
        }
    }
}
