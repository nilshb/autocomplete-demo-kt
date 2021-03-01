package autocompletekt

import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.suggest.Suggest
import org.elasticsearch.search.suggest.SuggestBuilder
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import java.io.IOException

@Service
class AutoCompleteService(private val client: RestHighLevelClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val index = "addresses"
    private val fieldCompletion = "address.completion"


    fun autocomplete(q: String, size: Int): Completions {
        val searchRequest = SearchRequest(index)
        val suggestBuilder = CompletionSuggestionBuilder(fieldCompletion)
        suggestBuilder.size(size)
            .prefix(q, Fuzziness.ZERO)
            .skipDuplicates(true)
            .analyzer("standard")

        val sourceBuilder = SearchSourceBuilder()
        sourceBuilder.suggest(SuggestBuilder().addSuggestion(CompletionSuggestionBuilder.SUGGESTION_NAME, suggestBuilder))
        searchRequest.source(sourceBuilder)

        val response: SearchResponse

        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT)
            return findCompletions(response)
        } catch (ex: IOException) {
            log.error("Error in autocomplete search", ex)
            throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error in ES search")
        }
    }

    private fun findCompletions(response: SearchResponse): Completions {
        val completions = Completions()
        val suggest = response.suggest
        val suggestion = suggest.getSuggestion<Suggest.Suggestion<Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option>>>(
            CompletionSuggestionBuilder.SUGGESTION_NAME
        )
        for (entry in suggestion.entries) {
            for (option in entry.options) {
                completions.addresses.add(option.text.toString())
            }
        }
        return completions
    }
}




