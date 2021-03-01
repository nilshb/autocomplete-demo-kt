package autocompletekt

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AutoCompleteResource(private val autoCompleteService: AutoCompleteService) {

    @GetMapping("/address")
    fun address(@RequestParam q: String, @RequestParam(defaultValue = "20") size: Int): Completions {
        return autoCompleteService.autocomplete(q, size)
    }

}