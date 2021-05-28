package sandeep.kumar.imagesearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import sandeep.kumar.imagesearch.data.UnsplashPhoto
import sandeep.kumar.imagesearch.data.api.UnsplashApi
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    //this function related to start api request and turn api request to page
    //lodeResult take first parameter in Int and another that we need to fill the page
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        //we need to know what page we are currently on.
        // The params store page initially it is null
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX

        return try {
            //create api call
            val response = unsplashApi.searchPhotos(query, position, params.loadSize)

            val photos = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,

                nextKey = if (photos.isEmpty()) null else position + 1
            )
            //No internet connection
        } catch (exception: IOException) {
            LoadResult.Error(exception)
            //something wrong with sever
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition
    }
}