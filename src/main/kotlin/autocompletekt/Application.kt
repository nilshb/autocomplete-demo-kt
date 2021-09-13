package autocompletekt

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class Application {

    // testing squash, commit 1
    // test squash, commit 2

    @Value("\${es.port}")
    val port = 9200

    @Value("\${es.host}")
    val host = "localhost"

    @Value("\${es.user}")
    val user: String = ""

    @Value("\${es.password}")
    val password: String = ""


    @Bean
    open fun client(): RestHighLevelClient {
        val credentialsProvider: CredentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(AuthScope.ANY, UsernamePasswordCredentials(user, password))
        val builder =
            RestClient.builder(HttpHost(host, port, "http")).setHttpClientConfigCallback { httpClientBuilder: HttpAsyncClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(
                    credentialsProvider
                )
            }

        return RestHighLevelClient(builder)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
