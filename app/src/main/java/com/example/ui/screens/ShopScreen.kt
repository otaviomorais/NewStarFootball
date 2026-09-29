package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DefaultShopItems
import com.example.model.Player
import com.example.model.ShopCategory
import com.example.model.ShopItem
import com.example.ui.theme.BallBlack
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.StadiumGreenLight

@Composable
fun ShopScreen(
  player: Player,
  ownedItems: Set<String>,
  onBuyItem: (ShopItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf<ShopCategory?>(null) }
  val allItems = remember { DefaultShopItems.getAllItems() }

  val filteredItems = if (selectedCategory == null) {
    allItems
  } else {
    allItems.filter { it.category == selectedCategory }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Balance Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("shop_balance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "LOJA & ESTILO DE VIDA",
              color = ChampionGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text(
              text = "Compre equipamentos e artigos de luxo",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          }

          Surface(
            color = Color(0xFF142918),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(
              text = "R$ ${player.money}",
              color = ChampionGold,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // 2. Category Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
      ) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { selectedCategory = null },
            label = { Text("Todos", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = StadiumGreenLight,
              selectedLabelColor = Color.White
            )
          )
        }
        items(ShopCategory.values()) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat.title, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = StadiumGreenLight,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    // 3. Shop Items List
    items(filteredItems) { item ->
      val isOwned = ownedItems.contains(item.id)
      val canAfford = player.money >= item.price

      ShopItemRow(
        item = item,
        isOwned = isOwned,
        canAfford = canAfford,
        onBuy = { onBuyItem(item) }
      )
    }
  }
}

@Composable
private fun ShopItemRow(
  item: ShopItem,
  isOwned: Boolean,
  canAfford: Boolean,
  onBuy: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1B311E)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = getIconForName(item.iconName),
          contentDescription = null,
          tint = ChampionGold,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = item.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          if (!isOwned || item.isConsumable) {
            Text(
              text = "R$ ${item.price}",
              color = if (canAfford) ChampionGold else Color(0xFFEF5350),
              fontSize = 13.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = item.description,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isOwned && !item.isConsumable) {
          Surface(
            color = Color(0xFF1E3A24),
            shape = RoundedCornerShape(6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = StadiumGreenLight,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "ADQUIRIDO",
                color = StadiumGreenLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        } else {
          Button(
            onClick = onBuy,
            enabled = canAfford,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = ChampionGold,
              contentColor = BallBlack
            )
          ) {
            Text(
              text = if (item.isConsumable) "USAR AGORA" else "COMPRAR",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

private fun getIconForName(name: String): ImageVector {
  return when (name) {
    "LocalDrink" -> Icons.Default.LocalDrink
    "Bolt" -> Icons.Default.Bolt
    "Spa" -> Icons.Default.Spa
    "SportsSoccer" -> Icons.Default.SportsSoccer
    "Speed" -> Icons.Default.Speed
    "TrendingUp" -> Icons.Default.TrendingUp
    "Adjust" -> Icons.Default.Adjust
    "Star" -> Icons.Default.Star
    "PhoneAndroid" -> Icons.Default.PhoneAndroid
    "Watch" -> Icons.Default.Watch
    "DirectionsCar" -> Icons.Default.DirectionsCar
    "House" -> Icons.Default.House
    "FitnessCenter" -> Icons.Default.FitnessCenter
    "BusinessCenter" -> Icons.Default.BusinessCenter
    else -> Icons.Default.SportsSoccer
  }
}
