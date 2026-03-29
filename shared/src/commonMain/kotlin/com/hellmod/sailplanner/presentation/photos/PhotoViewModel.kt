package com.hellmod.sailplanner.presentation.photos

import com.hellmod.sailplanner.domain.model.GeoPoint
import com.hellmod.sailplanner.domain.model.TripCollage
import com.hellmod.sailplanner.domain.model.TripPhoto
import com.hellmod.sailplanner.domain.repository.PhotoRepository
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State
import kotlinx.coroutines.launch

// ── State ──────────────────────────────────────────────────────────────────
data class PhotoState(
    val isLoading: Boolean = true,
    val photos: List<TripPhoto> = emptyList(),
    val collage: TripCollage? = null,
    val isGeneratingCollage: Boolean = false,
    val selectedPhoto: TripPhoto? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface PhotoIntent : Intent {
    data class LoadPhotos(val tripId: String) : PhotoIntent
    data class SelectPhoto(val photo: TripPhoto) : PhotoIntent
    data object DeselectPhoto : PhotoIntent
    data class UploadPhoto(
        val tripId: String,
        val imageBytes: ByteArray,
        val location: GeoPoint?,
        val caption: String?
    ) : PhotoIntent
    data class DeletePhoto(val photoId: String) : PhotoIntent
    data class UpdateCaption(val photoId: String, val caption: String) : PhotoIntent
    data class GenerateCollage(val tripId: String) : PhotoIntent
    data class LoadCollage(val tripId: String) : PhotoIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface PhotoEffect : Effect {
    data class ShowError(val message: String) : PhotoEffect
    data class ShowSuccess(val message: String) : PhotoEffect
    data object OpenCamera : PhotoEffect
    data object OpenGallery : PhotoEffect
    data class ShowPhotoDetail(val photoId: String) : PhotoEffect
    data class ShowCollage(val tripId: String) : PhotoEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class PhotoViewModel(
    private val photoRepository: PhotoRepository
) : BaseViewModel<PhotoState, PhotoIntent, PhotoEffect>(PhotoState()) {

    override suspend fun handleIntent(intent: PhotoIntent) {
        when (intent) {
            is PhotoIntent.LoadPhotos -> loadPhotos(intent.tripId)
            is PhotoIntent.SelectPhoto -> updateState { copy(selectedPhoto = intent.photo) }
            PhotoIntent.DeselectPhoto -> updateState { copy(selectedPhoto = null) }
            is PhotoIntent.UploadPhoto -> uploadPhoto(intent.tripId, intent.imageBytes, intent.location, intent.caption)
            is PhotoIntent.DeletePhoto -> deletePhoto(intent.photoId)
            is PhotoIntent.UpdateCaption -> updateCaption(intent.photoId, intent.caption)
            is PhotoIntent.GenerateCollage -> generateCollage(intent.tripId)
            is PhotoIntent.LoadCollage -> loadCollage(intent.tripId)
        }
    }

    private fun loadPhotos(tripId: String) {
        viewModelScope.launch {
            photoRepository.observePhotos(tripId).collect { photos ->
                updateState { copy(isLoading = false, photos = photos) }
            }
        }
    }

    private suspend fun uploadPhoto(
        tripId: String,
        imageBytes: ByteArray,
        location: GeoPoint?,
        caption: String?
    ) {
        updateState { copy(isUploading = true) }
        photoRepository.uploadPhoto(tripId, imageBytes, location, caption)
            .onSuccess {
                updateState { copy(isUploading = false, uploadProgress = 0f) }
                emitEffect(PhotoEffect.ShowSuccess("Photo uploaded!"))
            }
            .onFailure { e ->
                updateState { copy(isUploading = false) }
                emitEffect(PhotoEffect.ShowError(e.message ?: "Upload failed"))
            }
    }

    private suspend fun deletePhoto(photoId: String) {
        photoRepository.deletePhoto(photoId)
            .onSuccess { emitEffect(PhotoEffect.ShowSuccess("Photo deleted")) }
            .onFailure { e -> emitEffect(PhotoEffect.ShowError(e.message ?: "Failed to delete")) }
    }

    private suspend fun updateCaption(photoId: String, caption: String) {
        val photo = state.value.photos.firstOrNull { it.id == photoId } ?: return
        photoRepository.updatePhoto(photo.copy(caption = caption))
            .onFailure { e -> emitEffect(PhotoEffect.ShowError(e.message ?: "Failed to update")) }
    }

    private suspend fun generateCollage(tripId: String) {
        updateState { copy(isGeneratingCollage = true) }
        photoRepository.generateCollage(tripId)
            .onSuccess { collage ->
                updateState { copy(isGeneratingCollage = false, collage = collage) }
                emitEffect(PhotoEffect.ShowCollage(tripId))
            }
            .onFailure { e ->
                updateState { copy(isGeneratingCollage = false) }
                emitEffect(PhotoEffect.ShowError(e.message ?: "Failed to generate collage"))
            }
    }

    private suspend fun loadCollage(tripId: String) {
        photoRepository.getCollage(tripId)
            .onSuccess { collage -> updateState { copy(collage = collage) } }
            .onFailure { e -> emitEffect(PhotoEffect.ShowError(e.message ?: "Failed to load collage")) }
    }
}
