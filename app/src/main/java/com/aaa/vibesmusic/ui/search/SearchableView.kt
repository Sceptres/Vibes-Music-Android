package com.aaa.vibesmusic.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableView(
    searchableData: List<T>,
    placeholder: String,
    filter: (T, String) -> Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier, List<T>) -> Unit
) {
    var searchText: String by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onSearch = { focusManager.clearFocus() },
                    expanded = false,
                    onExpandedChange = {},
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        selectionColors = TextSelectionColors(
                            handleColor = Color.White,
                            backgroundColor = Color.LightGray
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            placeholder,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = { focusManager.clearFocus() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                searchText = ""
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = Color.White,
                            )
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {}

        Spacer(modifier = Modifier.height(10.dp))

        val filteredData: List<T> = if(searchText.isBlank())
            searchableData
        else
            searchableData.filter { filter(it, searchText) }

        content(Modifier.weight(1f), filteredData)
    }
}