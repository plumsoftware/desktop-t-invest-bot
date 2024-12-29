package ru.plumsoftware.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.plumsoftware.ui.theme.Space
import ru.tinkoff.piapi.core.models.Portfolio

@Composable
fun PortfolioComposable(portfolio: Portfolio?) {

    if (portfolio != null) {
        val total = portfolio.totalAmountPortfolio.value.toLong()
        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                space = Space.small,
                alignment = Alignment.Top
            ),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = Space.large,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                with(portfolio.totalAmountPortfolio) {
                    val percent = if (total != 0L) value.toLong() / total * 100 else 0
                    Text(
                        text = "Общая сумма портфеля ${value.toLong()} $currency ($percent%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    modifier = Modifier
                        .size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent
                    ),
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = "Развернуть портфель",
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }
            }
            if (expanded) {
                with(portfolio.totalAmountBonds) {
                    val percent = if (total != 0L) value.toLong() / total * 100 else 0
                    Text(
                        text = "Общая сумма облигаций ${value.toLong()} $currency ($percent%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                with(portfolio.totalAmountEtfs) {
                    val percent = if (total != 0L) value.toLong() / total * 100 else 0
                    Text(
                        text = "Общая сумма фондов ${value.toLong()} $currency ($percent%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                with(portfolio.totalAmountFutures) {
                    val percent = if (total != 0L) value.toLong() / total * 100 else 0
                    Text(
                        text = "Общая сумма фьючерсов ${value.toLong()} $currency ($percent%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                with(portfolio.totalAmountShares) {
                    val percent = if (total != 0L) value.toLong() / total * 100 else 0
                    Text(
                        text = "Общая сумма акций ${value.toLong()} $currency ($percent%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                with(portfolio.totalAmountCurrencies) {
                    val percent = if (total != 0L) value.toLong() / total * 100 else 0
                    Text(
                        text = "Общая сумма валют ${value.toLong()} $currency ($percent%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}