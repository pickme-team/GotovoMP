package org.yaabelozerov.gotovomp.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.emptyList

data class Pageable<T>(
    val items: List<T>,
    val nextKey: Int?,
)

sealed interface PagerState<out T> {
    data object Initial : PagerState<Nothing>
    data object Loading : PagerState<Nothing>
    data class HasContent<T>(
        val content: List<T>,
        val hasMore: Boolean,
    ) : PagerState<T>

    data class Error(val error: DomainError) : PagerState<Nothing>
}

interface PagerSource<T> {
    suspend fun loadPage(page: Int, pageSize: Int): Pageable<T>
}

class Pager<T>(
    private val source: PagerSource<T>,
    private val pageSize: Int = 20,
    private val scope: CoroutineScope,
    private val initialPage: Int = 0,
) {
    private val _state = MutableStateFlow<PagerState<T>>(PagerState.Initial)
    val state: StateFlow<PagerState<T>> = _state.asStateFlow()

    private val mutex = Mutex()
    private var nextPageKey: Int? = initialPage

    fun refresh() {
        scope.launch {
            mutex.withLock {
                nextPageKey = initialPage
                _state.update { PagerState.Loading }
                loadPageInternal(isRefreshing = true)
            }
        }
    }

    fun loadNextPage() {
        scope.launch {
            mutex.withLock {
                if (_state.value is PagerState.Loading) return@launch
                loadPageInternal()
            }
        }
    }

    fun retry() {
        scope.launch {
            mutex.withLock {
                when (_state.value) {
                    is PagerState.Error -> loadPageInternal()
                    else -> return@launch
                }
            }
        }
    }

    private suspend fun loadPageInternal(isRefreshing: Boolean = false) {
        val pageToLoad = nextPageKey ?: return
        try {
            delay(3000)
            val result = source.loadPage(pageToLoad, pageSize)

            val currentItems = when (val current = _state.value) {
                is PagerState.HasContent -> if (isRefreshing) emptyList() else current.content
                else -> emptyList()
            }

            nextPageKey = result.nextKey
            _state.update {
                PagerState.HasContent(
                    content = currentItems + result.items,
                    hasMore = result.nextKey != null
                )
            }
        } catch (e: Throwable) {
            _state.update { PagerState.Error(e.toDomainError()) }
        }
    }
}