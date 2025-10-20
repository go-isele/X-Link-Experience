package com.lanotech.xpost.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHiddenSheetState(): SheetState {
    return rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
}
