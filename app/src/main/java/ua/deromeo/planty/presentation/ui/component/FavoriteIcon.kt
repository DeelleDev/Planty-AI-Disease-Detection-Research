package ua.deromeo.planty.presentation.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.deromeo.planty.R
import ua.deromeo.planty.presentation.viewmodel.FavoritesViewModel

@Composable
fun FavoriteIcon(
    viewModel: FavoritesViewModel, id: Long, type: String, context: Context = LocalContext.current
) {
    val isFavorite = viewModel.isFavorite

    Image(
        painter = painterResource(
            if (isFavorite) R.drawable.bookmark_filled else R.drawable.bookmark_empty
        ),
        contentDescription = "Saved Icon",
        modifier = Modifier
            .size(42.dp)
            .padding(8.dp)
            .clickable {
                viewModel.toggleFavorite(context, id, type)
            })
}