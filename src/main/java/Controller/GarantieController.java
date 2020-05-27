package Controller;

import Model.Garantie;


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
    public ResponseEntity<Garantie> créerGarantie(@RequestParam("nom") String nom, @RequestParam("montant") float montant, @RequestParam("description") String description,) {
        if (StringUtils.isBlank(nom)) {
            return ResponseEntity.status(400).build();
        }

        // affectation d'un id et persistance
        Garantie garantie = new Garantie();
        garantie.setId(fakeSeq.incrementAndGet());
        garantie.setNom(nom);
        garantie.setMontant(montant);
        garantie.setDescription(description);
        fakeDb.put(garantie.getId(), garantie);

        // URI de localisation de la ressource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(garantie.getId());

        // réponse 201 avec la localisation et la ressource créée
        return ResponseEntity.created(location).body(garantie);
    }

    @GetMapping("api/garantie/{id}")
    public ResponseEntity<Garantie> getGarantie(@PathVariable("id") @NotNull int id) {
        if (fakeDb.containsKey(id)) {
            return ResponseEntity.ok(fakeDb.get(id));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("api/garantie")
    public ResponseEntity<Collection<Garantie>> getGaranties() {
        return ResponseEntity.ok().body(fakeDb.values());
    }

    @DeleteMapping("api/garantie/{id}")
    public ResponseEntity<Garantie> deleteEquipe(@PathVariable("id") @NotNull int id) {
        if (fakeDb.containsKey(id)) {
            fakeDb.remove(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("api/garantie/{id}")
    public ResponseEntity<Garantie> putGarantie(@PathVariable("id") @NotNull int id, @RequestBody @Valid Garantie garantie) {
        if (fakeDb.containsKey(id)) {
            // si garantie existante, mise à jour
            garantie.setId(id);
            fakeDb.put(id, garantie);

            return ResponseEntity.ok().build();
        } else {
            // si garantie inexistante, création

            // affectation d'un id et persistance
            garantie.setId(fakeSeq.incrementAndGet());
            fakeDb.put(garantie.getId(), garantie);

            // URI de localisation de la ressource
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build(garantie.getId());

            // réponse 202 avec la localisation et la ressource créée
            return ResponseEntity.created(location).build();
        }
    }
}