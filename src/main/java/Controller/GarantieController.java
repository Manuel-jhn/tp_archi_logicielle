package Controller;

import Model.Garantie;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class GarantieController {

    // Simulation d'une persistence de notre ressource en attribut d'instance
    // pour simplifier le TP. Bien évidemment, on ne fait pas ça en temps normal
    // ... car c'est stateful ! Une base de données serait plus adaptée !
    private Map<Integer, Garantie> fakeDb = new ConcurrentHashMap<Integer, Garantie>();

    // Simulation d'un séquenceur pour générer l'identifiant des équipes
    private AtomicInteger fakeSeq = new AtomicInteger(0);


    @PostMapping("/garantie")
    public ResponseEntity<Equipe> postEquipe(@RequestParam("name") String name) {
        if (StringUtils.isBlank(name)) {
            return ResponseEntity.status(400).build();
        }

        // affectation d'un id et persistance
        Equipe equipe = new Equipe();
        equipe.setId(fakeSeq.incrementAndGet());
        equipe.setName(name);
        fakeDb.put(equipe.getId(), equipe);

        // URI de localisation de la ressource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(equipe.getId());

        // réponse 201 avec la localisation et la ressource créée
        return ResponseEntity.created(location).body(equipe);
    }

    @GetMapping("/equipes/{id}")
    public ResponseEntity<Equipe> getEquipe(@PathVariable("id") @NotNull int id) {
        if (fakeDb.containsKey(id)) {
            return ResponseEntity.ok(fakeDb.get(id));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/equipes")
    public ResponseEntity<Collection<Equipe>> getEquipes() {
        return ResponseEntity.ok().body(fakeDb.values());
    }

    @DeleteMapping("/equipes/{id}")
    public ResponseEntity<Equipe> deleteEquipe(@PathVariable("id") @NotNull int id) {
        if (fakeDb.containsKey(id)) {
            fakeDb.remove(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/equipes/{id}")
    public ResponseEntity<Equipe> putEquipe(@PathVariable("id") @NotNull int id, @RequestBody @Valid Equipe equipe) {
        if (fakeDb.containsKey(id)) {
            // cas équipe existante, mise à jour
            equipe.setId(id);
            fakeDb.put(id, equipe);

            return ResponseEntity.ok().build();
        } else {
            // cas équipe inexistante, création

            // affectation d'un id et persistance
            equipe.setId(fakeSeq.incrementAndGet());
            fakeDb.put(equipe.getId(), equipe);

            // URI de localisation de la ressource
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build(equipe.getId());

            // réponse 202 avec la localisation et la ressource créée
            return ResponseEntity.created(location).build();
        }
    }
}