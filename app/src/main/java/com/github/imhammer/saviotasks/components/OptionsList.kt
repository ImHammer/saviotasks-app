package com.github.imhammer.saviotasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.imhammer.saviotasks.ui.theme.MyColors

data class OptionItem<T>(val name: String, val color: Color, val data: T)

@Composable
fun <T> OptionsList(
    modifier: Modifier = Modifier,
    options: List<OptionItem<T>>,
    onSelected: (OptionItem<T>) -> Unit
) {
    Column (
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        options.forEach { option -> key(option) {
            RoundedButton(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .padding(vertical = 5.dp),
                title = option.name,
                color = option.color
            ) {
                onSelected(option)
            }
        }}
    }
}