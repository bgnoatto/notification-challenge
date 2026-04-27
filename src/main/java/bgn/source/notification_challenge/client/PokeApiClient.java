package bgn.source.notification_challenge.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PokeApiClient {

    private final RestClient restClient;

    public PokeApiClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://pokeapi.co/api/v2")
                .build();
    }

    public String getPokemonName(Integer pokemonId) {
        PokemonResponse response = restClient.get()
                .uri("/pokemon/{id}", pokemonId)
                .retrieve()
                .body(PokemonResponse.class);
        return response != null ? response.name() : "unknown";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PokemonResponse(String name) {}
}
