package com.example.aistudy.ui.viewmodels

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.aistudy.data.CategoryDao
import com.example.aistudy.data.TranscriptDao
import com.example.aistudy.data.models.ARModel
import com.example.aistudy.data.models.Category
import com.example.aistudy.data.models.Note
import com.example.aistudy.data.models.Transcript
import com.example.aistudy.data.notecontent.ContentItem
import com.example.aistudy.data.notecontent.ContentSerializers
import com.example.aistudy.data.repositories.MicrosoftSpeechRepository
import com.example.aistudy.data.repositories.NotesRepository
import com.example.aistudy.service.NotificationService
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.SearchAppBarState
import com.example.aistudy.worker.NoteReminderWorker
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max

    /**
     * The SharedViewModel classs uses Hilt for dependency injection.
     * It manages the application state, user interactions and database operations
     * This class interacts with the repositories, DAOs and the WorkManafer for asynchronous tasks like reminders
     */

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val application: Application,
    private val notesRepository: NotesRepository,
    private val workManager: WorkManager,
    private val categoryDao: CategoryDao,
    private val transcriptDao: TranscriptDao
) : ViewModel() {

    private val _shouldShowSplashScreen = MutableStateFlow<Boolean>(true)
    val shouldShowSplashScreen = _shouldShowSplashScreen.asStateFlow()

    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val categoryId: MutableState<Int> = mutableStateOf(0)
    val reminderDateTime: MutableState<Date?> = mutableStateOf(null)
    private val workerRequestId: MutableState<UUID?> = mutableStateOf(null)
    private val createdAt: MutableState<Date> = mutableStateOf(Date())
    private val updatedAt: MutableState<Date> = mutableStateOf(Date())

    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)

    private val _selectedNote: MutableStateFlow<Note?> = MutableStateFlow(null)
    val selectedNote: MutableStateFlow<Note?> = _selectedNote

    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

    private val _allNotes = MutableStateFlow<PagingData<Note>>(PagingData.empty())
    val allNotes: StateFlow<PagingData<Note>> = _allNotes

    private val _searchedNotes = MutableStateFlow<PagingData<Note>>(PagingData.empty())
    val searchedNotes: StateFlow<PagingData<Note>> = _searchedNotes


    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Add a StateFlow for filtered notes based on category
    private val _filteredNotes = MutableStateFlow<PagingData<Note>>(PagingData.empty())
    val filteredNotes: StateFlow<PagingData<Note>> = _filteredNotes.asStateFlow()

    // StateFlow to hold the ID of the selected category for filtering
    private val _categoryFilter = MutableStateFlow<Int?>(null)
    val categoryFilter: StateFlow<Int?> = _categoryFilter.asStateFlow()



    // Function to update the category filter
    fun setCategoryFilter(categoryId: Int?) {
        Log.d("ViewModel", "Category Filter set to: $categoryId")
        _categoryFilter.value = categoryId
        categoryId?.let { filterNotesByCategory(it) } ?: run { resetNotesFilter() }
    }

    fun filterNotesByCategory(categoryId: Int?) {
        Log.d("ViewModel", "Filtering notes by category ID: $categoryId")
        viewModelScope.launch {
            try {
                // Check if categoryId is null or not
                val flow: Flow<PagingData<Note>> = if (categoryId != null) {
                    notesRepository.filterNotesByCategory(categoryId)
                } else {
                    notesRepository.getAllNotes()
                }

                flow.cachedIn(viewModelScope).collect { pagingData ->
                    _filteredNotes.value = pagingData
                }
            } catch (e: Exception) {
                _filteredNotes.value = PagingData.empty()
            }
        }
    }



    private fun resetNotesFilter() {
        viewModelScope.launch {
            try {
                notesRepository.getAllNotes().cachedIn(viewModelScope).collect { pagingData ->
                    _filteredNotes.value = pagingData
                }
            } catch (e: Exception) {
                _filteredNotes.value = PagingData.empty()
            }
        }
    }


    val categoryIDforNoteContent: Int?
        get() = selectedNote.value?.categoryId

    // Add a way to get the content items of the selected note
    val selectedNoteContentItems: StateFlow<List<ContentItem>> = selectedNote
        .map { note ->
            note?.contentItemsJson?.let { json ->
                try {
                    ContentSerializers.json.decodeFromString<List<ContentItem>>(json)
                } catch (e: SerializationException) {
                    emptyList<ContentItem>()
                }
            } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            delay(2000)
            _shouldShowSplashScreen.value = false
        }
        getAllNotes()
    }

    init {
        // Fetch categories from database
        viewModelScope.launch {
            categoryDao.getAllCategories().collect { categoriesList ->
                _categories.value = categoriesList
            }
        }
    }

    private fun getAllNotes() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                notesRepository.getAllNotes().cachedIn(viewModelScope).collect { pagingData ->
                    Log.d("Notes", pagingData.toString())
                    _allNotes.value = pagingData
                }
            }
        } catch (e: Exception) {
            Log.d("Notes", e.toString())
            _allNotes.value = PagingData.empty()
        }
    }

    fun searchNotes() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                notesRepository.searchNotes(searchQuery = "%${searchTextState.value}%")
                    .cachedIn(viewModelScope).collect { pagingData ->
                        _searchedNotes.value = pagingData
                    }
            }
        } catch (e: Exception) {
            _searchedNotes.value = PagingData.empty()
        }
        searchAppBarState.value = SearchAppBarState.TRIGGERED
    }

    fun getSelectedNote(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.getSelectedNote(noteId = noteId).collect { note ->
                _selectedNote.value = note
            }
        }
    }

    fun updateNoteFields(selectedNote: Note?) {
        if (selectedNote != null) {
            id.value = selectedNote.id
            title.value = selectedNote.title
            description.value = selectedNote.description
            categoryId.value = selectedNote.categoryId
            reminderDateTime.value = selectedNote.reminderDateTime
            workerRequestId.value = selectedNote.workerRequestId
            createdAt.value = selectedNote.createdAt
            updatedAt.value = selectedNote.updatedAt
        } else {
            id.value = 0
            title.value = ""
            description.value = ""
            categoryId.value = 0
            reminderDateTime.value = null
            workerRequestId.value = null
            createdAt.value = Date()
            updatedAt.value = Date()
        }
    }

    fun validateNoteFields(): Boolean {
        return title.value.isNotEmpty()
//                && description.value.isNotEmpty()
    }

    fun handleDatabaseAction(action: Action) {
        when (action) {
            Action.ADD -> {
                addNote()
            }
            Action.UPDATE -> {
                updateNote()
            }
            Action.DELETE -> {
                deleteNote()
            }
            Action.DELETE_ALL -> {
                deleteAllNotes()
            }
            Action.UNDO -> {
                addNote()
            }
            else -> {
            }
        }
    }

    private fun addNote() {
        viewModelScope.launch(Dispatchers.IO) {
            createOrUpdateWorkerForNotesReminder()
            val note = Note(
                title = title.value,
                description = description.value,
                reminderDateTime = reminderDateTime.value,
                workerRequestId = workerRequestId.value,
                createdAt = Calendar.getInstance().time,
                updatedAt = Calendar.getInstance().time,
                categoryId = categoryId.value
            )

            notesRepository.addNote(note)
        }
    }

    private fun updateNote() {
        viewModelScope.launch(Dispatchers.IO) {
            createOrUpdateWorkerForNotesReminder()

            // Fetch the currently selected note's contentItemsJson
            val currentContentItemsJson = selectedNote.value?.contentItemsJson.orEmpty()

            // Optionally decode and modify content items here if needed, similar to addTranscript()
            // For example, if you need to add another content item during update, you could decode the JSON,
            // modify the list, and then re-encode it. This step is optional and depends on your use case.

            val note = Note(
                id = id.value,
                title = title.value,
                description = description.value,
                reminderDateTime = reminderDateTime.value,
                workerRequestId = workerRequestId.value,
                createdAt = createdAt.value,
                updatedAt = Date(),
                categoryId = categoryId.value,
                contentItemsJson = currentContentItemsJson // Use the fetched or modified contentItemsJson
            )

            notesRepository.updateNote(note)
        }
    }


    private fun deleteNote() {
        viewModelScope.launch(Dispatchers.IO) {
            if (workerRequestId.value != null) {
                cancelNoteReminderWorkerById(workerRequestId.value!!)
            }
            val note = Note(
                id = id.value,
                title = title.value,
                description = description.value,
                reminderDateTime = reminderDateTime.value,
                workerRequestId = workerRequestId.value,
                createdAt = createdAt.value,
                updatedAt = updatedAt.value,
                categoryId = categoryId.value
            )

            notesRepository.deleteNote(note)
        }
    }

    private fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            cancelAllNoteReminderWorkers()
            notesRepository.deleteAllNotes()
        }
    }


    private fun cancelAllNoteReminderWorkers() {
        workManager.cancelAllWork()
    }

    private fun cancelNoteReminderWorkerById(id: UUID) {
        workManager.cancelWorkById(workerRequestId.value!!)
    }

    private fun createOrUpdateWorkerForNotesReminder() {
        if (reminderDateTime.value != null) {

            // if worker is already Scheduled then cancel it
            if (workerRequestId.value != null) {
                cancelNoteReminderWorkerById(workerRequestId.value!!)
            }

            val currentDateTime = Date()

            val delayInSeconds =
                (reminderDateTime.value!!.time / 1000L) - (currentDateTime.time / 1000L)

            createWorkRequest(message = title.value, timeDelayInSeconds = delayInSeconds)
        }
    }

    private fun createWorkRequest(message: String, timeDelayInSeconds: Long) {
        val myWorkRequest = OneTimeWorkRequestBuilder<NoteReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "title" to "Reminder",
                    "message" to message,
                )
            )
            .build()

        workerRequestId.value = myWorkRequest.id

        workManager.enqueue(myWorkRequest)
    }

    // Function to add a new category
    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            val newCategory = Category(name = categoryName)
            categoryDao.addCategory(newCategory)
        }
    }

    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
