package com.lightfeather.domain.model

/**
 * Represents paginated data with page information and content.
 * Used for both web-style pagination (clicking page numbers) and mobile-style infinite scroll.
 *
 * @param T The type of items in the data list
 * @param data The list of items for this page
 * @param page Current page number (0-based)
 * @param pageSize Number of items per page
 * @param totalItems Total number of items across all pages
 * @param totalPages Total number of pages
 * @param hasNextPage Whether there are more pages available
 * @param hasPreviousPage Whether there are previous pages available
 */
data class PagedData<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
) {
    companion object {
        /**
         * Creates a PagedData instance with calculated page information
         *
         * @param data The list of items for this page
         * @param page Current page number (0-based)
         * @param pageSize Number of items per page
         * @param totalItems Total number of items across all pages
         */
        fun <T> create(
            data: List<T>,
            page: Int,
            pageSize: Int,
            totalItems: Long,
        ): PagedData<T> {
            val totalPages = if (totalItems == 0L) 0 else ((totalItems + pageSize - 1) / pageSize).toInt()

            return PagedData(
                data = data,
                page = page,
                pageSize = pageSize,
                totalItems = totalItems,
                totalPages = totalPages,
                hasNextPage = page < totalPages - 1,
                hasPreviousPage = page > 0,
            )
        }

        /**
         * Creates an empty PagedData instance
         */
        fun <T> empty(pageSize: Int = 20): PagedData<T> =
            create(
                data = emptyList(),
                page = 0,
                pageSize = pageSize,
                totalItems = 0L,
            )
    }

    fun <R> map(transform: (T) -> R): PagedData<R> {
        return create(
            data = data.map { transform(it) },
            page = page,
            pageSize = pageSize,
            totalItems = totalItems
        )
    }
}
