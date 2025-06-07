package com.github.imhammer.saviotasks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.imhammer.saviotasks.ui.theme.MyColors

@Composable
fun InputField(
    title: String,
    titleSize: TextUnit = 20.sp,
    titleColor: Color = Color.Gray,
    placeHolder: String,
    placeHolderColor: Color = Color.White,
    placeHolderSize: TextUnit = TextUnit.Unspecified,
    minLines: Int = 1,
    maxLines: Int,
    onValueChange: (String) -> Unit,
    value: String
) {
    Column (
        modifier = Modifier
            .padding(bottom = 20.dp)
    ){
        Text(title, modifier = Modifier.padding(bottom = 10.dp), style = MaterialTheme.typography.titleMedium, fontSize = titleSize, color = titleColor)
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            maxLines = maxLines,
            onValueChange = onValueChange,
            value = value,
            placeholder = {
                Text(placeHolder, color = placeHolderColor, fontSize = placeHolderSize)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MyColors.DarkGray,
                unfocusedContainerColor = MyColors.DarkGray,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}