//            val newCategory = Category(name = categoryId)
            categoryDao.deleteCategory(categoryId)
        }
    }

    suspend fun fetchCategoryName(catId: Int): String {
        return categoryDao.getCategoryNameById(catId) ?: ""
    }


    var textInput by mutableStateOf("")

    fun addTranscript(selectedNote: Note?, transcriptId: Int) {
        viewModelScope.launch {
            selectedNote?.let { note ->
                val contentItems: MutableList<ContentItem> = note.contentItemsJson.takeIf { it.isNotBlank() }?.let { json ->
                    try {
                        ContentSerializers.json.decodeFromString(json)
                    } catch (e: SerializationException) {
                        mutableListOf()
                    }
                } ?: mutableListOf()

                // Fetch the title asynchronously and wait for the result
                val transcriptTitle = getTranscriptTitleById(transcriptId)

                val newTranscriptContent = ContentItem.TranscriptContent(id = transcriptId, title = transcriptTitle)
                contentItems.add(newTranscriptContent)

                val updatedContentItemsJson = ContentSerializers.json.encodeToString(contentItems)
                val updatedNote = note.copy(contentItemsJson = updatedContentItemsJson, updatedAt = Date())

                // Now this entire block is already within a coroutine scope
                notesRepository.updateNote(updatedNote)
            }
        }
    }

    fun removeTranscript(transcriptId: Int, selectedNote: Note?) {
        selectedNote?.let { note ->
            // Safely decode the JSON string into a List<ContentItem>, handling empty or malformed JSON.
            val contentItems = if (note.contentItemsJson.isNotBlank()) {
                try {
                    ContentSerializers.json.decodeFromString<List<ContentItem>>(note.contentItemsJson)
                } catch (e: SerializationException) {
                    mutableListOf<ContentItem>() // Return an empty mutable list if JSON is invalid.
                }
            } else {
                mutableListOf<ContentItem>() // Return an empty mutable list if JSON is empty.
            }.toMutableList()

            // Remove all TranscriptContent items that match the given transcriptId.
            contentItems.removeAll { item ->
                item is ContentItem.TranscriptContent && item.id == transcriptId
            }

            // Encode the updated list back into a JSON string.
            val updatedContentItemsJson = ContentSerializers.json.encodeToString(contentItems)
            // Create a new note instance with the updated contentItemsJson.
            val updatedNote = note.copy(contentItemsJson = updatedContentItemsJson, updatedAt = Date())

            // Launch a coroutine to update the note in the repository.
            viewModelScope.launch {
                notesRepository.updateNote(updatedNote)
            }
        }
    }



    suspend fun getTranscriptTitleById(transcriptId: Int): String {
        // This will now wait for the database call to complete and return the title
        return transcriptDao.getTranscriptById(transcriptId)?.name ?: "New Transcript"
    }

    fun getLiveTranscriptTitleById(transcriptId: Int): Flow<String> {
        // This method should return a Flow that emits the current title and any updates
        return transcriptDao.getTranscriptTitleFlowById(transcriptId)
    }



    fun addPhoto(selectedNote: Note?, photoUri: String) {
        selectedNote?.let { note ->
            // Safely decode the JSON string, handling empty or malformed JSON
            val contentItems = if (note.contentItemsJson.isNotBlank()) {
                try {
                    ContentSerializers.json.decodeFromString<List<ContentItem>>(note.contentItemsJson)
                } catch (e: SerializationException) {
                    mutableListOf<ContentItem>() // Return an empty mutable list if JSON is invalid
                }
            } else {
                mutableListOf<ContentItem>() // Return an empty mutable list if JSON is empty
            }.toMutableList()

            // Optionally handle text input before adding the photo content
            if (textInput.isNotBlank()) {
                val newTextContent = ContentItem.TextContent(text = textInput)
                contentItems.add(newTextContent)
                textInput = ""
            }

            val newPhotoContent = ContentItem.PhotoContent(uri = photoUri)
            contentItems.add(newPhotoContent)

            val updatedContentItemsJson = ContentSerializers.json.encodeToString(contentItems)
            val updatedNote = note.copy(contentItemsJson = updatedContentItemsJson, updatedAt = Date())

            viewModelScope.launch {
                notesRepository.updateNote(updatedNote)
            }
        }
    }

    fun removePhoto(photoUri: String, selectedNote: Note?) {
        selectedNote?.let { note ->
            // Safely decode the JSON string, handling empty or malformed JSON
            val contentItems = if (note.contentItemsJson.isNotBlank()) {
                try {
                    ContentSerializers.json.decodeFromString<List<ContentItem>>(note.contentItemsJson)
                } catch (e: SerializationException) {
                    mutableListOf<ContentItem>() // Return an empty mutable list if JSON is invalid
                }
            } else {
                mutableListOf<ContentItem>() // Return an empty mutable list if JSON is empty
            }.toMutableList()

            contentItems.removeAll { item ->
                item is ContentItem.PhotoContent && item.uri == photoUri
            }

            val updatedContentItemsJson = ContentSerializers.json.encodeToString(contentItems)
            val updatedNote = note.copy(contentItemsJson = updatedContentItemsJson , updatedAt = Date())

            viewModelScope.launch {
                notesRepository.updateNote(updatedNote)
            }
        }
    }

    // Transcript stuff
    val TranscriptTitle: MutableState<String> = mutableStateOf("")
    val TranscriptText: MutableStateFlow<String> = MutableStateFlow("")

    private val msSpeechRepository: MicrosoftSpeechRepository by lazy {
        MicrosoftSpeechRepository()
    }

    fun setTranscriptFilePath(context: Context, uri: Uri, onTranscriptCreated: (Int) -> Unit) {
        Toast.makeText(context, "We're working on your transcript! It'll be ready in just a minute or two.", Toast.LENGTH_LONG).show()
        viewModelScope.launch(Dispatchers.IO) {
            val file = copyUriContentToFile(context, uri)
            val transcriptText = msSpeechRepository.convertAudioToText(file.absolutePath)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val transcriptName = "Transcript - ${dateFormat.format(Date())}"
            val newTranscript = Transcript(name = transcriptName, transcript = transcriptText, filepath = file.absolutePath)

            val newId = transcriptDao.addTranscript(newTranscript)
            // add toast here
            withContext(Dispatchers.Main) {
                onTranscriptCreated(newId.toInt()) // Assuming addTranscript returns a Long ID

                // Show the notification for transcript completion
                // Ensure you're passing `true` for `useTranscriptChannel` to use the transcript notification channel
                NotificationService.showNotification(
                    context = context,
                    title = "Transcript Ready",
                    message = "Your transcript '$transcriptName' is ready for viewing.",
                    useTranscriptChannel = true // Specify to use the transcript channel
                )
            }
        }
    }

    fun loadTranscriptById(transcriptId: Int) {
        viewModelScope.launch {
            transcriptDao.getTranscriptById(transcriptId)?.let { transcript ->
                TranscriptTitle.value = transcript.name
                TranscriptText.value = transcript.transcript
                // If you need to initialize the player with the file path
                initializePlayer(transcript.filepath)
            }
        }
    }

    private fun copyUriContentToFile(context: Context, uri: Uri): File {
        val file = File(context.filesDir, "audio_transcript.wav")
        // Create a new file in your app's private storage
        // Get an input stream from the content resolver
        val inputStream = context.contentResolver.openInputStream(uri)
        // Write the input stream to the file
        FileOutputStream(file).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return file
    }

    private var mediaPlayer: MediaPlayer? = null
    private val progressUpdateHandler = Handler(Looper.getMainLooper())
    private var isPrepared = false

    val currentPosition = MutableStateFlow(0) // Current position in milliseconds
    val totalDuration = MutableStateFlow(0) // Total duration in milliseconds

    fun initializePlayer(filePath: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepareAsync() // Prepare asynchronously to not block the main thread
            setOnPreparedListener { mp ->
                isPrepared = true
                totalDuration.value = mp.duration // Set the total duration here
                startProgressUpdater()
            }
            setOnCompletionListener { mp ->
                // Handle completion, e.g., reset the UI to the start
            }
        }
    }

    fun updateTranscriptTitle(transcriptId: Int, newTitle: String) {
        viewModelScope.launch {
            // Assuming you have a method in your DAO to update the title
            transcriptDao.updateTitleById(transcriptId, newTitle)
            // After updating in the database, also update the in-memory value if necessary
            TranscriptTitle.value = newTitle
        }
    }


    fun playPauseAudio() {
        mediaPlayer?.let {
            if (isPrepared) {
                if (it.isPlaying) {
                    it.pause()
                } else {
                    it.start()
                    startProgressUpdater()
                }
            }
        }
    }

    fun seekAudio(progress: Float) {
        if (isPrepared) {
            val seekToPosition = (progress * totalDuration.value).toInt()
            mediaPlayer?.seekTo(seekToPosition)
//            currentPosition.value = seekToPosition // Update immediately for responsiveness
        }
    }


    private fun startProgressUpdater() {
        val updateTask = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        currentPosition.value = it.currentPosition
                        progressUpdateHandler.postDelayed(this, 1000) // Update every second
                    }
                }
            }
        }
        progressUpdateHandler.post(updateTask)
    }


    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun skipBackward() {
        mediaPlayer?.let {
            if (isPrepared) {
                // Calculate new position
                val newPosition = it.currentPosition - 10000 // 10 seconds in milliseconds
                it.seekTo(max(0, newPosition)) // Ensure position does not go below 0
            }
        }
    }

    fun skipForward() {
        mediaPlayer?.let {
            if (isPrepared) {
                // Calculate new position
                val newPosition = it.currentPosition + 10000 // 10 seconds in milliseconds
                if (newPosition < it.duration) {
                    it.seekTo(newPosition) // Ensure new position does not exceed duration
                } else {
                    it.seekTo(it.duration) // Go to end if over duration
                }
            }
        }
    }

    // for Image2Text
    fun recognizeTextFromImage(
        context: Context,
        imageUri: Uri?,
        onTextRecognized: (String) -> Unit
    ) {
        // Check if the URI is null and return early if it is
        if (imageUri == null) {
            Log.e("Image2Text", "Error: imageUri is null")
            return
        }

        // Now that we've checked imageUri isn't null, we can safely use it
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onTextRecognized(visionText.text)
                }
                .addOnFailureListener { e ->
                    Log.e("Image2Text", "Error recognizing text", e)
                }
        } catch (e: Exception) {
            Log.e("Image2Text", "Error loading image from URI", e)
        }
    }

    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).also {
            // Here you might want to save the path of the file for later
        }
    }

    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        modelInstances: MutableList<ModelInstance>,
        anchor: Anchor,
        ARModel: ARModel,
        MaxModelInstances: Int
    ): AnchorNode {
        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
        val modelNode = ModelNode(
            modelInstance = modelInstances.apply {
                this += modelLoader.createInstancedModel(ARModel.modelPath, MaxModelInstances)
            }.removeLast(),

            // Scale to fit depending on which Model it is.

            scaleToUnits = ARModel.distance

        ).apply {
            // Model Node needs to be editable for independent rotation from the anchor rotation
            isEditable = false
        }
        val boundingBoxNode = CubeNode(
            engine,
            size = modelNode.extents,
            center = modelNode.center,
            materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
        ).apply {
            isVisible = false
        }
        modelNode.addChildNode(boundingBoxNode)
        anchorNode.addChildNode(modelNode)

        listOf(modelNode, anchorNode).forEach {
            it.onEditingChanged = { editingTransforms ->
                boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
            }
        }
        return anchorNode
    }

}