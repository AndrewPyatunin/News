package com.andreich.news.data

import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.repository.NewsRepository
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NewsRepositoryTest {

    lateinit var listNews: List<News>
    lateinit var favoriteNews: MutableList<News>

    var favorites = MutableStateFlow<List<News>>(emptyList())

    private val fakeNewsRepository = object : NewsRepository {

        override fun getNews(
            language: String?,
            country: String?,
            limit: Int
        ): Flow<List<News>> {
            return flowOf(listNews.filter { (language == it.language || language == null) && (country == it.sourceCountry || country == null) }.take(limit))
        }

        override fun getFavorites(): Flow<List<News>> {
            return favorites //flowOf(favoriteNews)
        }

        override fun searchNews(
            param: String,
            paramsFilter: ParamsFilter?
        ): Flow<List<News>> {
            return flowOf(listNews.filter {
                (it.title.contains(param) || it.description.contains(
                    param
                ) || it.content.contains(param, true)) &&
                        paramsFilter?.let { paramsFilter ->
                            (paramsFilter.language == it.language || paramsFilter.language == null) &&
                                    (paramsFilter.country == it.sourceCountry || paramsFilter.country == null) &&
                                    (paramsFilter.category == it.category || paramsFilter.category == null) && it.title.contains(
                                paramsFilter.location ?: ""
                            )
                        } ?: true
            })
        }

        override suspend fun updateNews(
            language: String,
            country: String
        ): RequestResult {
            return RequestResult.Success
        }

        override suspend fun updateSearchedNews(
            param: String,
            paramsFilter: ParamsFilter?
        ): RequestResult {
            return RequestResult.Failure.NoInternet("No internet")
        }

        override fun getNewsListByIds(ids: List<Int>): Flow<List<News>> {
            return flowOf(ids.map {
                listNews[it]
            })
        }

        override fun getSingleNews(id: Int): Flow<News> {
            return flowOf(listNews[id])
        }

        override suspend fun removeFromFavourites(newsId: Int) {
            favorites.update { it.filterNot {  it.id == newsId } }
        }

        override suspend fun addToFavourites(newsId: Int) {
            listNews.firstOrNull { it.id == newsId }?.let { news ->
                favorites.update { it+news }
            }
        }

        override suspend fun getNewsSuggestions(query: String): List<String> {
            return emptyList()
        }

    }

    @Before
    fun setUp() {
        listNews = (0..100).map { i ->
            News(
                id = i,
                author = "Author $i",
                title = "Title $i",
                description = "Description $i",
                imageUrl = "ImageUrl $i",
                url = "Url $i",
                content = "Content $i",
                publishedAt = "PublishedAt $i",
                category = "Category $i",
                sourceCountry = if (i<50) "ru" else "us",
                language = if (i<50) "ru" else "en"
            )
        }
        favoriteNews = mutableListOf()
    }

    @Test
    fun favoritesTest() = runTest {
        fakeNewsRepository.addToFavourites(0)
        fakeNewsRepository.addToFavourites(1)
        fakeNewsRepository.addToFavourites(2)
        val result = fakeNewsRepository.getFavorites().first()
        assertEquals(3, result.size)
        fakeNewsRepository.removeFromFavourites(2)
        val resultRemoved = fakeNewsRepository.getFavorites().first()
        assertFalse(resultRemoved.any { it.id == 2 })
        assertEquals(listOf(0, 1), resultRemoved.map { it.id })
    }

    @Test
    fun getNewTest() = runTest {
        val result = fakeNewsRepository.getNews("ru", "ru", 50).first()

        assertEquals(50, result.size)
        assertTrue(result.all { it.language == "ru" })
        assertTrue(result.all { it.sourceCountry == "ru" })
    }

    @Test
    fun getNewsWrongLanguageTest() = runTest {
        val result = fakeNewsRepository.getNews("fr", "en", 50).first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun searchTestWithNullParamFilter() = runTest {
        val result = fakeNewsRepository.searchNews("Title 10").first()
        assertEquals(2, result.size)
    }

    @Test
    fun searchTestWithParamsFilter() = runTest {
        val paramsFilter = ParamsFilter(country = "ru", language = "ru", category = "Category 10")
        val result = fakeNewsRepository.searchNews("Title 10", paramsFilter).first()
        assertEquals(1, result.size)
    }

    @Test
    fun searchTestWithPartlyParamsFilter() = runTest {
        val paramsFilter = ParamsFilter(country = "us", language = "en")
        val resultCheck = fakeNewsRepository.searchNews("Title", paramsFilter).first()
        assertEquals(51, resultCheck.size)
        val result = fakeNewsRepository.searchNews("Title 5", paramsFilter).first()
        assertEquals(10, result.size)
    }

    @Test
    fun searchWIthWrongQuery() = runTest {
        val result = fakeNewsRepository.searchNews("Query").single()
        assertEquals(0, result.size)
    }
}